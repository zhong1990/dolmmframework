package org.dol.framework.util;

import org.dol.framework.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

public class ApplicationContextUtil implements ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(ApplicationContextUtil.class);

    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        Object bean = applicationContext.getBean(beanName);
        return bean == null ? null : (T) bean;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    public static void reload() {
        LOGGER.error("relaod", "开始重启应用程序上下文");
        refreshParent(ApplicationContextUtil.applicationContext);
        LOGGER.error("relaod", "重启应用程序上下文完成");
    }

    private static void refreshParent(ApplicationContext applicationContext) {
        ApplicationContext parent = applicationContext.getParent();
        if (parent != null) {
            refreshParent(parent);
        }
        ((ConfigurableApplicationContext) applicationContext).refresh();
    }
}
