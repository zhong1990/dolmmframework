package org.dol.framework.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.Map;

public class DefaultHttpClient {

    private final static HttpClientUtil DEFAULT_CLIENT = new HttpClientUtil();


    public static RequestResult post(String url, String data) throws IOException {
        return DEFAULT_CLIENT.postString(url,
                null, data,
                null,
                ContentType.APPLICATION_FORM_URLENCODED);
    }

    public static RequestResult get(String url, Map<String, Object> parameters) throws IOException {
        return DEFAULT_CLIENT.get(url, parameters);
    }

    public static RequestResult get(String serviceUrl) throws IOException {
        return get(serviceUrl, null);
    }

    public static RequestResult postJSON(String url, Object data) throws IOException {

        return DEFAULT_CLIENT.postJSON(url, JSON.toJSONString(data));
    }
}
