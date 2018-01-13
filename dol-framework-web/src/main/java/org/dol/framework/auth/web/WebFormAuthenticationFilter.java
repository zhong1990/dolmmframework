package org.dol.framework.auth.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.util.WebUtils;
import org.dol.framework.auth.AuthConstraints;

public class WebFormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    private String ajaxResponse = AuthConstraints.DEFAULT_UNAUTHENTICATED_JSON;
    private String captchaParam = AuthConstraints.DEFAULT_CAPTCHA_PARAM;
    private byte[] ajaxResponseData;

    public String getCaptchaParam() {
        return captchaParam;
    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }

    private byte[] getAjaxResponseData(HttpServletRequest request) {
        if (ajaxResponseData == null) {
            String response = ajaxResponse.replace("LOGINURL", request.getContextPath() + getLoginUrl());
            ajaxResponseData = response.getBytes(AuthConstraints.UTF_8);
        }
        return ajaxResponseData;
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            } else {
                return true;
            }
        } else {
            if (ShiroManager.isAjaxRequest(request)) {
                response.getOutputStream().write(getAjaxResponseData((HttpServletRequest) request));
                response.setContentType(AuthConstraints.JSON_CONTENT_TYPE);
            } else {
                saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }

    /**
     * @return the ajaxResponse
     */
    public String getAjaxResponse() {
        return ajaxResponse;
    }

    /**
     * @param ajaxResponse the ajaxResponse to set
     */
    public void setAjaxResponse(String ajaxResponse) {
        this.ajaxResponse = ajaxResponse;
    }

}
