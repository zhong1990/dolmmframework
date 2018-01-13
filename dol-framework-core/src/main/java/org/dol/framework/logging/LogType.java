package org.dol.framework.logging;

public interface LogType {
    /**
     * @Fields GENERAL : 正常日志
     */
    public static final byte GENERAL = 1;
    /**
     * @Fields BIZ_DATA : 业务日志
     */
    public static final byte BIZ_DATA = 2;

    /**
     * @Fields IN : 入口日志
     */
    public static final byte IN = 3;
    /**
     * @Fields OUT : 出口日志
     */
    public static final byte OUT = 4;
    /**
     * @Fields API_REQUEST : API调用请求日志
     */
    public static final byte API_REQUEST = 5;
    /**
     * @Fields API_RETURN : API调用请求返回结果日志
     */
    public static final byte API_RETURN = 6;

    public static final byte METRIC = 7;

    public static final byte HTTP_INVOKE = 8;

}
