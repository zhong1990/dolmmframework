/**
 * dol-gc-web
 * SelectItem.java
 * org.dol.gc.web.common
 * TODO
 *
 * @author dolphin
 * @date 2016年2月1日 上午11:12:24
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.web.model;

/**
 * ClassName:SelectItem <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年2月1日 上午11:12:24 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SelectItem {

    private String id;
    private String text;
    private Boolean selected;

    public SelectItem(String id, String text) {
        setId(id);
        setText(text);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
