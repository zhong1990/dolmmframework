package org.dol.framework.registry;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by dolphin on 2017/10/18.
 */
public abstract class ServiceParameterChecker {

    public static void check(String serviceImplPackage, String servicePackage) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Class> classes = PackageScanner.scan(serviceImplPackage);
        for (Class implClazz : classes) {
            if (implClazz.isInterface()) {
                continue;
            }
            Method[] implMethods = implClazz.getMethods();
            Class[] interfaces = implClazz.getInterfaces();
            for (Class interfaceClazz : interfaces) {
                if (interfaceClazz.getPackage().getName().startsWith(servicePackage)) {
                    Method[] interfaceMethods = interfaceClazz.getMethods();
                    for (Method interfaceMethod : interfaceMethods) {
                        for (Method implMethod : implMethods) {
                            if (isEquals(interfaceMethod, implMethod)) {
                                List<ServiceMethodParameter> serviceImplMethodParameters = ParameterDiscover.getServiceImplMethodParameters(implMethod);
                                List<ServiceMethodParameter> serviceInterfaceMethodParameters = ParameterDiscover.getServiceInterfaceMethodParameters(interfaceMethod, false);
                                for (int i = 0; i < serviceImplMethodParameters.size(); i++) {
                                    if (!serviceImplMethodParameters.get(i).getParameterName().equals(serviceInterfaceMethodParameters.get(i).getParameterName())) {
                                        stringBuilder.append(implClazz.getSimpleName() + "."
                                                + implMethod.getName() + ":"
                                                + serviceImplMethodParameters.get(i).getParameterName()
                                                + "!="
                                                + serviceInterfaceMethodParameters.get(i).getParameterName()
                                                + "\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (stringBuilder.length() > 0) {
            System.out.println(stringBuilder.toString());
        } else {
            System.out.println("方法名称参数相同。");
        }
    }

    public static void checkServiceMethodParameters(String servicePackage) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Class> classes = PackageScanner.scan(servicePackage);
        for (Class implClazz : classes) {
            if (!implClazz.isInterface()) {
                continue;
            }
            Method[] interfaceMethods = implClazz.getMethods();
            for (Method interfaceMethod : interfaceMethods) {
                List<ServiceMethodParameter> serviceInterfaceMethodParameters = ParameterDiscover.getServiceInterfaceMethodParameters(interfaceMethod, true);
                for (ServiceMethodParameter serviceInterfaceMethodParameter : serviceInterfaceMethodParameters) {
                    System.out.println(implClazz.getName() + ":" + interfaceMethod.getName() + ":" + serviceInterfaceMethodParameter.getParameterName());
                }
            }
        }
    }

    private static boolean isEquals(Method interfaceMethod, Method implMethod) {
        if (interfaceMethod.getName().equals(implMethod.getName())) {
            if (!interfaceMethod.getReturnType().equals(implMethod.getReturnType())) {
                return false;
            }
                /* Avoid unnecessary cloning */
            Class<?>[] params1 = interfaceMethod.getParameterTypes();
            Class<?>[] params2 = implMethod.getParameterTypes();
            if (params1.length == params2.length) {
                for (int i = 0; i < params1.length; i++) {
                    if (params1[i] != params2[i])
                        return false;
                }
                return true;
            }
        }
        return false;
    }
}
