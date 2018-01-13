/**
 * dol-framework-core
 * XmlUtil.java
 * org.dol.framework.util
 * TODO
 *
 * @author dolphin
 * @date 2016年9月11日 下午4:19:02
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * XML工具类
 *
 * @author dolphin
 * @date 2016年9月11日 下午4:21:17
 * @version 1.0
 */
public abstract class XmlUtil {

    public static String map2Xml(
            Map<String, Object> map,
            boolean ignoreEmpty) {
        Set<Entry<String, Object>> entries = map.entrySet();
        StringBuilder sb = new StringBuilder("<xml>");
        for (Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            if (ignoreEmpty && value.equals(StringUtil.EMPTY_STRING)) {
                continue;
            }
            sb.append("<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static String object2Xml(Object object, boolean ignoreEmpty) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder("<xml>");
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value == null) {
                    continue;
                }
                if (ignoreEmpty && value.equals(StringUtil.EMPTY_STRING)) {
                    continue;
                }
                sb.append("<" + field.getName() + ">" + value + "</" + field.getName() + ">");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
