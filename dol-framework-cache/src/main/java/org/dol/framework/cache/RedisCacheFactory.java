/**
 * dol-framework-zookeeper
 * LocalCacheFactory.java
 * org.dol.framework.zookeeper
 * TODO
 * 
 * @author dolphin
 * @date 2016年3月1日 上午11:55:38
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.cache;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;

/**
 * ClassName:LocalCacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月1日 上午11:55:38 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
/**
 * TODO
 *
 * @author dolphin
 * @date 2016年3月1日 下午6:15:26
 * @version 1.0
 */
public class RedisCacheFactory extends AbstractCacheFactory {

    private StringRedisTemplate redisTemplate;

    private String cachePrefix;

    @Override
    public boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt,
            final Long relateExpiredTimeMs,
            final Object... dependencies) {

        String redisKey = buildRedisKey(cacheObjectKey);
        if (cacheData == null) {
            remove(cacheObjectKey);
            return true;
        }
        String value = JSON.toJSONString(cacheData);
        BoundValueOperations<String, String> opts = redisTemplate.boundValueOps(redisKey);
        if (expiredAt != null) {
            long expireTimeInMs = expiredAt.getTime() - System.currentTimeMillis();
            if (expireTimeInMs > 0) {
                opts.set(value, expireTimeInMs, TimeUnit.MILLISECONDS);
            }
        } else if (relateExpiredTimeMs != null) {
            if (relateExpiredTimeMs.longValue() > 0) {
                opts.set(value, relateExpiredTimeMs.longValue(), TimeUnit.MILLISECONDS);
            }
        } else {
            opts.set(value);
        }
        return true;
    }

    @Override
    public boolean clear() {
        redisTemplate.delete(redisTemplate.keys(cachePrefix + "*"));
        return true;
    }

    public synchronized void destroy() {
        try {
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void expire(Object dependency) {
        throw new UnsupportedOperationException("不支持，请示用方法 remove(cacheObjectKey)");
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    @Override
    public <T> T getObject(String cacheObjectKey) {
        throw new UnsupportedOperationException("不支持，请示用方法 getObject(String cacheObjectKey, Class<T> clazz)");
    }

    @Override
    public <T> T getObject(String cacheObjectKey, Class<T> clazz) {

        String value = redisTemplate.boundValueOps(buildRedisKey(cacheObjectKey)).get();
        if (null == value) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public synchronized void init() throws IOException, KeeperException, InterruptedException {
        // do nothing
    }

    @Override
    public void remove(String cacheObjectKey) {
        redisTemplate.delete(buildRedisKey(cacheObjectKey));
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildRedisKey(String cacheObjectKey) {
        return cachePrefix + cacheObjectKey;
    }

}
