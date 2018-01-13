package org.dol.framework.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dol.framework.constrants.Chars;
import org.dol.framework.logging.HttpInvokeLogFilter.ResponseWrapper;
import org.dol.framework.logging.Logger;

public class LogableReloadableDispatcherServlet extends ReloadableDispatcherServlet {

    public static final String INIT_PARAM_DELIMITERS = ",; \t\n";

    private static final String[] DEFAULT_LOG_RESOURCES = {"HTML", "HTM", "SHTML", "JSP", "DO", "ACTION"};
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Logger LOGGER = Logger.getLogger(LogableReloadableDispatcherServlet.class);
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
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String string = config.getInitParameter("logResources");

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
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLogResrouce(request.getRequestURI())) {
            super.service(request, response);
            return;
        }

        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        String httpMethod = request.getMethod();
        String api = request.getRequestURI();
        String clientIp = HttpServletUtil.getClientIP(request);
        String paramters = getParameters(request);
        String rid = getRequestId(request);
        long startTime = LOGGER.logIn(rid, httpMethod, clientIp, api, paramters);
        Object data = null;
        String msg = null;
        try {
            super.service(request, responseWrapper);
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

    private String getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getQueryString();
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
}
