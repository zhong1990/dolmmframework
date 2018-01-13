package org.dol.framework.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.dol.framework.logging.Logger;
import org.dol.framework.redis.StringRedisTemplateEX;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class RedisEhCache<TValue>
        implements InitializingBean, DisposableBean, MessageListener {

    private static final Logger LOGGER = Logger.getLogger(RedisEhCache.class);
    private static final String DELETE_PATTERN = ".delete";
    private static final String UPDATE_PATTERN = ".update";
    private final LinkedBlockingQueue<KeyPattern> NOTIFY_UPDATE_QUEUE = new LinkedBlockingQueue<KeyPattern>();
    protected Ehcache cache;
    /**
     * @Fields shutdown : 关闭线程
     */
    private volatile boolean shutdown = false;
    private String channel;
    private String cacheName;
    private CacheManager cacheManager;
    private StringRedisTemplateEX stringRedisTemplate;
    private final Thread notifyWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!shutdown && !Thread.currentThread().isInterrupted()) {
                    KeyPattern keyPattern = NOTIFY_UPDATE_QUEUE.take();
                    try {
                        notifyUpdateCache(keyPattern.key, keyPattern.pattern);
                    } catch (Exception e1) {
                        NOTIFY_UPDATE_QUEUE.put(keyPattern);
                        Thread.sleep(200);
                    }
                }
            } catch (InterruptedException e2) {
                //
            }
        }
    });

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = buildCache();
        notifyWorker.start();
    }

    /**
     * 删除缓存，并通知其他服务器删除缓存
     *
     * @param key
     * @author dolphin
     * @date 2016年5月27日 下午1:54:35
     * @since JDK 1.7
     */
    public void delete(String key) {
        try {
            removeCache(key);
            notifyUpdateCache(key, DELETE_PATTERN);
        } catch (Exception e) {
            putNotifyQueue(key, DELETE_PATTERN);
            LOGGER.error("delete", "删除缓存失败", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown = true;
        notifyWorker.interrupt();
    }

    /**
     * 从缓存中获取数据，不存在返回null
     *
     * @param key
     * @return T
     * @author dolphin
     * @date 2016年5月27日 上午11:31:56
     * @since JDK 1.7
     */
    @SuppressWarnings("unchecked")
    public TValue get(String key) {
        Element element = cache.get(key);
        return element == null ? null : (TValue) element.getObjectValue();
    }

    /**
     * 从缓存中读取数据，如果不存在，则后台存储设备（db,File）读取数据
     *
     * @param key
     * @return TValue
     * @author dolphin
     * @date 2016年6月3日 下午5:35:59
     * @since JDK 1.7
     */
    @SuppressWarnings("unchecked")
    public TValue getAside(String key) {
        Element element = cache.get(key);
        if (element == null) {
            try {
                this.cache.acquireReadLockOnKey(key);
                element = cache.get(key);
            } finally {
                this.cache.releaseReadLockOnKey(key);
            }

            if (element == null) {
                try {
                    cache.acquireWriteLockOnKey(key);
                    element = cache.get(key);
                    if (element == null) {
                        TValue value = getSoR(key);
                        LOGGER.debug("load data from db");
                        element = new Element(key, value);
                        cache.put(element);
                    }

                } finally {
                    cache.releaseWriteLockOnKey(key);
                }
            }
        }
        Object obj = element.getObjectValue();
        return obj == null ? null : (TValue) obj;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * channel.
     *
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * channel.
     *
     * @param channel the channel to set
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    public StringRedisTemplateEX getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplateEX stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 通知其他服务器更新缓存
     *
     * @param key
     * @author dolphin
     * @date 2016年5月27日 下午1:40:54
     * @since JDK 1.7
     */
    public void notifyUpdateCache(String key, String pattern) {
        stringRedisTemplate.convertAndSend(channel + pattern, key);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        try {
            cache.acquireWriteLockOnKey(key);
            if (pattern != null) {
                String action = new String(message.getChannel());
                if (action.endsWith(DELETE_PATTERN)) {
                    removeCache(key);
                    return;
                }
            }
            doUpdateCache(key);
        } finally {
            cache.releaseWriteLockOnKey(key);
        }
    }

    /**
     * 更新缓存
     *
     * @param key
     * @author dolphin
     * @date 2016年5月27日 上午11:31:02
     * @since JDK 1.7
     */
    public void update(String key) {
        try {
            notifyUpdateCache(key, UPDATE_PATTERN);
        } catch (Exception e) {
            putNotifyQueue(key, UPDATE_PATTERN);
            LOGGER.error("update", "更新缓存失败", e);
        }
    }

    protected void add(String key, TValue value) {
        cache.put(new Element(key, value));
    }

    protected void afterAfter(String key, Object value) {
        // TODO Auto-generated method stub
    }

    protected void beforeRemove(String key, Object value) {
        // TODO Auto-generated method stub
    }

    protected Ehcache buildCache() {
        return cacheManager.getEhcache(cacheName);
    }

    /**
     * 执行更新本地缓存
     *
     * @param key
     * @author dolphin
     * @date 2016年5月27日 下午1:41:40
     * @since JDK 1.7
     */
    protected void doUpdateCache(String key) {
        TValue value = getSoR(key);
        if (value == null) {
            removeCache(key);
        } else {
            add(key, value);
        }
    }

    /**
     * 从存储系统（DB，File，etc.）读取数据
     *
     * @param key
     * @return TValue
     * @author dolphin
     * @date 2016年6月3日 下午5:35:12
     * @since JDK 1.7
     */
    protected abstract TValue getSoR(String key);

    private void putNotifyQueue(String key, String pattern) {
        try {
            NOTIFY_UPDATE_QUEUE.put(new KeyPattern(key, pattern));
        } catch (InterruptedException e) {
            removeCache(key);
        }
    }

    private void removeCache(String key) {
        Element el = cache.get(key);
        if (el != null) {
            beforeRemove(key, el.getObjectValue());
            cache.remove(key);
            afterAfter(key, el.getObjectValue());
        }
    }

    static class KeyPattern {

        String key;
        String pattern;

        KeyPattern(String key1, String pattern2) {
            this.key = key1;
            this.pattern = pattern2;
        }
    }
}
