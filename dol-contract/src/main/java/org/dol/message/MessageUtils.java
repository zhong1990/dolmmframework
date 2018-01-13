/**
 *
 */
package org.dol.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年5月1日 上午8:07:28
 */
public abstract class MessageUtils {

    private static final Comparator<Order> ORDER_SORT_COMPARATOR = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            return o1.getOrder() - o2.getOrder();
        }
    };

    public static void removeDuplicateById(List<? extends Id<?>> resources) {
        List<Object> ids = new ArrayList<Object>(resources.size());
        Iterator<? extends Id<?>> ss = resources.iterator();
        while (ss.hasNext()) {
            Id<?> id = ss.next();
            if (ids.contains(id.getId())) {
                ss.remove();
            } else {
                ids.add(id.getId());
            }
        }
    }

    public static <V extends Order> void sort(List<V> list) {
        Collections.sort(list, ORDER_SORT_COMPARATOR);
    }

    public static <K, V extends Id<K>> Map<K, V> toMap(List<V> list) {
        Map<K, V> map = new TreeMap<K, V>();
        for (V v : list) {
            map.put(v.getId(), v);
        }
        return map;
    }

    public static <K, V extends Id<K>> Collection<K> getIds(List<V> list) {
        Set<K> ids = new HashSet<K>(list.size());
        for (V v : list) {
            ids.add(v.getId());
        }
        return ids;
    }

    public static <K, V extends Treeable<K>> List<V> toTree(List<V> list, K rootParentId) {
        Map<K, V> map = toMap(list);
        return toTree(map, list, rootParentId);
    }

    public static <K, V extends Treeable<K>> List<V> toTree(Map<K, V> map, List<V> list, K rootParentId) {
        List<V> roots = new ArrayList<V>();
        for (V v : list) {
            if (v.getParentId() == null || v.getParentId().equals(rootParentId) || v.getParentId().toString().equals(rootParentId.toString())) {
                roots.add(v);
                continue;
            }
            V parent = map.get(v.getParentId());
            if (parent != null) {
                parent.addChild(v);
            }
        }
        return roots;
    }
}
