/**
 *
 */
package org.dol.framework.web.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.AuthorizationException;
import org.dol.framework.auth.AuthConstraints;
import org.dol.framework.auth.web.ShiroManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年4月11日 上午10:55:39
 */
public class ExceptionResolver extends SimpleMappingExceptionResolver {

    private String ajaxResponse;
    private byte[] ajaxResponseData = AuthConstraints.DEFAULT_UNAUTHORIZED_JSON.getBytes(AuthConstraints.UTF_8);

    public ExceptionResolver() {
        setOrder(HIGHEST_PRECEDENCE);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ShiroManager.isAjaxRequest(request)) {
            try {
                return doResolveAjaxException(request, response, handler, ex);
            } catch (Exception e) {
                return super.doResolveException(request, response, handler, ex);
            }
        } else {
            return super.doResolveException(request, response, handler, ex);
        }
    }

    /**
     * 参照方法名.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private ModelAndView doResolveAjaxException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        if (ex instanceof AuthorizationException) {
            response.getOutputStream().write(ajaxResponseData);
            response.setContentType(AuthConstraints.JSON_CONTENT_TYPE);
            return new ModelAndView();
        }
        if (ex instanceof NoHandlerFoundException) {
            response.getOutputStream().write(AuthConstraints.DEFAULT_NOT_FOUND_EXCEPTION_JSON.getBytes());
            response.setContentType(AuthConstraints.JSON_CONTENT_TYPE);
            return new ModelAndView();
        }
        logger.error(ex);
        response.getOutputStream().write(AuthConstraints.DEFAULT_DEFAULT_EXCEPTION_JSON.getBytes());
        response.setContentType(AuthConstraints.JSON_CONTENT_TYPE);
        return new ModelAndView();
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
        this.ajaxResponseData = this.ajaxResponse.getBytes(AuthConstraints.UTF_8);
    }
}
