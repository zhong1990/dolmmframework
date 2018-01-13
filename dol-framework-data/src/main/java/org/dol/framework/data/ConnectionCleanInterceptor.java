/**
 * dol-framework-data
 * ConnectionCleanInterceptor.java
 * org.dol.framework.data
 * TODO
 *
 * @author dolphin
 * @date 2016年2月3日 下午1:31:29
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dol.framework.logging.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName:ConnectionCleanInterceptor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年2月3日 下午1:31:29 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class ConnectionCleanInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = Logger.getLogger(ConnectionCleanInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        if (MapperFactory.checkAndRollbackTransaction()) {
            LOGGER.error("postHandle", "发现为关闭的事务");
            // System.out.println("found uncommit transaction");
        }

    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {

    }

}
