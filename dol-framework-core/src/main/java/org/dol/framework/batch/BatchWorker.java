package org.dol.framework.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.dol.framework.logging.Logger;

public abstract class BatchWorker<T> {

    private static final Logger LOGGER = Logger.getLogger(BatchWorker.class);

    private static final int MINUTES_OF_THE_IDLE_LIFE = 5;
    private int batchSize = 100;
    private String taskName = "BatckTask";
    private int maxRetryTimes = 0;
    private boolean reputInQueue = false;
    private int numberOfThreads = 1;
    private int maximumQueueSize = 10000;

    private ArrayBlockingQueue<T> queue = null;
    private volatile boolean isActive = false;
    private volatile boolean isReady = false;
    private ExecutorService threadPool;

    public BatchWorker() {

    }

    public BatchWorker(String taskName) {
        this.taskName = taskName;
    }

    public BatchWorker(
            String taskName,
            int maximumQueueSize,
            int batchSize,
            int numberOfThreads,
            int maxRetryTimes,
            boolean reputInQueue) {
        this.taskName = taskName;
        this.maximumQueueSize = maximumQueueSize;
        this.batchSize = batchSize;
        this.numberOfThreads = numberOfThreads;
        this.maxRetryTimes = maxRetryTimes;
        this.reputInQueue = reputInQueue;
    }

    private ExecutorService buildThreadPool() {
        return new ThreadPoolExecutor(numberOfThreads, numberOfThreads, MINUTES_OF_THE_IDLE_LIFE, TimeUnit.MINUTES,
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

    protected abstract void doProcess(List<T> list);

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
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

    public boolean isReputInQueue() {
        return reputInQueue;
    }

    public void setReputInQueue(boolean reputInQueue) {
        this.reputInQueue = reputInQueue;
    }

    private boolean process(List<T> list) {
        if (list.isEmpty()) {
            return true;
        }
        try {
            int retryTimes = 0;
            while (isActive && !Thread.currentThread().isInterrupted()) {
                try {
                    doProcess(list);
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
                        return false;
                    }
                }
            }
            return false;
        } finally {
            list.clear();
        }
    }

    public void put(T t) {
        if (!isReady) {
            LOGGER.error("put", "推送任务到队列失败", "worker尚未开始，或者已经停止");
            start();
        }
        try {
            queue.put(t);
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    public void putAll(Collection<T> list) {
        if (!isReady) {
            LOGGER.error("put", "推送任务到队列失败", "worker尚未开始，或者已经停止");
            start();
        }
        try {
            queue.addAll(list);
        } catch (Throwable e) {
            LOGGER.error("put", "推送任务到队列发生异常:" + e.getMessage(), e);
        }
    }

    public synchronized void start() {
        if (isActive) {
            return;
        }
        isActive = true;
        queue = new ArrayBlockingQueue<T>(maximumQueueSize);
        threadPool = buildThreadPool();
        for (int i = 0; i < numberOfThreads; i++) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isActive && !Thread.currentThread().isInterrupted()) {
                            List<T> batch = new ArrayList<T>(batchSize);
                            int currentCount = 0;
                            for (T t = queue.take(); t != null; t = queue.poll(100L, TimeUnit.MILLISECONDS)) {
                                batch.add(t);
                                if (++currentCount >= batchSize) {
                                    break;
                                }
                            }
                            process(batch);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("run", "线程中断，可能系统停止了");
                    } catch (Throwable e) {
                        LOGGER.error("run", "批量处理任务发生异常", e);
                    }
                }
            });
        }
        isReady = true;
    }

    public void stop() {
        try {
            isReady = false;
            isActive = false;
            threadPool.shutdown();
            threadPool.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("stop", "停止失败", e);
        } finally {
            threadPool.shutdownNow();
        }
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
}
