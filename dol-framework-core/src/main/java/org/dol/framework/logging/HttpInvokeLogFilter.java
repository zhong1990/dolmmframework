package org.dol.framework.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.dol.framework.constrants.Chars;
import org.springframework.web.util.WebUtils;

public class HttpInvokeLogFilter implements Filter {

    public static final String INIT_PARAM_DELIMITERS = ",; \t\n";
    private static final String[] DEFAULT_LOG_RESOURCES = {"HTML", "HTM", "SHTML", "JSP", "DO", "ACTION"};
    private final Logger LOGGER = Logger.getLogger(HttpInvokeLogFilter.class);
    private String[] logResrouces;

    public static int lastIndexOf(String str) {
        int i = str.length() - 1;
        for (; i >= 0; i--) {
            char c = str.charAt(i);
            if (c == Chars.CH_DOT) {
                return i;
            }
            if (c == Chars.CH_BACK) {
                return -1;
            }
        }
        return -1;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

        String string = filterConfig.getInitParameter("logResources");
        // 没有配置
        if (string == null) {
            logResrouces = DEFAULT_LOG_RESOURCES;
        } else {
            string = string.trim();
            // 配置了，但是为空
            if (string.length() == 0) {
                logResrouces = new String[0];
            } else {
                logResrouces = string.split(INIT_PARAM_DELIMITERS);
                for (int i = 0; i < logResrouces.length; i++) {
                    logResrouces[i] = logResrouces[i].toUpperCase();
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

//		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//		boolean needLog = isLogResrouce(httpServletRequest.getRequestURI());
//		if (!needLog) {
//				chain.doFilter(request, response);
//				return;
//			}
//
//		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//		ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
//		long startTime = System.currentTimeMillis();
//		Object data = null;
//		try {
//
//
//
//
//
//			chain.doFilter(request, responseWrapper);
//		} catch (Throwable e) {
//			data = LOGGER.getDetailMessage(e);
//		} finally {
//			//如果是需要记录的资源  开始记录日志
////			if (isLogResrouce(httpServletRequest.getRequestURI())) {
////				LOGGER.logHttpInvoke(
////						httpServletRequest.getRequestURI(),
////						httpServletRequest.getMethod(),
////						getParameters(httpServletRequest),
////						HttpRequestUtil.getClientIP(httpServletRequest),
////						responseWrapper.getStatus(),
////						startTime,
////						data);
////			}
//		}
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (!isLogResrouce(httpServletRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
        Object data = null;
        String httpMethod = httpServletRequest.getMethod();
        String api = httpServletRequest.getRequestURI();
        String clientIp = "sss";
        String paramters = getParameters(httpServletRequest);
        String rid = getRequestId(httpServletRequest);
        long startTime = LOGGER.logIn(rid, httpMethod, clientIp, api, paramters);
        String msg = null;
        try {
            chain.doFilter(request, responseWrapper);
        } catch (Throwable e) {
            msg = e.getMessage();
            data = LOGGER.getDetailMessage(e);
        } finally {
            LOGGER.logOut(httpMethod, clientIp, api, responseWrapper.getStatus(), msg, startTime, data);
        }


    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader(Logger.REQUEST_ID_KEY);
    }

    public boolean isLogResrouce(String url) {
        int lastIndex = lastIndexOf(url);
        if (lastIndex == -1) {
            return true;
        }
        String resource = url.substring(lastIndex + 1).toUpperCase();
        for (String string : logResrouces) {
            if (string.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    private String getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getQueryString();
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    public static class ResponseWrapper extends HttpServletResponseWrapper {

        private int httpStatus = HttpServletResponse.SC_OK;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            doSetStatus(sc);
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            doSetStatus(sc);
            super.sendError(sc, msg);
        }

        private void doSetStatus(int sc) {
            httpStatus = sc;
        }

        public int getStatus() {
            return httpStatus;
        }

        @Override
        public void setStatus(int sc) {
            doSetStatus(sc);
            super.setStatus(sc);
        }

    }

}
