package org.dol.framework.http;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.MapUtil;
import org.dol.framework.util.StringUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by dolphin on 2017/9/7.
 */
public class HttpClientUtil implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = Logger.getLogger(HttpClientUtil.class);
    private final HttpClientBuilder customBuilder = HttpClients.custom();
    private final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
    private volatile boolean hasConfigChanges = false;
    private CloseableHttpClient httpClient;
    private Charset charset = StandardCharsets.UTF_8;
    private boolean tcpNoDelay = true;
    private boolean soKeepAlive = true;
    private ContentType jsonContentType = ContentType.APPLICATION_JSON;
    private ContentType xmlContentType = ContentType.create(ContentType.TEXT_XML.getMimeType(), Consts.UTF_8);

    public HttpClientUtil() {

    }

    public HttpClientUtil(Integer maxConnTotal,
                          Integer maxConnPerRoute) {

        this(
                maxConnTotal,
                maxConnPerRoute,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

    }

    public HttpClientUtil(Integer maxConnTotal,
                          Integer maxConnPerRoute,
                          Long connectionTimeToLive,
                          Boolean tcpNoDelay,
                          Boolean soKeepAlive,
                          String userAgent,
                          Charset charset,
                          SSLContext sslContext,
                          HostnameVerifier hostnameVerifier) {

        if (connectionTimeToLive != null) {
            this.setConnectionTimeToLive(connectionTimeToLive);
        }
        if (maxConnTotal != null) {
            this.setMaxConnTotal(maxConnTotal);
        }
        if (maxConnPerRoute != null) {
            this.setMaxConnPerRoute(maxConnPerRoute);
        }
        if (tcpNoDelay != null) {
            this.tcpNoDelay = tcpNoDelay;
        }
        if (soKeepAlive != null) {
            this.soKeepAlive = soKeepAlive;
        }
        if (userAgent != null) {
            this.setUserAgent(userAgent);
        }
        if (charset != null) {
            this.charset = charset;
        }
        if (sslContext != null) {
            this.setSSLContext(sslContext);
        }
        if (hostnameVerifier != null) {
            this.setSSLHostnameVerifier(hostnameVerifier);
        }
    }

    public URI buildFullUrl(String url, Map<String, ?> query) {
        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(url);
            if (MapUtil.isNullOrEmpty(query)) {
                return uriBuilder.build();
            }
            Set<? extends Map.Entry<String, ?>> entries = query.entrySet();
            for (Map.Entry<String, ?> entry : entries) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
            }
            return uriBuilder.build();
        } catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }


    public String encode(Object value, Charset charset) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value.toString(), charset.name());
        } catch (Exception e) {
            return null;
        }
    }

    public HttpClientUtil setDefaultHeaders(final Map<String, String> defaultHeaders) {
        this.customBuilder.setDefaultHeaders(map2Headers(defaultHeaders));
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setJsonContentType(ContentType jsonContentType) {
        this.jsonContentType = jsonContentType;
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setXmlContentType(ContentType xmlContentType) {
        this.xmlContentType = xmlContentType;
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setConnectionTimeToLive(final long connTimeToLive) {
        customBuilder.setConnectionTimeToLive(connTimeToLive, TimeUnit.MILLISECONDS);
        return this;
    }

    public HttpClientUtil setMaxConnTotal(int maxConnTotal) {
        customBuilder.setMaxConnTotal(maxConnTotal);
        return this;
    }

    public HttpClientUtil setMaxConnPerRoute(int maxConnPerRoute) {
        customBuilder.setMaxConnPerRoute(maxConnPerRoute);
        return this;
    }

    public HttpClientUtil setUserAgent(String userAgent) {
        customBuilder.setUserAgent(userAgent);
        return this;
    }

    public HttpClientUtil setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public void close() {

        HttpClientUtils.closeQuietly(httpClient);
    }

    public synchronized void init() {
        if (httpClient != null) {
            if (!hasConfigChanges) {
                return;
            }
            HttpClientUtils.closeQuietly(httpClient);
            httpClient = null;
        }
        hasConfigChanges = false;
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setCharset(StandardCharsets.UTF_8)
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(tcpNoDelay)
                .setSoKeepAlive(soKeepAlive)
                .build();

        RequestConfig requestConfig = requestConfigBuilder.build();

        CloseableHttpClient httpClient1 =
                customBuilder
                        .setDefaultSocketConfig(socketConfig)
                        .setDefaultRequestConfig(requestConfig)
                        .setDefaultConnectionConfig(connectionConfig)
                        .build();
        if (!charset.equals(StandardCharsets.UTF_8)) {
            jsonContentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), charset);
            xmlContentType = ContentType.create(ContentType.APPLICATION_XML.getMimeType(), charset);
        }
        httpClient = httpClient1;
    }

    public RequestResult postForm(String url, Map<String, ?> form) throws IOException {
        return postForm(url, null, form, null);
    }

    public RequestResult postForm(String url,
                                  Map<String, ?> query,
                                  Map<String, ?> form,
                                  Map<String, ?> headers) throws IOException {
        HttpPost httpPost = new HttpPost(buildFullUrl(url, query));
        addHeaders(headers, httpPost);
        if (MapUtil.isNotNullAndEmpty(form)) {
            List<NameValuePair> nameValuePairs = toNameValuePairs(form);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, charset);
            httpPost.setEntity(urlEncodedFormEntity);
        }
        return execute(httpPost, charset);
    }

    public RequestResult get(String url) throws IOException {
        return get(url, null);
    }

    public RequestResult get(String url,
                             Map<String, ?> query,
                             Map<String, ?> headers) throws IOException {
        URI fullUrl = buildFullUrl(url, query);
        HttpGet httpGet = new HttpGet(fullUrl);
        addHeaders(headers, httpGet);
        return execute(httpGet, charset);
    }

    public RequestResult get(String url, Map<String, ?> parameters) throws IOException {
        return get(url, parameters, null);
    }

    public RequestResult execute(HttpUriRequest request) throws IOException {
        return execute(request, charset);

    }

    protected RequestResult execute(HttpUriRequest request, Charset defaultCharset) throws IOException {
        CloseableHttpResponse response = null;
        try {
            request.setHeader(Logger.REQUEST_ID_KEY, LOGGER.getRid());
            response = httpClient.execute(request);
            return new RequestResult(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    headers2Map(response.getAllHeaders()),
                    EntityUtils.toByteArray(response.getEntity()),
                    resolveEntityCharset(response.getEntity())
            );
        } catch (NullPointerException ex) {
            if (httpClient == null) {
                init();
                return execute(request, defaultCharset);
            }
            throw ex;
        } catch (IOException ex) {
            Map<String, Object> map = new HashMap<>(10);
            map.put("url", request.getURI().toString());
            if (request instanceof HttpPost) {
                HttpPost httpPost = (HttpPost) request;
                String body = EntityUtils.toString(httpPost.getEntity(), defaultCharset);
                map.put("body", body);
            }
            LOGGER.error("execute", map, ex);
            throw ex;
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    private Charset resolveEntityCharset(HttpEntity entity) {
        ContentType contentType = ContentType.get(entity);
        if (contentType == null || contentType.getCharset() == null) {
            return charset;
        }
        return contentType.getCharset();
    }

    private Map<String, String> headers2Map(Header[] headers) {
        Map<String, String> map = new HashMap<>(headers.length);
        for (Header header : headers) {
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    public RequestResult postJSON(String url,
                                  String json) throws IOException {
        return postJSON(url, null, json, null);
    }

    public RequestResult postJSON(String url,
                                  Map<String, ?> query,
                                  String json,
                                  Map<String, ?> headers) throws IOException {
        return postString(url, query, json, headers, jsonContentType);
    }

    public RequestResult postXml(String url,
                                 String xml) throws IOException {
        return postString(url, null, xml, null, xmlContentType);
    }

    public RequestResult postXml(String url,
                                 Map<String, ?> query,
                                 String xml,
                                 Map<String, String> headers) throws IOException {
        return postString(url, query, xml, headers, xmlContentType);
    }

    public RequestResult postString(String url,
                                    Map<String, ?> query,
                                    String data,
                                    Map<String, ?> headers,
                                    ContentType contentType) throws IOException {

        HttpPost httpPost = new HttpPost(buildFullUrl(url, query));
        addHeaders(headers, httpPost);
        if (StringUtil.isNotBlank(data)) {
            httpPost.setEntity(new StringEntity(data, contentType));
        }
        return execute(httpPost, contentType.getCharset());
    }

    public RequestResult postBytes(String url, byte[] body)
            throws IOException {
        return postBytes(url, null, body, null);
    }

    public RequestResult postBytes(String url,
                                   Map<String, ?> query,
                                   byte[] body,
                                   Map<String, ?> headers)
            throws IOException {

        HttpPost httpPost = new HttpPost(buildFullUrl(url, query));
        addHeaders(headers, httpPost);
        if (ArrayUtils.isNotEmpty(body)) {
            HttpEntity entity = new ByteArrayEntity(body, ContentType.DEFAULT_BINARY);
            httpPost.setEntity(entity);
        }
        return execute(httpPost, charset);
    }

    protected void addHeaders(Map<String, ?> headers, HttpUriRequest httpRequest) {
        if (MapUtil.isNullOrEmpty(headers)) {
            return;
        }
        Set<? extends Map.Entry<String, ?>> entries = headers.entrySet();
        for (Map.Entry<String, ?> header : entries) {
            if (header.getValue() == null) {
                httpRequest.removeHeaders(header.getKey());
            } else {
                httpRequest.addHeader(header.getKey(), header.getValue().toString());
            }
        }
    }

    protected Collection<? extends Header> map2Headers(Map<String, ?> headers) {
        if (MapUtil.isNullOrEmpty(headers)) {
            return null;
        }
        Set<? extends Map.Entry<String, ?>> entries = headers.entrySet();
        List<Header> headerList = new ArrayList<Header>(headers.size());
        for (Map.Entry<String, ?> header : entries) {
            Header header1 = new BasicHeader(header.getKey(), header.getValue() == null ? "" : header.getValue().toString());
            headerList.add(header1);
        }
        return headerList;
    }

    private List<NameValuePair> toNameValuePairs(Map<String, ?> data) {
        Set<? extends Map.Entry<String, ?>> entries = data.entrySet();
        List<NameValuePair> nameValuePairs = new ArrayList<>(entries.size());
        for (Map.Entry<String, ?> entry : entries) {
            if (entry.getValue() == null) {
                continue;
            }
            new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        return nameValuePairs;
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public HttpClientUtil setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
        customBuilder.setSSLHostnameVerifier(hostnameVerifier);
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setSSLContext(SSLContext sslContext) {
        this.customBuilder.setSSLContext(sslContext);
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setConnectionManagerShared(final boolean shared) {
        this.customBuilder.setConnectionManagerShared(shared);
        hasConfigChanges = true;
        return this;
    }

    public HttpClientUtil setProxy(final HttpHost proxy) {
        this.customBuilder.setProxy(proxy);
        hasConfigChanges = true;
        return this;
    }

    /**
     * 设置从connection Manager中获取连接的等待时间，默认-1一直等待
     *
     * @param connectionRequestTimeout
     * @return
     */
    public HttpClientUtil setConnectionRequestTimeout(final int connectionRequestTimeout) {
        requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    /**
     * 设置与服务器建立连接的等待时间，
     * 设置为0表示无限等待
     * 默认-1，系统默认超时时间
     *
     * @param connectTimeout
     * @return
     */
    public HttpClientUtil setConnectTimeout(final int connectTimeout) {
        requestConfigBuilder.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * SO_TIMEOUT,两个连接之间的数据交互时间
     * 默认-1，与操作系统一致
     * 设置成0表示无限等待
     *
     * @param socketTimeout
     * @return
     */
    public HttpClientUtil setSocketTimeout(final int socketTimeout) {
        requestConfigBuilder.setSocketTimeout(socketTimeout);
        return this;
    }


}
