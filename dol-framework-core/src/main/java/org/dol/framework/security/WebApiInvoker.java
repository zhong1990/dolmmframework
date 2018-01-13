/**
 * dol-framework-core
 * WebApiInvoker.java
 * org.dol.framework.security
 * TODO
 *
 * @author dolphin
 * @date 2016年8月3日 下午2:42:21
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.security;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectTimeoutException;
import org.dol.framework.http.DefaultHttpClient;
import org.dol.framework.http.RequestResult;
import org.dol.framework.util.DateUtil;

import com.alibaba.fastjson.JSON;

/**
 * ClassName:WebApiInvoker <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年8月3日 下午2:42:21 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class WebApiInvoker {
    public static final String FROM_KEY = "from";
    public static final String SIGN_KEY = "signKey";
    public static final String PARAMS_KEY = "params";
    private static final Log logger = LogFactory.getLog(ApiInvoker.class);
    private String apiKey;
    private String from;

    public String invokePostApi(
            final String serviceUrl,
            final Object data) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String params = JSON.toJSONString(data);
        String sign = DigestUtils.md5Hex(params + getApiKey());
        map.put(FROM_KEY, getFrom());
        map.put(SIGN_KEY, sign);
        map.put(PARAMS_KEY, params);
        return doInvokePostApi(serviceUrl, map);
    }

    private String doInvokePostApi(
            final String serviceUrl,
            final Object data) throws ConnectTimeoutException, SocketTimeoutException,
            Exception {
        String result = null;
        String startTime = DateUtil.getDate("yyyyMMdd HH:mm:ss SSS");
        try {
            RequestResult requestResult = null;
            requestResult = DefaultHttpClient.postJSON(serviceUrl, data);
            if (requestResult.success()) {
                result = requestResult.getBodyString();
            } else {
                throw new Exception("访问失败，" + requestResult.getReasonPhrase());
            }
        } catch (ConnectTimeoutException ex) {
            logger.error("连接第三方接口超时:", ex);
            throw ex;
        } catch (SocketTimeoutException ex) {
            logger.error("从第三方系统获取响应接口数据超时:", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("ApiInvoker:ToString:", ex);
            throw ex;
        } finally {
            if (logger.isDebugEnabled()) {
                String endTime = DateUtil.getDate("yyyyMMdd HH:mm:ss SSS");
                logger.debug("start invoke api [" + serviceUrl + "] at Time " + startTime + " ,end at " + endTime);
            }
        }
        return result;
    }

    public String invokeGetApi(String serviceUrl, Map<String, Object> parameters) throws Exception {
        String result = null;
        try {
            RequestResult requestResult = DefaultHttpClient.get(serviceUrl, parameters);
            if (requestResult.success()) {
                result = requestResult.getBodyString();
            } else {
                throw new Exception("访问失败，" + requestResult.getReasonPhrase());
            }
            if (logger.isDebugEnabled()) {
                logger.warn("Invoke In Time:" + DateUtil.getDate("yyyyMMdd HH:mm:ss") + " :END****" + serviceUrl + "***" + parameters.toString() + "***"
                        + result);
            }
        } catch (ConnectTimeoutException ex) {
            logger.error("连接第三方接口超时:" + ex.toString());
            throw ex;
        } catch (SocketTimeoutException ex) {
            logger.error("从第三方系统获取响应接口数据超时:", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("ApiInvoker:ToString:", ex);
            throw ex;
        }
        return result;
    }

    /**
     * apiKey.
     *
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * apiKey.
     *
     * @param apiKey
     *            the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * from.
     *
     * @param from
     *            the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }
}
