/**
 *
 */
package org.dol.message;

/**
 * 具有Id字段的模型.
 *
 * @param <T> Id字段的类型
 * @author dolphin
 * @date 2017年4月28日 下午2:26:53
 */
public interface Id<T> {

    T getId();

    void setId(T id);
}
