package org.dol.framework.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.constructs.refreshahead.RefreshAheadCache;
import net.sf.ehcache.constructs.refreshahead.RefreshAheadCacheConfiguration;
import net.sf.ehcache.loader.CacheLoader;

public abstract class RefreshAheadRedisEhCache<TValue> extends RedisEhCache<TValue>
        implements CacheLoader {

    static class AutoRefreshAheadCache extends RefreshAheadCache {

        private Thread autoRefreshWorker;

        public AutoRefreshAheadCache(Ehcache adaptedCache, RefreshAheadCacheConfiguration refreshConfig) {
            super(adaptedCache, refreshConfig);
        }

        public AutoRefreshAheadCache(
                Ehcache adaptedCache,
                RefreshAheadCacheConfiguration refreshConfig,
                CacheLoader cacheLoader) {
            super(adaptedCache, refreshConfig);
            registerCacheLoader(cacheLoader);
        }

        public void startRefreshWorker(final long refreshIntervalMs) {

            autoRefreshWorker = new Thread(new Runnable() {
                @SuppressWarnings("rawtypes")
                @Override
                public void run() {
                    long refreshIntervalNanos = TimeUnit.NANOSECONDS.convert(refreshIntervalMs, TimeUnit.MILLISECONDS);
                    while (!Thread.currentThread().isInterrupted()) {
                        // System.out.println("auto refresh");
                        try {
                            List keys = getKeys();
                            for (Object key : keys) {
                                get(key);
                            }
                        } catch (Throwable e) {

                        }
                        LockSupport.parkNanos(refreshIntervalNanos);
                    }
                }
            });
            autoRefreshWorker.start();
        }

        public void stopRefreshWorker() {
            if (autoRefreshWorker != null) {
                autoRefreshWorker.interrupt();
                autoRefreshWorker = null;
            }
        }
    }

    private volatile Status status = Status.STATUS_UNINITIALISED;

    /**
     * 从缓存创建，经过多少秒（timeToRefreshSeconds）后开始更细缓存
     */
    private long timeToRefreshSeconds = 0;

    /**
     * 可以放入队列的等待更新的缓存Key数量
     */
    private int maximumRefreshBacklogItems = 10000;

    /**
     * 批量更新缓存的数量
     */
    private int batchSize = 100;

    private AutoRefreshAheadCache autoRefreshAheadCache;
    /**
     * 如果从后端（DB,FILE,etc）等没有查询到数据，是否立即清空该缓存
     */
    private boolean evictOnLoadMiss = true;

    /**
     * 是否启动自动刷新
     */
    private boolean autoRefresh = true;

    /**
     * 刷新间隔
     */
    private long refreshIntervalMs = 0;

    /**
     * 从后台读取数据更新缓存的线程数
     */
    private int numberOfThreads = 1;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        super.afterPropertiesSet();
        if (autoRefresh) {
            if (refreshIntervalMs == 0) {
                refreshIntervalMs = (autoRefreshAheadCache.getCacheConfiguration().getTimeToLiveSeconds() - timeToRefreshSeconds) * 1000 / 2;
            }
            autoRefreshAheadCache.startRefreshWorker(refreshIntervalMs);
        }
    }

    @Override
    public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void destroy() throws Exception {
        dispose();
        super.destroy();
    }

    @Override
    public void dispose() throws CacheException {
        status = Status.STATUS_SHUTDOWN;
        autoRefreshAheadCache.stopRefreshWorker();
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean getEvictOnLoadMiss() {
        return evictOnLoadMiss;
    }

    public int getMaximumRefreshBacklogItems() {
        return maximumRefreshBacklogItems;
    }

    @Override
    public String getName() {
        return getCacheName() + ".docoraor";
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public long getRefreshIntervalMs() {
        return refreshIntervalMs;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public Long getTimeToRefreshSeconds() {
        return timeToRefreshSeconds;
    }

    @Override
    public void init() {
        status = Status.STATUS_ALIVE;
        Cache cache = getCacheManager().getCache(getCacheName());
        if (this.timeToRefreshSeconds <= 0) {
            this.timeToRefreshSeconds = cache.getCacheConfiguration().getTimeToLiveSeconds() * 3 / 4;
        }
        RefreshAheadCacheConfiguration refreshConfig = buildRefreshConfig();
        autoRefreshAheadCache = new AutoRefreshAheadCache(
                cache,
                refreshConfig,
                this);
        autoRefreshAheadCache.registerCacheLoader(this);
        getCacheManager().addDecoratedCache(autoRefreshAheadCache);
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    @Override
    public Object load(Object key) throws CacheException {
        return getSoR((String) key);
    }

    @Override
    public Object load(Object key, Object argument) {
        return load(key);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map loadAll(Collection keys) {
        Map map = new HashMap();
        for (Object key : keys) {
            map.put(key, load(key));
        }
        return map;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map loadAll(Collection keys, Object argument) {
        return loadAll(keys);
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setEvictOnLoadMiss(boolean evictOnLoadMiss) {
        this.evictOnLoadMiss = evictOnLoadMiss;
    }

    public void setMaximumRefreshBacklogItems(int maximumRefreshBacklogItems) {
        this.maximumRefreshBacklogItems = maximumRefreshBacklogItems;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setRefreshIntervalMs(long refreshIntervalMs) {
        this.refreshIntervalMs = refreshIntervalMs;
    }

    public void setTimeToRefreshSeconds(long timeToRefreshSeconds) {
        this.timeToRefreshSeconds = timeToRefreshSeconds;
    }

    @Override
    protected Ehcache buildCache() {
        return getCacheManager().getEhcache(getName());
    }

    private RefreshAheadCacheConfiguration buildRefreshConfig() {
        RefreshAheadCacheConfiguration refreshConfig = new RefreshAheadCacheConfiguration();
        refreshConfig.setName(getName());
        refreshConfig.setEvictOnLoadMiss(this.evictOnLoadMiss);
        refreshConfig.setBatchSize(Math.max(this.batchSize, 1));
        refreshConfig.setMaximumRefreshBacklogItems(Math.max(this.maximumRefreshBacklogItems, 1));
        refreshConfig.setNumberOfThreads(Math.max(this.numberOfThreads, 1));
        refreshConfig.setTimeToRefreshSeconds(this.timeToRefreshSeconds);
        return refreshConfig.build();
    }
}
