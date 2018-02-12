/**
 * xf9-contract
 * SortField.java
 * org.dol.contract.message
 * TODO
 *
 * @author dolphin
 * @date 2016年7月26日 下午4:48:19
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.message;

import java.io.Serializable;

/**
 * ClassName:SortField <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年7月26日 下午4:48:19 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SortField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @Fields name : 排序名称
     */
    private String name;
    /**
     * @Fields direction :排序方向 1:升序，0:降序
     */
    private boolean direction = Boolean.TRUE;

    public SortField() {

    }

    public SortField(String name2, boolean direction2) {

        setName(name2);
        setDirection(direction2);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

}
