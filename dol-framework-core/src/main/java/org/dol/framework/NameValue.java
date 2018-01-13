/**
 * dol-framework-core
 * NameValue.java
 * org.dol.framework
 * TODO
 *
 * @author dolphin
 * @date 2016年3月18日 上午11:01:18
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework;

/**
 * ClassName:NameValue <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 上午11:01:18 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class NameValue<N, V> {
    private N name;
    private V value;

    public NameValue() {

    }

    public NameValue(N k, V v) {
        this.setName(k);
        this.setValue(v);
    }

    public N getName() {
        return name;
    }

    public void setName(N name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
