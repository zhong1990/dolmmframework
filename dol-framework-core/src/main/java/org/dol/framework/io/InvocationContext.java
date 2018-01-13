/**
 * dol-framework-core
 * InvocationContext.java
 * org.dol.framework.io
 * TODO
 *
 * @author dolphin
 * @date 2016年2月3日 下午3:05:22
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.io;

/**
 * ClassName:InvocationContext <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年2月3日 下午3:05:22 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class InvocationContext {

    private static final InvokeUniqueIdHolder invokeUniqueIdHolder = new InvokeUniqueIdHolder();

    public static String getInvokeUniqueId() {
        return invokeUniqueIdHolder.get();
    }

    public static void close() {
        invokeUniqueIdHolder.remove();
    }

    static class InvokeUniqueIdHolder extends ThreadLocal<String> {
        @Override
        protected String initialValue() {
            return String.valueOf(System.nanoTime());
        }
    }
}
