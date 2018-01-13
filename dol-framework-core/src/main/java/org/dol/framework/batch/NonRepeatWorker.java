package org.dol.framework.batch;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.dol.framework.logging.Logger;
import org.dol.framework.redis.StringRedisTemplateEX;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * 同一个数据处理在同一时间只有一个任务在处理
 * 如果存在等待处理的数据，新的数据请求被忽略
 * 使用Redis实现分布式控制.
 *
 * @param <T>
 * @author dolphin
 * @date 2017年5月11日 上午10:57:47
 */
public abstract class NonRepeatWorker<T> {
    private static final Logger LOGGER = Logger.getLogger(NonRepeatWorker.class);
    private static final int MINUTES_OF_THE_IDLE_LIFE = 5;
    /**
     * 每个任务的预估最大处理时间，用于设置redis缓存过期
     */
    private long maxTaskRunTimeMs = 60000L;
    private String taskName = "NonRepeatWorker";
    private int maxRetryTimes = 0;
    private boolean reputInQueue = false;
    private int numberOfThreads = 10;
    private int maximumQueueSize = 10000;
    private ArrayBlockingQueue<T> queue = null;
    private volatile boolean isInit = false;
    private ExecutorService threadPool;
    private StringRedisTemplateEX runningTasks;
    private GenericObjectPool<Task> taskPool;
    private boolean isStop;
    private Thread dispatchWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    T t = queue.take();
                    if (!isRunning(t)) {
                        Task task = taskPool.borrowObject();
                        task.setData(t);
                        if (addToRunningTask(task)) {
                            threadPool.execute(task);
                            continue;
                        }
                    }
                    put(t);
                }
            } catch (InterruptedException ignore) {
                // ignore
                // e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    });

    public NonRepeatWorker() {

    }

    public NonRepeatWorker(String taskName) {
        this.taskName = taskName;
    }

    public NonRepeatWorker(
            String taskName,
            int maximumQueueSize,
            int numberOfThreads,
            int maxRetryTimes,
            boolean reputInQueue,
            long maxTaskRunTime) {
        this.taskName = taskName;
        this.maximumQueueSize = maximumQueueSize;
        this.numberOfThreads = numberOfThreads;
        this.maxRetryTimes = maxRetryTimes;
        this.reputInQueue = reputInQueue;
        this.maxTaskRunTimeMs = maxTaskRunTime;
    }

    public static void main(String[] args) {
        NonRepeatWorker<Integer> worker = new NonRepeatWorker<Integer>("test") {

            @Override
            protected void doProcess(Integer t) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1000L));

            }

            @Override
            protected StringRedisTemplateEX taskRedisTemplate() {
                JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
                redisConnectionFactory.setHostName("192.168.3.56");
                redisConnectionFactory.setPort(6379);
                redisConnectionFactory.afterPropertiesSet();
                StringRedisTemplateEX stringRedisTemplateEX = new StringRedisTemplateEX();
                stringRedisTemplateEX.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplateEX.afterPropertiesSet();
                return stringRedisTemplateEX;
            }
        };

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            worker.put(random.nextInt(10));
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10L));
        }
        worker.stop(1000L);
    }

    /**
     * 获取存储正在运行的任务的Redis.
     */
    protected abstract StringRedisTemplateEX taskRedisTemplate();

    private String getTaskkey(T data) {
        return "running:task:" + taskName + ":" + data.toString();
    }

    public int getMaximumQueueSize() {
        return maximumQueueSize;
    }

    public void setMaximumQueueSize(int maximumQueueSize) {
        this.maximumQueueSize = maximumQueueSize;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isReputInQueue() {
        return reputInQueue;
    }

    public void setReputInQueue(boolean reputInQueue) {
        this.reputInQueue = reputInQueue;
    }

    public void put(T t) {
        sureStart();
        if (isStop) {
            LOGGER.error("put", "已经停止");
            return;
        }
        try {
            queue.put(t);
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    public void putAll(Collection<T> list) {
        sureStart();
        if (isStop) {
            LOGGER.error("put", "已经停止");
            return;
        }
        try {
            queue.addAll(list);
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    public synchronized void init() {
        if (isInit) {
            return;
        }
        this.runningTasks = taskRedisTemplate();
        this.queue = new ArrayBlockingQueue<T>(maximumQueueSize);
        this.threadPool = buildThreadPool();
        this.taskPool = this.buildTaskPool();
        this.dispatchWorker.start();
        isInit = true;
    }

    public void stop(long timeOut) {
        try {
            isStop = true;
            while (!queue.isEmpty()) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10000L));
            }
            dispatchWorker.interrupt();
            threadPool.shutdown();
            threadPool.awaitTermination(timeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("stop", "停止失败", e);
        } finally {
            threadPool.shutdownNow();
        }
    }

    protected abstract void doProcess(T t);

    private boolean addToRunningTask(Task task) {
        return runningTasks.setIfAbsent(task.key, task.time, maxTaskRunTimeMs);
    }

    private ExecutorService buildThreadPool() {
        return new ThreadPoolExecutor(
                numberOfThreads,
                numberOfThreads,
                MINUTES_OF_THE_IDLE_LIFE,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(taskName + "-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    /**
     * 参照方法名.
     *
     * @param data
     * @return
     */

    protected GenericObjectPool<NonRepeatWorker<T>.Task> buildTaskPool() {

        BasePooledObjectFactory<Task> pooledObjectFactory = new BasePooledObjectFactory<Task>() {
            @Override
            public NonRepeatWorker<T>.Task create() throws Exception {
                return new Task();
            }

            @Override
            public PooledObject<NonRepeatWorker<T>.Task> wrap(NonRepeatWorker<T>.Task obj) {
                return new DefaultPooledObject<NonRepeatWorker<T>.Task>(obj);
            }
        };
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setBlockWhenExhausted(true);
        config.setMaxIdle(5);
        config.setMaxTotal(2000);
        return new GenericObjectPool<NonRepeatWorker<T>.Task>(pooledObjectFactory, config);
    }

    private boolean isRunning(T t) {
        return runningTasks.hasKey(getTaskkey(t));
    }

    private boolean process(T t) {
        int retryTimes = 0;
        do {
            try {
                doProcess(t);
                return true;
            } catch (Throwable e) {
                LOGGER.error("process", "批量处理任务发生异常", e);
                if (retryTimes++ < maxRetryTimes) {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e1) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        } while (retryTimes < maxRetryTimes);

        if (reputInQueue) {
            put(t);
        }
        return false;
    }

    private void removeFromRunningTask(Task task) {
        runningTasks.deleteIfEqual(getTaskkey(task.data), String.valueOf(task.time));
    }

    /**
     * 参照方法名.
     */
    private void sureStart() {
        if (!isInit) {
            init();
        }
    }

    class Task implements Runnable {

        private T data;
        private String key;
        private String time;

        /**
         */
        public Task() {

        }

        /**
         * @param data the data to set
         */
        public void setData(T data) {
            this.data = data;
            this.key = getTaskkey(data);
            this.time = String.valueOf(System.currentTimeMillis());
        }

        @Override
        public void run() {
            try {
                NonRepeatWorker.this.process(data);
            } catch (Exception e) {
                LOGGER.error("run", "运行任务" + taskName + "失败,任务数据：" + this.data, e);
            } finally {
                taskPool.returnObject(this);
                removeFromRunningTask(this);
            }
        }
    }
}
