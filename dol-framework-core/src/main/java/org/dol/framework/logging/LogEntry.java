package org.dol.framework.logging;

import java.io.Serializable;

public class LogEntry implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 消息树编号
     */
    private String rid;
    /**
     * 日志时间
     */
    private long time;
    /**
     * 日志类型：{@link LogType }
     */
    private byte type;
    /**
     * 日志级别，{@link LogType}
     */
    private byte level;

    /**
     * 当前应用程序名称
     */
    private String app;
    /**
     * 当前应用程序IP
     */
    private String appIP;
    /**
     * 记录日志的方法
     */
    private String md;
    /**
     * 日志消息
     */
    private Object msg;

    /**
     * 调用方法的参数
     */
    private Object params;
    /**
     * 返回状态
     */
    private Integer status;
    /**
     * 返回数据
     */
    private Object data;
    /**
     * 调用者名称
     */
    private String invoker;
    /**
     * 调用者 IP
     */
    private String invokerIP;

    /**
     * 服务名称
     */
    private String service;

    /**
     * 用时
     */
    private Integer useTime;

    public LogEntry(byte logType, byte logLevel, String rid, long currentTimeMillis, String app, String appIP) {
        this.setType(logType);
        this.setLevel(logLevel);
        this.setRid(rid);
        this.setTime(currentTimeMillis);
        this.setApp(app);
        this.setAppIP(appIP);

    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAppIP() {
        return appIP;
    }

    public void setAppIP(String appIP) {
        this.appIP = appIP;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getInvoker() {
        return invoker;
    }

    public void setInvoker(String invoker) {
        this.invoker = invoker;
    }

    public String getInvokerIP() {
        return invokerIP;
    }

    public void setInvokerIP(String invokerIP) {
        this.invokerIP = invokerIP;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Integer getUseTime() {
        return useTime;
    }

    public void setUseTime(Integer useTime) {
        this.useTime = useTime;
    }

    public void setStartTime(long start) {
        this.useTime = (int) (this.time - start);
    }

}
