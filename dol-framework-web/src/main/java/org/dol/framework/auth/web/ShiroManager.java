package org.dol.framework.auth.web;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dol.framework.auth.AuthConstraints;
import org.springframework.util.StringUtils;

public class ShiroManager {

    public static boolean isAjaxRequest(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        return AuthConstraints.X_REQUESTED_WITH.equals(httpServletRequest.getHeader(AuthConstraints.X_REQUESTED_WITH_KEY))
                || AuthConstraints.AJAX_ACCEPT_CONTENT_TYPE.equals(httpServletRequest.getHeader(AuthConstraints.AJAX_ACCEPT_CONTENT_TYPE_PARAM))
                || StringUtils.hasText(httpServletRequest.getHeader(AuthConstraints.AJAX_SOURCE_PARAM));
    }
}
