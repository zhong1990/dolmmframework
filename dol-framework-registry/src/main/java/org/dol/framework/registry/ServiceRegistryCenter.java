package org.dol.framework.registry;

import javassist.NotFoundException;
import org.dol.framework.logging.Logger;
import org.dol.framework.reflect.ReflectUtil;
import org.dol.message.Api;
import org.dol.message.ServiceDef;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRegistryCenter implements InitializingBean, ApplicationContextAware {

    public static final String API_DELIMITER = ".";
    private static final Logger LOGGER = Logger.getLogger(ServiceRegistryCenter.class);
    private final Map<Class<?>, Object> serviceObjectMap = new HashMap<>();
    private final Map<String, ServiceObject> serviceObjectCache = new HashMap<String, ServiceObject>();
    private List<String> basePackages;
    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        generateServiceRegistry();
    }

    /**
     * 根据接口生成服务对象仓库
     *
     * @throws Exception
     */
    private void generateServiceRegistry() throws Exception {
        List<Class> classList = PackageScanner.scan(basePackages);
        for (Class clazz : classList) {
            if (clazz.isAnnotationPresent(ServiceDef.class)) {

                String[] beanNames = applicationContext.getBeanNamesForType(clazz);
                if (beanNames.length == 0) {
                    continue;
                }
                if (beanNames.length == 1) {
                    Object bean = applicationContext.getBean(beanNames[0]);
                    if (bean != null) {
                        serviceObjectMap.put(clazz, bean);
                        generateServiceObject(clazz);
                    }
                } else {
                    throw new Exception("发现多个服务的实现，服务："
                            + clazz.getName()
                            + ",实现bean：" + connect2String(beanNames));
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    private void generateServiceObject(Class<?> beanClass) throws NotFoundException {
        ServiceDef annotation = (ServiceDef) beanClass.getAnnotation(ServiceDef.class);
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            if (ReflectUtil.isDefaultMethod(method) || !method.isAnnotationPresent(Api.class)) {
                continue;
            }
            if (ReflectUtil.isDefaultMethod(method)) {
                continue;
            }
            String methodName = method.getName();
            String api = annotation.value() + API_DELIMITER + methodName;
            List<ServiceMethodParameter> parameters = ParameterDiscover.getServiceInterfaceMethodParameters(method, true);
            ServiceObject serviceObject = new ServiceObject(api, serviceObjectMap.get(beanClass), method, parameters);
            serviceObjectCache.put(api, serviceObject);
        }
    }

    private String connect2String(String[] beanNames) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String beanName : beanNames) {
            stringBuilder.append(beanName + ",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public ServiceObject getServiceObject(String api) throws Exception {
        return serviceObjectCache.get(api);
    }
}
