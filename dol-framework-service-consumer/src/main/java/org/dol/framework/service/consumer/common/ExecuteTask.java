package org.dol.framework.service.consumer.common;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dol.framework.bizlogging.BizLogger;
import org.dol.framework.config.ConfigManager;
import org.dol.framework.http.HttpRequestUtil;
import org.dol.framework.http.RequestResult;
import org.dol.framework.job.ServiceJob;
import org.dol.framework.job.ServiceTask;
import org.dol.framework.logging.Logger;
import org.dol.framework.queue.JobMessage;
import org.dol.framework.queue.Producer;
import org.dol.framework.util.ApplicationContextUtil;
import org.dol.framework.util.FormatUtil;
import org.dol.framework.util.StringUtil;

public class ExecuteTask {

    public static final String INVOKER_NAME = "service-consumer";
    public static final String S_FROM = "from";
    public static final String S_STATUS = "status";
    public static final String S_MESSAGE = "message";
    public static final String S_DATA = "data";
    public static final String S_RESPONSE = "response";
    public static final String S_FAILED_QUEUE_PREFIX = "fail.";

    public static final Logger LOGGER = Logger.getLogger(ExecuteTask.class);

    private Producer serviceProducer;
    private String defaultFailedQueue;

    public Producer getServiceProducer() {
        return serviceProducer;
    }

    public void setServiceProducer(Producer serviceProducer) {
        this.serviceProducer = serviceProducer;
    }

    public String getDefaultFailedQueue() {
        return defaultFailedQueue;
    }

    public void setDefaultFailedQueue(String defaultFailedQueue) {
        this.defaultFailedQueue = defaultFailedQueue;
    }

    /**
     * 运行一个task work
     * 
     * @param jobMessage
     * @param destination
     * 
     * @author dolphin
     * @since JDK 1.7
     * @date 2015年7月22日 上午9:24:16
     */
    public void doWork(JobMessage jobMessage, String destination) {
        ServiceJob serviceJob = null;
        try {
            LOGGER.setRid(jobMessage.getRid());
            String json = jobMessage.getBody();
            serviceJob = JSON.parseObject(json, ServiceJob.class);
            List<ServiceTask> taskList = serviceJob.getTaskList();
            for (ServiceTask task : taskList) {
                if (task.isSuccess()) {
                    continue;
                }
                executeTask(task);
            }
            boolean hasFailedTask = hasFailedTask(taskList);
            if (hasFailedTask) {
                serviceJob.increaseFailedTimes();
                int delaySecondes = serviceJob.getDelaySeconds();
                jobMessage.setBody(JSON.toJSONString(serviceJob));
                if (delaySecondes > 0) {
                    jobMessage.setDelaySeconds(delaySecondes);
                    serviceProducer.produce(jobMessage, destination);
                } else {
                    jobMessage.setDelaySeconds(0);
                    serviceProducer.produce(jobMessage, S_FAILED_QUEUE_PREFIX + destination);
                }
            }
        } catch (Throwable e) {
            if (serviceJob != null && serviceJob.getFailedTimes() > 1) {
                LOGGER.warn("doWork", "执行Job发生未知错误", JSON.toJSONString(jobMessage), e);
            } else {
                LOGGER.error("doWork", "执行Job发生未知错误", JSON.toJSONString(jobMessage), e);
            }
        } finally {
            LOGGER.clearRid();
        }
    }

    /**
     * 检查是否有失败任务
     * 
     * @param taskList
     * @return boolean
     * @author dolphin
     * @since JDK 1.7
     * @date 2016年9月14日 下午3:19:33
     */
    private boolean hasFailedTask(List<ServiceTask> taskList) {
        boolean hasFailedTask = false;
        for (ServiceTask serviceTask : taskList) {
            if (serviceTask.isSuccess()) {
                continue;
            }
            serviceTask.increaseFailedTimes();
            hasFailedTask = true;
        }
        return hasFailedTask;
    }

