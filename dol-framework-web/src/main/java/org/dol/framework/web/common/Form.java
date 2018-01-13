/**
 * 
 */
package org.dol.framework.web.common;

import org.dol.framework.util.HashMap2;
import org.dol.framework.web.model.FormData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO.
 * 
 * @author dolphin
 * @date 2017年5月2日 下午3:02:42
 */
public class Form extends HashMap2<String, FormData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String body;

    public Map<String, Object> getData(boolean includeFile) {
        Set<Map.Entry<String, FormData>> entries = this.entrySet();
        Map<String, Object> map = new HashMap<String, Object>(entries.size());
        for (Map.Entry<String, FormData> entry : entries) {
            FormData formData = entry.getValue();
            if (!formData.getIsFile()) {
                map.put(formData.getName(), formData.getValue());
            } else if (includeFile) {
                map.put(formData.getName(), formData.getData());
            }
        }
        return map;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

}
