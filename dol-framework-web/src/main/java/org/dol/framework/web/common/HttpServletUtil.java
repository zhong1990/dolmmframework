package org.dol.framework.web.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpServletUtil {
    private static volatile String contextPath;

    public static String url(String relativeUrl) {
        return getFullUrl(relativeUrl);
    }

    public static HttpServletRequest currentRequest() {

        return RequestContext.currentRequest();
    }

    public static HttpServletResponse currentResponse() {

        return RequestContext.currentResponse();
    }

    public static String contextPath() {
        return currentRequest().getContextPath();
    }

    public static HttpSession currentSession() {
        return currentRequest().getSession();
    }

    public static String getFullUrl(String url) {
        if (url.startsWith("/")) {
            return getContextPath() + url;
        } else {
            return getContextPath() + "/" + url;
        }
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    public static String getContextPath() {
        if (contextPath == null) {
            contextPath = currentRequest().getContextPath();
        }
        return contextPath;
    }
}
