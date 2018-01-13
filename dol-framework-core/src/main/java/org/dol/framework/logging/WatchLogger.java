package org.dol.framework.logging;

import com.alibaba.fastjson.JSON;

public class WatchLogger extends Logger {

    public WatchLogger() {
    }

    public WatchLogger(Class<? extends Object> clazz) {
        super(clazz);
    }

    @Override
    public void info(String methodName, Object... extendsStrings) {
        SysLogEntry logEntry = new SysLogEntry(3, className + "." + methodName, LogLevel.NORMAL, extendsStrings);
        realLog.info(getLogMessage(logEntry));
    }

    @Override
    public void error(String methodName, Object... extendsStrings) {
        SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogLevel.ERROR, extendsStrings);
        realLog.error(getLogMessage(logEntry));
    }

    @Override
    public void error(String methodName, Throwable e) {
        LAST_EXEPTION.set(e);
        SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogLevel.ERROR, getDetailMessage(e));
        realLog.error(getLogMessage(logEntry));
    }

    @Override
    public void error(String methodName, String bizTitle, Throwable e) {
        LAST_EXEPTION.set(e);
        SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogLevel.ERROR, bizTitle, getDetailMessage(e));
        realLog.error(getLogMessage(logEntry));
    }

    @Override
    public void warn(String methodName, Object... extendsStrings) {
        SysLogEntry logEntry = new SysLogEntry(2, className + "." + methodName, LogLevel.WARN, extendsStrings);
        realLog.warn(getLogMessage(logEntry));
    }

    private String getLogMessage(SysLogEntry logEntry) {
        logEntry.setRid(REQUEST_ID_LOCAL.get());
        String logBody = JSON.toJSONString(logEntry);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dol-" + logEntry.getType() + " " + logBody);
        return stringBuilder.toString();
    }

    @Override
    public long logApiRequest(String api, Object... parameters) {

        SysLogEntry logEntry = new SysLogEntry(2, api, LogLevel.WARN, parameters);
        realLog.warn(getLogMessage(logEntry));
        return System.currentTimeMillis();

    }

    @Override
    public void logApiResponse(String api, Object[] parameters, Integer status, String message, long requestStartTime, Object result) {
        SysLogEntry logEntry = null;
        if (isDebugEnabled()) {
            logEntry = new SysLogEntry(LogLevel.NORMAL, api, LogLevel.WARN, status, message, result);
        } else {
            logEntry = new SysLogEntry(LogLevel.NORMAL, api, LogLevel.WARN, status, message);
        }
        realLog.warn(getLogMessage(logEntry));
    }

    @Override
    public void logHttpInvoke(
            String url,
            String method, String parameters, String clientIp, Integer status, long requestStartTime, Object data) {
        SysLogEntry logEntry = new SysLogEntry(LogLevel.NORMAL, url, LogLevel.WARN, status, parameters);
        realLog.warn(getLogMessage(logEntry));
    }

    @Override
    public void logApiResponse(String api, String parameters, Integer status, String message, long requestStartTime, Object result) {
        SysLogEntry logEntry = null;
        if (isDebugEnabled()) {
            logEntry = new SysLogEntry(LogLevel.NORMAL, api, LogLevel.WARN, status, message, result);
        } else {
            logEntry = new SysLogEntry(LogLevel.NORMAL, api, LogLevel.WARN, status, message);
        }
        realLog.warn(getLogMessage(logEntry));
    }

    @Override
    public long doLogEntryIn(String invoker, String invokerIP, String api, Object... parameters) {

        SysLogEntry logEntry = new SysLogEntry(2, api, LogLevel.WARN, parameters);
        realLog.warn(getLogMessage(logEntry));
        return System.currentTimeMillis();
    }

    @Override
    protected void doLogEntryOut(String invoker, String invokerIP, String api, Integer status, String message, long start, Object data) {

        SysLogEntry logEntry = null;
        long useTime = System.currentTimeMillis() - start;
        if (isDebugEnabled()) {
            logEntry = new SysLogEntry(2, api, LogLevel.WARN, status, message, data, useTime);
        } else {
            logEntry = new SysLogEntry(2, api, LogLevel.WARN, status, message, useTime);
        }
        realLog.warn(getLogMessage(logEntry));

    }

    @Override
    public void metric(String name, String message, long start) {
        long useTime = (System.currentTimeMillis() - start);
        SysLogEntry logEntry = new SysLogEntry(LogLevel.NORMAL, message, LogType.GENERAL, useTime);
        realLog.info(getLogMessage(logEntry));
    }

}
