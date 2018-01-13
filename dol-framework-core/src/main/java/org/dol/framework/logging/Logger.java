package org.dol.framework.logging;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dol.framework.util.StringUtil;
import org.springframework.core.io.support.PropertiesLoaderUtils;

;

public abstract class Logger {

    public static final String REQUEST_ID_KEY = "rid";
    protected static final RequestIdThreadLocal REQUEST_ID_LOCAL = new RequestIdThreadLocal();
    protected static final ThreadLocal<Throwable> LAST_EXEPTION = new ThreadLocal<Throwable>();
    protected static String loggerImplClass;
    protected static String app;

    static {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties("local.properties");
            app = properties.getProperty("appName");
            loggerImplClass = properties.getProperty("logger.impl.class");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected Log realLog = null;
    protected String className = null;
    protected String methodPrefix = null;

    public Logger() {

    }

    public Logger(Class<? extends Object> clazz) {
        init(clazz);
    }

    public static Logger getLogger(Class<? extends Object> clazz) {
        Logger logger = null;

        if (StringUtil.isBlank(loggerImplClass)) {
            logger = new WatchLogger(clazz);
            return logger;
        }

        try {
            logger = (Logger) Logger.class.getClassLoader().loadClass(loggerImplClass).newInstance();
            logger.init(clazz);
        } catch (Exception ex) {
            logger = new WatchLogger(clazz);
        }

        return logger;
    }

    public static Logger getNewLogger(Class<? extends Object> clazz) {
        Logger logger = new NewLogger(clazz);
        return logger;
    }

    private void init(Class<? extends Object> clazz) {
        className = clazz.getName();
        methodPrefix = className + ".";
        realLog = LogFactory.getLog(clazz);
    }

    public abstract void info(String methodName, Object... extentions);

    public abstract void error(String methodName, Object... extentions);

    public abstract void warn(String methodName, Object... extentions);

    public abstract void error(String methodName, Throwable e);

    public abstract void error(String methodName, String bizTitle, Throwable e);

    /**
     * 记录API调用请求日志
     *
     * @param serviceName 调用的API所属系统名称
     * @param api         调用的API名称
     * @param parameters
     * @author dolphin
     * @date 2016年5月19日 下午8:00:54
     * @since JDK 1.7
     */
    public abstract long logApiRequest(String api, Object... parameters);

    /**
     * @param api              API名称
     * @param parameters       调用参数
     * @param status           返回状态码
     * @param message          返回消息
     * @param requestStartTime 请求开始时间
     * @param result           返回数据，注：只有启动debug模式才记录
     */
    public abstract void logApiResponse(String api, Object[] parameters, Integer status, String message, long requestStartTime, Object result);

    /**
     * @param api
     * @param parameters
     * @param status
     * @param message
     * @param requestStartTime
     * @param result
     */
    public abstract void logApiResponse(String api, String parameters, Integer status, String message, long requestStartTime, Object result);

    public abstract void logHttpInvoke(
            String url,
            String method,
            String parameters,
            String clientIp,
            Integer status,
            long requestStartTime,
            Object data);

    /**
     * 入口日志
     *
     * @param requestId  请求ID
     * @param invoker    调用方名称
     * @param invokerIP  调用方IP
     * @param api        被调用的API
     * @param parameters 调用参数
     * @author dolphin
     * @date 2016年5月19日 下午2:35:26
     * @since JDK 1.7
     */
    public long logIn(String requestId, String invoker, String invokerIP, String api, Object... parameters) {
        if (StringUtil.isNotBlank(requestId)) {
            REQUEST_ID_LOCAL.set(requestId);
        }
        return doLogEntryIn(invoker, invokerIP, api, parameters);
    }

    protected abstract long doLogEntryIn(String invoker, String invokerIP, String api, Object... parameters);

    /**
     * 出口日志
     *
     * @param invoker   调用方名称
     * @param invokerIP 调用方IP
     * @param api       被调用的API名称
     * @param status    调用返回状态码
     * @param message   调用返回消息
     * @param data      调用返回结果
     * @author dolphin
     * @date 2016年5月19日 下午2:36:18
     * @since JDK 1.7
     */
    public void logOut(String invoker, String invokerIP, String api, Integer status, String message, long start, Object data) {
        doLogEntryOut(invoker, invokerIP, api, status, message, start, data);
        REQUEST_ID_LOCAL.remove();
    }

    public abstract void metric(String name, String message, long start);

    protected abstract void doLogEntryOut(String invoker, String invokerIP, String api, Integer status, String message, long start, Object data);

    public void debug(Object arg0) {
        if (isDebugEnabled()) {
            realLog.debug(arg0);
        }

    }

    public boolean isDebugEnabled() {
        return realLog.isDebugEnabled();
    }

    public String getDetailMessage(Throwable e) {
        StringBuilder sBuilder = new StringBuilder(e.toString());
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sBuilder.append(" \n " + stackTraceElement.toString());
        }
        return sBuilder.toString();
    }

    public Throwable getLastException() {
        Throwable e = LAST_EXEPTION.get();
        if (e != null) {
            LAST_EXEPTION.remove();
        }
        return e;
    }

    public String getRid() {
        return REQUEST_ID_LOCAL.get();
    }

    public void setRid(String rid) {
        REQUEST_ID_LOCAL.set(rid);
    }

    public void clearRid() {
        REQUEST_ID_LOCAL.remove();
    }
}