    /**
     * 具体执行一个任务
     * 
     * @param task
     * @author dolphin
     * @since JDK 1.7
     * @date 2015年7月22日 上午9:23:48
     */
    private void executeTask(ServiceTask task) {
        JSONObject jsonObject = null;
        long startTime = System.currentTimeMillis();
        task.setLastInvokeTime(FormatUtil.formatSSS(new Date(startTime)));
        int returnStatus = 0;
        String returnMessage = null;
        String returnData = null;
        try {
            String serviceUrl = ConfigManager.getProperty(task.getServiceName());
            if (StringUtils.hasText(serviceUrl)) {
                task.getData().put(S_FROM, INVOKER_NAME);
                RequestResult result = HttpRequestUtil.post(serviceUrl, task.getData());
                try {
                    if (result.success()) {
                        try {
                            jsonObject = JSON.parseObject(result.getBody());
                            returnStatus = jsonObject.getInteger(S_STATUS);
                            returnMessage = jsonObject.getString(S_MESSAGE);
                            returnData = StringUtil.isBlank(returnData) ? jsonObject.getString(S_RESPONSE) : returnData;
                        } catch (Exception e) {
                            returnStatus = result.getStatus();
                            returnMessage = "返回消息不是JSON格式";
                            logInvokeError("executeTask", returnMessage, serviceUrl, task.getData(), result.getBody(), task.getFailedTimes());
                        }
                    } else {
                        returnStatus = result.getStatus();
                        returnMessage = result.getReasonPhrase();
                        returnData = StringUtil.EMPTY_STRING;
                        logInvokeError("executeTask", returnMessage, serviceUrl, task.getData(), result.getBody(), task.getFailedTimes());
                    }
                } catch (Exception e) {
                    returnStatus = -1;
                    String detailErrorMessage = LOGGER.getDetailMessage(e);
                    returnMessage = "调用服务发生系统异常:" + detailErrorMessage;
                    returnData = result.getBody();
                    logInvokeError("executeTask", "调用服务发生系统异常", serviceUrl, task.getData(), detailErrorMessage, task.getFailedTimes());
                }

            } else {
                returnStatus = -1;
                returnMessage = "没有为" + task.getServiceName() + "配置服务地址";
                returnData = StringUtil.EMPTY_STRING;
                logInvokeError("executeTask", returnMessage, task.getServiceName(), task.getData(), returnMessage, task.getFailedTimes());
            }

        } catch (Exception e) {
            returnStatus = -1;
            String detailErrorMsg = LOGGER.getDetailMessage(e);
            returnMessage = "调用服务发生系统异常:" + detailErrorMsg;
            returnData = StringUtil.EMPTY_STRING;
            logInvokeError("executeTask", "调用服务发生系统异常", task.getServiceName(), task.getData(), detailErrorMsg, task.getFailedTimes());
        } finally {
            task.setReturnData(returnData);
            task.setReturnMessage(returnMessage);
            task.updateReturnStatus(returnStatus);
            // logInvoke(task.getApi(), task.getMessage(), task.getFrom(),
            // task.getSign(), returnStatus, returnMessage, returnData,
            // requestTime);
            logApiResponse(task.getApi(), task.getMessage(), returnStatus, returnMessage, startTime, task.getReturnData());
        }
    }

    private static void logInvokeError(
            String methodName,
            String message,
            String url,
            Object param,
            Object data,
            int failedTimes) {
        Map<String, Object> body = new HashMap<String, Object>(2);
        body.put("服务地址", url);
        body.put("参数", param);
        body.put("返回值", data);
        if (failedTimes > 0) {
            LOGGER.warn(methodName, message, body);
        } else {
            LOGGER.error(methodName, message, body);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Log4jConfigurer.initLogging("classpath:log4j.xml");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "dolphin");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("adsfaf", "dsafafd");
        logInvokeError("test", "这是一条测试消息", "http://api.dol.com/index.php", map, data, 1);
    }

    private void logApiResponse(String api, String parameters, int status, String message, long requestStartTime, Object data) {
        LOGGER.logApiResponse(api, parameters, status, message, requestStartTime, data);
    }

    // private void logInvoke(String api, String message, String from, String
    // sign, int status, String returnMessage, String returnData, long
    // requestTime) {
    // try {
    // BizLogger bizLogger = ApplicationContextUtil.getBean("bizLogger");
    // bizLogger.logInvoke(api, message, from, sign, requestTime, status,
    // returnMessage, returnData, StringUtil.EMPTY_STRING,
    // StringUtil.EMPTY_STRING, INVOKER_NAME);
    // } catch (Exception e) {
    // LOGGER.error("logInvoke", "记录调用日志失败", e);
    // }
    // }
}
