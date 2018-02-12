package org.dol.framework.registry;

import org.dol.message.Param;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dolphin on 2017/10/18.
 */
public abstract class ParameterDiscover {

    private static final LocalVariableTableParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

    public static List<ServiceMethodParameter> getServiceInterfaceMethodParameters(
            Method method,
            boolean mustHasParamAnnotation) {
        List<ServiceMethodParameter> parameters = new ArrayList<>();
        Class<?>[] parameterClasses = method.getParameterTypes();
        if (parameterClasses.length == 0) {
            return parameters;
        }
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Type[] parameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameterClasses.length; i++) {
            ServiceMethodParameter serviceMethodParameter = new ServiceMethodParameter();
            Annotation[] annotations = parameterAnnotations[i];
            Param param = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                    break;
                }
            }
            if (param == null) {
                if (mustHasParamAnnotation) {
                    throw new RuntimeException("方法" + method.getName() + "的第" + i + "参数没有设置Param注解");
                }
                serviceMethodParameter.setParameterName("arg" + i);
            } else {
                serviceMethodParameter.setParameterName(param.value());
                serviceMethodParameter.setRequired(param.required());
            }
            Class<?> parameterClass = parameterClasses[i];
            serviceMethodParameter.setParameterClass(parameterClass);
            serviceMethodParameter.setParameterType(parameterTypes[i]);
            parameters.add(serviceMethodParameter);
        }
        return parameters;
    }

    public static List<ServiceMethodParameter> getServiceImplMethodParameters(
            Method method) {
        List<ServiceMethodParameter> parameters = new ArrayList<ServiceMethodParameter>();
        String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);

        Class<?>[] parameterClasses = method.getParameterTypes();
        Type[] parameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameterClasses.length; i++) {
            String paramName = parameterNames[i];
            Class<?> parameterClass = parameterClasses[i];
            ServiceMethodParameter serviceMethodParameter = new ServiceMethodParameter();
            serviceMethodParameter.setParameterName(paramName);
            serviceMethodParameter.setParameterClass(parameterClass);
            serviceMethodParameter.setParameterType(parameterTypes[i]);
            parameters.add(serviceMethodParameter);
        }
        return parameters;
    }
}
