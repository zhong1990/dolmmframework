package org.dol.framework.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ReloadableDispatcherServlet extends DispatcherServlet {

    /**
     *
     */
    private static final String THROW_EXCEPTION_IF_NO_HANDLER_FOUND = "throwExceptionIfNoHandlerFound";
    /**
     *
     */
    private static final String ENCODING_KEY = "encoding";
    private static final String OK_FLAG = "OK";
    private static final String CMD_FLAG = "cmd";
    private static final String RELOAD_FLAG = "reload";
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ENCODING_CHARSET = "utf-8";

    private String encoding;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        encoding = config.getInitParameter(ENCODING_KEY);
        if (encoding == null) {
            encoding = DEFAULT_ENCODING_CHARSET;
        }

        String throwExceptionIfNoHandlerFound = config.getInitParameter(THROW_EXCEPTION_IF_NO_HANDLER_FOUND);

        setThrowExceptionIfNoHandlerFound(throwExceptionIfNoHandlerFound != null
                && throwExceptionIfNoHandlerFound.equals(Boolean.TRUE.toString()));
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cmd = req.getParameter(CMD_FLAG);
        if (cmd != null && cmd.equalsIgnoreCase(RELOAD_FLAG)) {
            refresh();
            resp.setHeader(RELOAD_FLAG, OK_FLAG);
        } else {
            super.doHead(req, resp);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }
        super.service(request, response);
    }

    @Override
    public void refresh() {
        WebApplicationContext wac = getWebApplicationContext();
        refresh(wac);
    }

    private void refresh(ApplicationContext applicationContext) {
        if (applicationContext.getParent() != null) {
            refresh(applicationContext.getParent());
        }
        if (!(applicationContext instanceof ConfigurableApplicationContext)) {
            throw new IllegalStateException("WebApplicationContext does not support refresh: " + applicationContext);
        }
        ((ConfigurableApplicationContext) applicationContext).refresh();

    }

}
