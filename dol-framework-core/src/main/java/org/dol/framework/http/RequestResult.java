package org.dol.framework.http;

import java.nio.charset.Charset;
import java.util.Map;

public class RequestResult {


    private Map<String, String> headers;
    private String reasonPhrase;
    private int status;
    private byte[] body;
    private String bodyString;
    private Charset charset;

    public RequestResult(int statusCode,
                         String reasonPhrase,
                         Map<String, String> headers,
                         byte[] bytes,
                         Charset charset) {
        this.status = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = bytes;
        this.charset = charset;
    }

    public String getBodyString() {
        if (bodyString == null && body != null) {
            bodyString = new String(body, charset);
        }
        return bodyString;
    }

    public int getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public boolean success() {
        return status == 200 || status == 304;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
