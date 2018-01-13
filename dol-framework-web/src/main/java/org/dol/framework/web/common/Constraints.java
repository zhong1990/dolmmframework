/**
 *
 */
package org.dol.framework.web.common;

import java.nio.charset.Charset;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年4月11日 下午2:03:36
 */
public class Constraints {

    public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
    public static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";
    public static final String AJAX_ACCEPT_CONTENT_TYPE_PARAM = "Accept";
    public static final String AJAX_SOURCE_PARAM = "ajaxSource";
    public static final String X_REQUESTED_WITH = "XMLHttpRequest";
    public static final String X_REQUESTED_WITH_KEY = "x-requested-with";
    public static final String DEFAULT_UNAUTHORIZED_JSON = "{\"status\":401,\"message\":\"没有权限访问\",\"success\":false}";
    public static final String DEFAULT_NOT_FOUND_EXCEPTION_JSON = "{\"status\":404,\"message\":\"资源不存在\",\"success\":false}";
    public static final String DEFAULT_DEFAULT_EXCEPTION_JSON = "{\"status\":-1,\"message\":\"发生系统异常\",\"success\":false}";
    public static final String DEFAULT_UNAUTHENTICATED_JSON = "{\"status\":407,\"message\":\"尚未登录\",\"success\":false,\"loginUrl\":\"LOGINURL\"}";
    public static final Charset UTF_8 = Charset.forName("utf-8");
}
