/**
 * dol-framework-core
 * RequestIdThreadLocal.java
 * org.dol.framework.logging
 * TODO
 *
 * @author dolphin
 * @date 2016年5月5日 下午1:47:13
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.logging;

import java.util.UUID;

import org.dol.framework.util.StringUtil;

/**
 * ClassName:RequestIdThreadLocal <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月5日 下午1:47:13 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class RequestIdThreadLocal extends ThreadLocal<String> {
    private static final String OLD_CHAR = "-";

    @Override
    public String get() {
        String requestId = super.get();
        if (null == requestId) {
            requestId = UUID.randomUUID().toString().replace(OLD_CHAR, StringUtil.EMPTY_STRING);
            set(requestId);
        }
        return requestId;
    }
}
