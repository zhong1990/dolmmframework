package org.dol.framework.logging;

import org.dol.framework.util.StringUtil;
import org.dol.framework.util.SystemUtil;

import com.alibaba.fastjson.JSON;

public class NewLogger extends Logger {

    private static final String NULL_STR = "null";
    private static final String SPACE_STR = " ";

    public NewLogger() {
    }

    public NewLogger(Class<? extends Object> clazz) {
        super(clazz);
    }

    private static String buildStr(Object[] parameters, int startIndex) {
        if (parameters != null && parameters.length > startIndex) {
            Object param = parameters[startIndex];
            StringBuilder sb = new StringBuilder();
            sb.append(getValue(param));
            for (int i = (startIndex + 1); i < parameters.length; i++) {
                sb.append(SPACE_STR + getValue(parameters[i]));
            }
            return sb.toString();
        }
        return StringUtil.EMPTY_STRING;
    }

    private static Object getValue(Object param) {
        if (param == null) {
            return NULL_STR;
        } else if (param instanceof CharSequence || param instanceof Number || param instanceof Boolean) {
            return param;
        } else {
            return JSON.toJSONString(param);
        }
    }

    @Override
    public void info(String methodName, Object... extendsStrings) {
        LogEntry logEntry = buildLogEntry(methodName, LogType.GENERAL, LogLevel.NORMAL);
        if (extendsStrings != null && extendsStrings.length > 0) {
            logEntry.setMsg(extendsStrings[0]);
            if (extendsStrings.length > 1) {
                logEntry.setData(buildStr(extendsStrings, 1));
            }
        }
        realLog.info(getLogMessage(logEntry));
    }

    @Override
    public void error(String methodName, Object... extendsStrings) {
        LogEntry logEntry = buildLogEntry(methodName, LogType.GENERAL, LogLevel.ERROR);
        if (extendsStrings != null && extendsStrings.length > 0) {
            logEntry.setMsg(extendsStrings[0]);
            if (extendsStrings.length > 1) {
                logEntry.setData(buildStr(extendsStrings, 1));
            }
        }
        realLog.error(getLogMessage(logEntry));
    }

    @Override
    public void error(String methodName, Throwable e) {
        error(methodName, e.getMessage(), e);
    }

    // private String getLogMessage(byte logType, byte logFlag, String
    // methodName, Object... extensions) {
    // LogEntry data = buildLogEntry(methodName, logType, logFlag);
    // data.setMsg(buildStr(extensions));
    // return getLogMessage(data);
    // }

    @Override
    public void error(String methodName, String bizTitle, Throwable e) {
        LAST_EXEPTION.set(e);
        LogEntry logEntry = buildLogEntry(methodName, LogType.GENERAL, LogLevel.ERROR);
        logEntry.setMsg(bizTitle);
        logEntry.setData(getDetailMessage(e));
        realLog.error(getLogMessage(logEntry));
    }

    @Override
    public void warn(String methodName, Object... extendsStrings) {
        LogEntry logEntry = buildLogEntry(methodName, LogType.GENERAL, LogLevel.WARN);
        if (extendsStrings != null && extendsStrings.length > 0) {
            logEntry.setMsg(extendsStrings[0]);
            if (extendsStrings.length > 1) {
                logEntry.setData(buildStr(extendsStrings, 1));
            }
        }
        realLog.warn(getLogMessage(logEntry));
    }

    private LogEntry buildLogEntry(String methodName, byte logType, byte logLevel) {
        LogEntry data = getLogBaseData(logType, logLevel);
        data.setMd(methodPrefix + methodName);
        return data;
    }

    private LogEntry getLogBaseData(byte logType, byte logFlag) {
        LogEntry logData = new LogEntry(
                logType,
                logFlag,
                REQUEST_ID_LOCAL.get(),
                System.currentTimeMillis(),
                app,
                SystemUtil.getLocalHostIP());
        return logData;
    }

    @Override
    public long logApiRequest(String api, Object... parameters) {
        LogEntry logData = getLogBaseData(LogType.API_REQUEST, LogLevel.NORMAL);
        logData.setMd(api);
        logData.setParams(buildStr(parameters, 0));
        realLog.info(getLogMessage(logData));
        return logData.getTime();

    }

    @Override
    public void logHttpInvoke(
            String url,
            String method,
            String parameters,
            String clientIp,
            Integer status,
            long requestStartTime,
            Object data) {
        LogEntry logData = getLogBaseData(LogType.HTTP_INVOKE, LogLevel.NORMAL);
        logData.setMd(url);
        logData.setParams(parameters);
        logData.setInvoker(method);
        logData.setInvokerIP(clientIp);
        logData.setStatus(status);
        if (status >= 400) {
            logData.setLevel(LogLevel.ERROR);
        }
        // logData.setTime(requestStartTime);
        logData.setStartTime(requestStartTime);
        logData.setTime(requestStartTime);
        // logData.setMsg(message);
        logData.setData(data);
        realLog.info(getLogMessage(logData));

        clearRid();

    }

    private String getLogMessage(Object logData) {
        return JSON.toJSONString(logData);
    }

    @Override
    public void logApiResponse(String api, String parameters, Integer status, String message, long requestStartTime, Object result) {
        LogEntry logData = getLogBaseData(LogType.API_RETURN, LogLevel.NORMAL);
        logData.setMd(api);
        logData.setStartTime(requestStartTime);
        logData.setParams(parameters);
        logData.setStatus(status);
        logData.setMsg(message);
        if (isDebugEnabled() && result != null) {
            logData.setData(result);
        }
        realLog.info(getLogMessage(logData));
    }

    @Override
    public void logApiResponse(String api, Object[] parameters, Integer status, String message, long requestStartTime, Object result) {
        logApiResponse(api, buildStr(parameters, 0), status, message, requestStartTime, result);
    }

    @Override
    protected long doLogEntryIn(String invoker, String invokerIP, String api, Object... parameters) {

        LogEntry logData = getLogBaseData(LogType.IN, LogLevel.NORMAL);
        logData.setInvoker(invoker);
        logData.setInvokerIP(invokerIP);
        logData.setMd(api);
        logData.setParams(buildStr(parameters, 0));
        realLog.info(getLogMessage(logData));
        return logData.getTime();
    }

    @Override
    public void doLogEntryOut(String invoker, String invokerIP, String api, Integer status, String message, long start, Object data) {

        LogEntry logData = getLogBaseData(LogType.OUT, LogLevel.NORMAL);
        int useTime = (int) (logData.getTime() - start);
        logData.setUseTime(useTime);
        logData.setInvoker(invoker);
        logData.setInvokerIP(invokerIP);
        logData.setMd(api);
        logData.setStatus(status);
        logData.setMsg(message);
        if (isDebugEnabled() && data != null) {
            logData.setData(data);
        }
        realLog.info(getLogMessage(logData));

    }

    @Override
    public void metric(String name, String message, long start) {
        LogEntry logEntity = getLogBaseData(LogType.METRIC, LogLevel.NORMAL);
        logEntity.setMd(name);
        logEntity.setMsg(message);
        int useTime = (int) (logEntity.getTime() - start);
        logEntity.setUseTime(useTime);
        realLog.info(getLogMessage(logEntity));
    }
}
