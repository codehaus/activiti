package org.activiti.engine.impl.jobexecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public interface IThreadPoolExecutor extends ExecutorService {

  public abstract AtomicInteger getTasksInProcess();

  public abstract int getPoolSize();

  public abstract int getLargestPoolSize();

  public abstract long getTaskCount();
  
  public abstract int getActiveCount();

  public abstract BlockingQueue<Runnable> getQueue();

  public abstract long getCompletedTaskCount();

  public void setRejectedExecutionHandler(RejectedExecutionHandler handler);

  public abstract boolean isPaused();

  public abstract void pause();

  public abstract void resume();

}