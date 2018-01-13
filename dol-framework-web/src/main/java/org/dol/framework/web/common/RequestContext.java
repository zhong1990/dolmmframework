package org.dol.framework.web.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * must register the @see FirstFilter into web.xml
 * Created by dolphin on 2017/8/11.
 */
public class RequestContext implements Filter {

    private static final ThreadLocal<HttpServletRequest> SERVLET_REQUEST_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> SERVLET_RESPONSE_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> CONTEXT_VARIABLES = new ThreadLocal<>();

    public static void set(ServletRequest request, ServletResponse response) {
        SERVLET_REQUEST_THREAD_LOCAL.set((HttpServletRequest) request);
        SERVLET_RESPONSE_THREAD_LOCAL.set((HttpServletResponse) response);
    }

    public static void remove() {
        SERVLET_REQUEST_THREAD_LOCAL.remove();
        SERVLET_RESPONSE_THREAD_LOCAL.remove();
        CONTEXT_VARIABLES.remove();
    }

    public static HttpServletRequest currentRequest() {
        return SERVLET_REQUEST_THREAD_LOCAL.get();
    }

    public static HttpServletResponse currentResponse() {
        return SERVLET_RESPONSE_THREAD_LOCAL.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T) CONTEXT_VARIABLES.get().get(name);
    }

    public static void set(String name, Object value) {
        CONTEXT_VARIABLES.get().put(name, value);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        set(request, response);
        CONTEXT_VARIABLES.set(new HashMap<String, Object>());
        try {
            chain.doFilter(request, response);
        } finally {
            remove();
        }
    }

    @Override
    public void destroy() {

    }
}
