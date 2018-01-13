/**
 * studio
 * CommonModel.java
 * org.dol.studio.model
 * TODO
 *
 * @author dolphin
 * @date 2016年7月8日 下午4:03:40
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.web.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class ModelData extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public ModelData() {

    }

    public ModelData(Map<String, Object> data) {
        super(data);
    }

    public String getString(String key) {
        Object value = get(key);
        return value == null ? null : value.toString();
    }
}
