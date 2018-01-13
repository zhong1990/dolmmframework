/**
 * dol-studio-web
 * ConcurrencyWorker.java
 * org.dol.studio.util
 * TODO
 *
 * @author dolphin
 * @date 2016年12月24日 下午7:01:29
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * ClassName:ConcurrencyWorker <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年12月24日 下午7:01:29 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public abstract class BatchJob<T, E> {

    private final AtomicInteger successTaskCount = new AtomicInteger(0);
    private final AtomicInteger finishedTaskCount = new AtomicInteger(0);
    private int threadNum;
    private CountDownLatch countter;
    private int totalTaskNum = 0;
    private Thread thread;
    private List<Future<E>> resultFutures;
    private transient JobStatus status = JobStatus.NOT_START;
    private long startTimeMs;
    private long endTimeMs;

    public BatchJob(int threadNum) {
        this.threadNum = threadNum;
    }

    public static void main(String[] args) throws InterruptedException {
        BatchJob<Integer, Integer> batchJob = new BatchJob<Integer, Integer>(20) {
            @Override
            protected Integer doProcess(Integer t) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100L));
                return t;
            }
        };
        int taskCount = 1002;
        List<Integer> dataList = new ArrayList<Integer>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            dataList.add(i);
        }
        batchJob.asynProcess(dataList);
        batchJob.waitAndPrintProgress(1000L);
    }

    public AtomicInteger getSuccessTaskCount() {
        return successTaskCount;
    }

    public AtomicInteger getFinishedTaskCount() {
        return finishedTaskCount;
    }

    public void asynProcess(final List<T> dataList) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    process(dataList);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public List<Future<E>> process(List<T> dataList) throws InterruptedException {
        startTimeMs = System.currentTimeMillis();
        resultFutures = new ArrayList<Future<E>>(totalTaskNum);
        totalTaskNum = dataList.size();
        countter = new CountDownLatch(totalTaskNum);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        status = JobStatus.RUNNING;
        try {
            for (T t : dataList) {
                Future<E> future = executorService.submit(getTask(t));
                resultFutures.add(future);
            }
            countter.await();

        } finally {
            status = JobStatus.COMLETED;
            executorService.shutdownNow();
            executorService = null;
            endTimeMs = System.currentTimeMillis();
            System.out.println(getReport());
        }
        return resultFutures;
    }

    public String getReport() {

        return String.format(getTimestamp() + ":所有任务执行完成\n\t\t任务数：%1d\n\t\t并发数：%2d\n\t\t成功数:%3d\n\t\t开始时间：%4s\n\t\t结束时间：%5s\n\t\t用时:%6d毫秒",
                totalTaskNum,
                threadNum,
                successTaskCount.get(),
                getTimestamp(startTimeMs),
                getTimestamp(endTimeMs),
                (endTimeMs - startTimeMs));
    }

    protected String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    protected void print(String message) {
        System.out.println(getTimestamp() + ":" + message);
    }

    private String getTimestamp(long start2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
        return sdf.format(new Date(start2));
    }

    public Callable<E> getTask(final T t) {
        return new Callable<E>() {
            @Override
            public E call() throws Exception {
                try {
                    E e = doProcess(t);
                    successTaskCount.incrementAndGet();
                    return e;
                } finally {
                    finishedTaskCount.incrementAndGet();
                    countter.countDown();
                }
            }
        };
    }

    public float printProgress() {
        if (status == JobStatus.NOT_START) {
            System.out.println(getTimestamp() + "----finshed  0.00%");
            return 0.00f;
        }
        float completePercent = finishedTaskCount.get() * 100f / totalTaskNum;
        System.out.printf(getTimestamp() + ": %.2f%%\n", completePercent);
        return completePercent;
    }

    public boolean isComplete() {
        return status == JobStatus.COMLETED;
    }

    protected abstract E doProcess(T t) throws Exception;

    public List<Future<E>> getResultFutures() {
        return resultFutures;
    }

    public void setResultFutures(List<Future<E>> resultFutures) {
        this.resultFutures = resultFutures;
    }

    /**
     * 等待任务完成，并打印任务进度
     *
     * @param intervalMs
     */
    public void waitAndPrintProgress(long intervalMs) {
        long naos = TimeUnit.MILLISECONDS.toNanos(intervalMs);
        while (!isComplete()) {
            printProgress();
            LockSupport.parkNanos(naos);
        }
    }

    static enum JobStatus {
        NOT_START, RUNNING, COMLETED,
    }
}
