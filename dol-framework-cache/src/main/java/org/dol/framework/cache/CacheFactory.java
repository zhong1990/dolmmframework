/**
 * dol-framework-cache
 * CacheFactory.java
 * org.dol.framework.zookeeper
 * TODO
 * 
 * @author dolphin
 * @date 2016年3月2日 下午2:43:36
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.cache;

import java.util.Date;

/**
 * ClassName:CacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月2日 下午2:43:36 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public interface CacheFactory {

    boolean add(
            final String cacheObjectKey,
            final Object cacheData);

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt);

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt,
            final Long relativeExpiredMs,
            final Object... dependencies);

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Long relativeExpiredMs);

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Object... dependencies);

    /**
     * 清楚所有缓存
     * 
     * @return boolean
     * @throws
     *             @author dolphin
     * @since JDK 1.7
     * @date 2016年3月2日 下午2:49:51
     */
    boolean clear();

    void expire(Object dependency);

    <T> T getObject(String cacheObjectkey);

    <T> T getObject(String cacheObjectkey, Class<T> clazz);

    void remove(String cacheObjectKey);
}
