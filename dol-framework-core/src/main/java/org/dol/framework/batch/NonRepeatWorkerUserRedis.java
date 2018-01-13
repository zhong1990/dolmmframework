package org.dol.framework.batch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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
import org.dol.framework.util.StringUtil;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 同一个数据处理在同一时间只有一个任务在处理
 * 如果存在等待处理的数据，新的数据请求被忽略
 * 使用Redis实现分布式控制.
 *
 * @param <String>
 * @author dolphin
 * @date 2017年5月11日 上午10:57:47
 */
public abstract class NonRepeatWorkerUserRedis {
    private static final RedisScript<String> TASK_TAKE_SCRIPT = new DefaultRedisScript<String>(
            "local taskData = redis.call('RPOP', KEYS[1])\n"
                    + "if taskData then\n"
                    + "redis.call('HDEL', KEYS[2],taskData)\n"
                    + "return taskData\n"
                    + "end\n"
                    + "return ''",
            String.class);
    private static final RedisScript<String> TASK_ADD_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('HEXISTS',KEYS[2],ARGV[1])==0 then\n"
                    + "redis.call('LPUSH',KEYS[1],ARGV[1])\n"
                    + "redis.call('HSET',KEYS[2], ARGV[1],ARGV[2])\n"
                    + "end\n"
                    + "return 'OK'",
            String.class);
    private static final Logger LOGGER = Logger.getLogger(NonRepeatWorkerUserRedis.class);
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
    private volatile boolean isInit = false;
    private ExecutorService threadPool;
    private StringRedisTemplateEX runningTasks;
    private GenericObjectPool<Task> taskPool;
    private boolean isStop;
    private List<String> taskKeys;
    private String runningTaskKey;
    private Thread dispatchWorker = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String t = takeTaskData();
                    if (StringUtil.EMPTY_STRING.equals(t)) {
                        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100L));
                        continue;
                    }
                    Task task = taskPool.borrowObject();
                    task.setData(t);
                    if (addToRunningTask(task)) {
                        threadPool.submit(task);
                        continue;
                    } else {
                        taskPool.returnObject(task);
                        put(t);
                    }
                }
            } catch (InterruptedException ignore) {
                // ignore
                // e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    });

    public NonRepeatWorkerUserRedis() {

    }

    public NonRepeatWorkerUserRedis(String taskName) {
        this.taskName = taskName;
    }

    public NonRepeatWorkerUserRedis(
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
        final AtomicInteger countter = new AtomicInteger(0);
        NonRepeatWorkerUserRedis worker = new NonRepeatWorkerUserRedis("test") {

            @Override
            protected void doProcess(String t) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1000L));
                System.out.println(t);
                countter.incrementAndGet();
            }

            @Override
            protected StringRedisTemplateEX taskRedisTemplate() {
                JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
                redisConnectionFactory.setHostName("192.168.3.102");
                redisConnectionFactory.setPassword("mcredispasSwd");
                redisConnectionFactory.setPort(6379);
                redisConnectionFactory.setDatabase(11);
                redisConnectionFactory.setUsePool(true);
                redisConnectionFactory.afterPropertiesSet();
                StringRedisTemplateEX stringRedisTemplateEX = new StringRedisTemplateEX();
                stringRedisTemplateEX.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplateEX.afterPropertiesSet();
                return stringRedisTemplateEX;
            }
        };
        worker.init();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            String vString = String.valueOf(random.nextInt(100));
            worker.put(vString);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10L));
        }
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(30000L));
        worker.stop(1000L);
        System.out.println("run task count = " + countter.get());
    }

    /**
     * 获取存储正在运行的任务的Redis.
     */
    protected abstract StringRedisTemplateEX taskRedisTemplate();

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

    public void put(String t) {
        sureStart();
        if (isStop) {
            LOGGER.error("put", "已经停止");
            return;
        }
        try {
            addTask(t);
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    /**
     * 参照方法名.
     *
     * @param t
     */
    private void addTask(String t) {
        runningTasks.execute(TASK_ADD_SCRIPT, taskKeys, t, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    private String takeTaskData() {
        return runningTasks.execute(TASK_TAKE_SCRIPT, taskKeys, "100");
    }

    public void putAll(Collection<String> list) {
        sureStart();
        if (isStop) {
            LOGGER.error("put", "已经停止");
            return;
        }
        try {
            for (String t : list) {
                addTask(t);
            }
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    public synchronized void init() {
        if (isInit) {
            return;
        }
        this.taskKeys = Arrays.asList("task:nonrepeat:task:" + this.taskName + ":queue", "task:nonrepeat:task:" + this.taskName + ":keys");
        this.runningTaskKey = "task:nonrepeat:task:" + this.taskName + ":running";
        this.runningTasks = taskRedisTemplate();
        this.threadPool = buildThreadPool();
        this.taskPool = this.buildTaskPool();
        this.dispatchWorker.start();
        isInit = true;
    }

    public void stop(long timeOut) {
        try {
            isStop = true;
            dispatchWorker.interrupt();
            threadPool.shutdown();
            threadPool.awaitTermination(timeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("stop", "停止失败", e);
        } finally {
            threadPool.shutdownNow();
        }
    }

    protected abstract void doProcess(String t);

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

    protected GenericObjectPool<NonRepeatWorkerUserRedis.Task> buildTaskPool() {

        BasePooledObjectFactory<Task> pooledObjectFactory = new BasePooledObjectFactory<Task>() {
            @Override
            public NonRepeatWorkerUserRedis.Task create() throws Exception {
                return new Task();
            }

            @Override
            public PooledObject<NonRepeatWorkerUserRedis.Task> wrap(NonRepeatWorkerUserRedis.Task obj) {
                return new DefaultPooledObject<NonRepeatWorkerUserRedis.Task>(obj);
            }
        };
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setBlockWhenExhausted(true);
        config.setMaxIdle(5);
        config.setMaxTotal(2000);
        return new GenericObjectPool<NonRepeatWorkerUserRedis.Task>(pooledObjectFactory, config);
    }

    private boolean process(String t) {
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

    private boolean addToRunningTask(Task task) {
        return runningTasks.setIfAbsent(runningTaskKey + "" + task.data, task.time, maxTaskRunTimeMs);
    }

    private void removeFromRunningTask(Task task) {
        runningTasks.deleteIfEqual(runningTaskKey + "" + task.data, task.time);
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

        private String data;
        private String time;

        /**
         */
        public Task() {

        }

        /**
         * @param data the data to set
         */
        public void setData(String data) {
            this.data = data;
            this.time = String.valueOf(System.currentTimeMillis());
        }

        @Override
        public void run() {
            try {
                NonRepeatWorkerUserRedis.this.process(data);
            } catch (Exception e) {
                LOGGER.error("run", "运行任务" + taskName + "失败,任务数据：" + this.data, e);
            } finally {
                taskPool.returnObject(this);
                removeFromRunningTask(this);
            }
        }
    }
}
