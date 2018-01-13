/**
 * dol-framework-core
 * RedisLockFactory.java
 * org.dol.framework.redis
 * TODO
 *
 * @author dolphin
 * @date 2016年3月18日 下午3:40:47
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.redis;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * ClassName:RedisLockFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 下午3:40:47 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class RedisLockFactory implements InitializingBean, DisposableBean {

    private static RedisLockFactory singlton;
    public volatile boolean shutdown = false;
    private StringRedisTemplateEX redisTemplate;

    public RedisLockFactory() {

    }

    public RedisLockFactory(StringRedisTemplateEX redisTemplate) {
        this.setRedisTemplate(redisTemplate);
    }

    public static RedisLock buildRedisLock(String key) {
        return singlton.buildLock(key);
    }

    public static RedisLock buildRedisLock(String key, int expire) {
        return singlton.buildLock(key, expire);
    }

    public static RedisLock buildRedisLock(List<String> keys) {
        return singlton.buildLock(keys);
    }

    public static RedisLock buildRedisLock(List<String> keys, int expire) {
        return singlton.buildLock(keys, expire);
    }

    public static boolean isShutdown() {
        return singlton.shutdown;
    }

    public StringRedisTemplateEX getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplateEX redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public synchronized void afterPropertiesSet() throws Exception {
        singlton = this;
    }

    @Override
    public void destroy() throws Exception {

        shutdown = true;

    }

    private RedisLock buildLock(String key) {
        return new RedisLock(redisTemplate, key);
    }

    private RedisLock buildLock(List<String> keys) {
        return new RedisLock(redisTemplate, keys);
    }

    private RedisLock buildLock(String key, int expire) {
        return new RedisLock(redisTemplate, key, expire);
    }

    private RedisLock buildLock(List<String> keys, int expire) {
        return new RedisLock(redisTemplate, keys, expire);
    }
}
