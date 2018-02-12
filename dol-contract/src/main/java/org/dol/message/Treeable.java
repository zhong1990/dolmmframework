/**
 *
 */
package org.dol.message;

import java.util.List;

/**
 * TODO.
 *
 * @param <K>
 * @param <V>
 * @author dolphin
 * @date 2017年4月15日 下午12:18:34
 */
public interface Treeable<K> extends Id<K> {

    void addChildren(Treeable<K> child);

    List<Treeable<K>> getChildren();

    String getName();

    K getParentId();
}
