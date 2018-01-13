package org.dol.framework.cache;

import com.alibaba.fastjson.JSON;
import org.dol.framework.logging.Logger;
import org.dol.framework.redis.StringRedisTemplateEX;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class RedisCache<TValue> implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = Logger.getLogger(RedisCache.class);
    private final LinkedBlockingQueue<String> QUEUE_FOR_REMOVE = new LinkedBlockingQueue<String>(1000);
    private final LinkedBlockingQueue<String> QUEUE_FOR_UPDATE = new LinkedBlockingQueue<String>(1000);
    private String keyPrefix;
    private StringRedisTemplateEX redisTemplate;
    private volatile boolean shutdown = false;

    private final Thread removeWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!shutdown && !Thread.currentThread().isInterrupted()) {
                    String redisKey = QUEUE_FOR_REMOVE.take();
                    try {
                        redisTemplate.delete(redisKey);
                    } catch (Exception e1) {
                        QUEUE_FOR_REMOVE.put(redisKey);
                        Thread.sleep(200);
                    }
                }
            } catch (InterruptedException e2) {
                //
            }
        }
    });

    private final Thread updateCacheWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!shutdown && !Thread.currentThread().isInterrupted()) {
                    String key = QUEUE_FOR_UPDATE.take();
                    try {
                        fetchAndAddToCache(key);
                    } catch (Exception e1) {
                        QUEUE_FOR_UPDATE.put(key);
                        Thread.sleep(200);
                    }
                }
            } catch (InterruptedException e2) {
                //
            }
        }
    });

    public void add(String key) {
        try {
            QUEUE_FOR_UPDATE.put(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void add(String key, TValue value) {
        String cacheKey = buildRedisKey(key);
        redisTemplate.boundValueOps(cacheKey).set(JSON.toJSONString(value));
    }

    public void add(String key, TValue value, Integer timeOutMs, TimeUnit timeUnit) {
        String cacheKey = buildRedisKey(key);
        redisTemplate.boundValueOps(cacheKey).set(JSON.toJSONString(value),
                timeOutMs,
                timeUnit);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void delete(List<String> keyList) {

        List<String> redisKeyList = new ArrayList<String>(keyList.size());
        for (String key : keyList) {
            redisKeyList.add(buildRedisKey(key));
        }
        try {
            redisTemplate.delete(redisKeyList);
        } catch (Exception e) {
            for (String redisKey : redisKeyList) {
                putRemoveQueue(redisKey);
            }
            LOGGER.error("delete", "删除优惠券失败", e);
        }
    }

    public void delete(String code) {
        String redisKey = buildRedisKey(code);
        try {
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            putRemoveQueue(redisKey);
            LOGGER.error("delete", "删除优惠券失败", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown = true;
        removeWorker.interrupt();
        updateCacheWorker.interrupt();
    }

    public TValue get(String key, Class<TValue> clazz) {
        try {
            String redisKey = buildRedisKey(key);
            return redisTemplate.getObject(redisKey, clazz);
        } catch (Exception e) {
            LOGGER.error("getCouponTemplate", "从缓存获取优惠券模板失败", e);
        }
        return null;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public StringRedisTemplateEX getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplateEX redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void start() {
        removeWorker.start();
        updateCacheWorker.start();
    }

    /**
     * 批量异步更新缓存
     *
     * @param keyList
     * @author dolphin
     * @date 2016年6月3日 下午4:32:27
     * @since JDK 1.7
     */
    public void update(List<String> keyList) {
        for (String code : keyList) {
            update(code);
        }
    }

    /**
     * 异步更新缓存
     *
     * @param key void
     * @throws
     * @author dolphin
     * @date 2016年6月3日 下午4:32:54
     * @since JDK 1.7
     */
    public void update(String key) {
        try {
            QUEUE_FOR_UPDATE.put(key);
        } catch (Exception e) {
            LOGGER.error("update", "更新优惠券缓存失败", e);
        }
    }

    protected String buildRedisKey(String code) {
        return keyPrefix + code;
    }

    /**
     * 从后端存储（DB，file,etc.）读取数据,然后放入缓存,如果数据为空，则从redis删除
     *
     * @param key
     * @author dolphin
     * @date 2016年6月3日 下午3:52:57
     * @since JDK 1.7
     */
    protected void fetchAndAddToCache(String key) {
        TValue value = fetchFromBackend(key);
        if (value == null) {
            redisTemplate.delete(buildRedisKey(key));
        } else {
            add(key, value);
        }
    }

    /**
     * 从后端存储（DB，file,etc.）读取数据
     *
     * @param key
     * @author dolphin
     * @date 2016年6月3日 下午3:54:57
     * @since JDK 1.7
     */
    protected abstract TValue fetchFromBackend(String key);

    private void putRemoveQueue(String redisKey) {
        try {
            QUEUE_FOR_REMOVE.put(redisKey);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
