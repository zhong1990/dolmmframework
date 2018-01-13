/**
 *
 */
package org.dol.message;

import java.util.List;

/**
 * TODO.
 *
 * @param <K>
 * @author dolphin
 */
public interface Treeable<K> extends Id<K> {

    void addChild(Treeable<K> child);

    List<? extends Treeable<K>> getChildren();

    String getName();

    K getParentId();
}
