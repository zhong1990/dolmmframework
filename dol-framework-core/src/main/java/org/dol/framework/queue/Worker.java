package org.dol.framework.queue;

public abstract class Worker<T> implements Runnable {
    private T job;

    public T getJob() {
        return job;
    }

    public void setJob(T job) {
        this.job = job;
    }


}
