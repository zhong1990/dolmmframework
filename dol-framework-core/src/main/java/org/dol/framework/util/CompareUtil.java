/**
 * dol-framework-core
 * CompareUtil.java
 * org.dol.framework.util
 * TODO
 *
 * @author dolphin
 * @date 2016年3月16日 下午4:42:38
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.util;

/**
 * ClassName:CompareUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月16日 下午4:42:38 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class CompareUtil {

    /**
     * 使用对象的Comparable方法判断两个对象是否相等 ，如果了两个对象都是null则相等
     *
     * @param a
     * @param b
     * @return
     */
    public static <T extends Comparable<T>> boolean equal(T a, T b) {
        if (a == null || b == null) {
            return b == null && a == null;
        }
        return a.compareTo(b) == 0;
    }

    /**
     * 使用对象的equals方法判断两个对象是否相等 ，如果了两个对象都是null则相等
     *
     * @param a
     * @param b
     * @return
     */
    public static <T> boolean equals(T a, T b) {
        if (a == null || b == null) {
            return b == null && a == null;
        }
        return a.equals(b);
    }
}
