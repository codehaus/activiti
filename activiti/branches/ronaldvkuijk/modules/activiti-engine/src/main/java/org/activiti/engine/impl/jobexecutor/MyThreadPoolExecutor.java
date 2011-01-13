package org.activiti.engine.impl.jobexecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadPoolExecutor extends ThreadPoolExecutor implements IThreadPoolExecutor {


  public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  
  /* (non-Javadoc)
   * @see org.activiti.engine.impl.jobexecutor.IThreadPoolExecutor#getTasksInProcess()
   */
  public AtomicInteger getTasksInProcess() {
    return new AtomicInteger(0);
  }

  public boolean isPaused() {
    return false;
  }


  public void pause() {
    // Does nothing (should throw a NotImplementedException or something...)
  }


  public void resume() {
 // Does nothing (should throw a NotImplementedException or something...)
  }
  
}
