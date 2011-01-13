package org.activiti.engine.impl.jobexecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor implements IThreadPoolExecutor {

  private static Logger log = Logger.getLogger(PausableThreadPoolExecutor.class.getName());
  
  AtomicInteger counter = new AtomicInteger(0);
  private boolean isPaused;
  private ReentrantLock pauseLock = new ReentrantLock();
  private Condition unpaused = pauseLock.newCondition();

  public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  protected void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
    pauseLock.lock();
    try {
      while (isPaused)
        unpaused.await();
    } catch (InterruptedException ie) {
      t.interrupt();
    } finally {
      pauseLock.unlock();
    }
  }

  public void pause() {
    pauseLock.lock();
    log.log(Level.INFO, "Pausing Queue");
    try {
      isPaused = true;
    } finally {
      pauseLock.unlock();
    }
  }

  public void resume() {
    pauseLock.lock();
    log.log(Level.INFO, "Resuming Queue");
    try {
      isPaused = false;
      unpaused.signalAll();
    } finally {
      pauseLock.unlock();
    }
  }

  @Override
  protected void afterExecute(Runnable command, Throwable t) {
    super.afterExecute(command, t);
    counter.getAndAdd(((ExecuteJobsRunnable) command).getSize());
  }

  /* (non-Javadoc)
   * @see org.activiti.engine.impl.jobexecutor.IThreadPoolExecutor#execute(java.lang.Runnable)
   */
  @Override
  public void execute(Runnable command) {
    super.execute(command);
  }

  public int getExecutedJobs() {
    return counter.get();
  }
  
  /* (non-Javadoc)
   * @see org.activiti.engine.impl.jobexecutor.IThreadPoolExecutor#getTasksInProcess()
   */
  public AtomicInteger getTasksInProcess() {
    return new AtomicInteger(0);
  }

  public boolean isPaused() {
    return isPaused;
  }
  
}
