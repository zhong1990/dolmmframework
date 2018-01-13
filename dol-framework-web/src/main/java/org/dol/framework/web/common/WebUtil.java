package org.dol.framework.web.common;

import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dolphin on 2017/8/24.
 */
public class WebUtil {
    public static boolean isAjaxRequest(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        return Constraints.X_REQUESTED_WITH.equals(httpServletRequest.getHeader(Constraints.X_REQUESTED_WITH_KEY))
                || Constraints.AJAX_ACCEPT_CONTENT_TYPE.equals(httpServletRequest.getHeader(Constraints.AJAX_ACCEPT_CONTENT_TYPE_PARAM))
                || StringUtils.hasText(httpServletRequest.getHeader(Constraints.AJAX_SOURCE_PARAM));
    }
}
