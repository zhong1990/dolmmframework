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

import org.apache.zookeeper.KeeperException;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.CompareUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LocalCacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月1日 上午11:55:38 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @see
 * @since JDK 1.7
 */

/**
 * TODO
 *
 * @author dolphin
 * @date 2016年3月1日 下午6:15:26
 * @version 1.0
 */
public class RedisInJVMCacheFactory extends AbstractCacheFactory {

    private static final Map<String, Object> cachedObjectsMap = new ConcurrentHashMap<String, Object>();
    private static final Map<String, String> dependecyMap = new ConcurrentHashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(RedisInJVMCacheFactory.class);
    private final ArrayBlockingQueue<CachedData> updateDependencyQueue = new ArrayBlockingQueue<CachedData>(1000);
    private Thread updateDependencyWorker;
    private StringRedisTemplate redisTemplate;
    /**
     * @Fields shutdown : 关闭线程
     */
    private volatile boolean shutdown = false;

    @Override
    public boolean add(
            String cacheObjectKey,
            Object cacheData,
            Date expiredAt,
            Long relativeExpiredMs,
            Object... dependencies) {

        if (dependencies == null) {
            return false;
        }

        cachedObjectsMap.put(cacheObjectKey, cacheData);

        long expireMs = 0l;
        if (expiredAt != null) {
            expireMs = expiredAt.getTime() - System.currentTimeMillis();
        } else if (relativeExpiredMs != null) {
            expireMs = relativeExpiredMs.longValue();
        }
        CachedData cachedData = new CachedData(cacheObjectKey, dependencies[0].toString(), expireMs);
        updateDependecy(cachedData);
        return true;
    }

    @Override
    public boolean clear() {

        redisTemplate.delete(cachedObjectsMap.keySet());
        cachedObjectsMap.clear();
        dependecyMap.clear();
        return true;
    }

    public synchronized void destroy() {
        shutdown = true;

        try {
            stopWorker();
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void expire(Object dependency) {
        // 不支持
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(String cacheObjectkey) {

        String localVersion = dependecyMap.get(cacheObjectkey);
        String remoteVersion = redisTemplate.boundValueOps(cacheObjectkey).get();
        if (CompareUtil.equal(localVersion, remoteVersion)) {
            Object obj = cachedObjectsMap.get(cacheObjectkey);
            return obj == null ? null : (T) cachedObjectsMap.get(cacheObjectkey);
        }
        return null;
    }

    @Override
    public <T> T getObject(String cacheObjectkey, Class<T> clazz) {
        return getObject(cacheObjectkey);
    }

    public synchronized void init() throws IOException, KeeperException, InterruptedException {
        updateDependencyWorker = getRefreshDependecyWorker();
        updateDependencyWorker.start();
    }

    @Override
    public void remove(String cacheObjectKey) {
        redisTemplate.delete(cacheObjectKey.toString());
        cachedObjectsMap.remove(cacheObjectKey);
        dependecyMap.remove(cacheObjectKey);
    }

    private Thread getRefreshDependecyWorker() {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 当线程收到中断通知，自动结束
                    while (!shutdown && !Thread.interrupted()) {
                        final CachedData cachedData = updateDependencyQueue.take();
                        if (!updateDependecy(cachedData)) {
                            Thread.sleep(20);
                        }
                    }
                } catch (InterruptedException e) {
                    // 该线程被终止
                    // do nothing
                }
            }
        });
    }

    private void stopWorker() {
        try {
            if (updateDependencyWorker != null) {
                updateDependencyWorker.interrupt();
            }
        } catch (Exception e) {
            LOGGER.error("stopWorker", "停止缓存异步工作线程失败", e);
        }
    }

    private boolean updateDependecy(CachedData cachedData) {
        try {
            if (cachedData.expireMs > 0) {
                redisTemplate.boundValueOps(cachedData.key)
                        .set(cachedData.version, cachedData.expireMs, TimeUnit.MILLISECONDS);
            } else {
                redisTemplate.boundValueOps(cachedData.key).set(cachedData.version);
            }
            dependecyMap.put(cachedData.key, cachedData.version);
            return true;
        } catch (Exception e) {
            try {
                updateDependencyQueue.put(cachedData);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return false;

    }

    static class CachedData {
        private String key;
        private String version;
        private long expireMs;

        public CachedData(String key, String version, long expireMs) {
            setExpireMs(expireMs);
            setKey(key);
            setVersion(version);
        }

        public long getExpireMs() {
            return expireMs;
        }

        public void setExpireMs(long expireMs) {
            this.expireMs = expireMs;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
