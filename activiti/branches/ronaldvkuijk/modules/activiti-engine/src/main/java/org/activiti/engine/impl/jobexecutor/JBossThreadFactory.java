package org.activiti.engine.impl.jobexecutor;

import java.util.concurrent.ThreadFactory;


public class JBossThreadFactory implements ThreadFactory {

  public Thread newThread(Runnable r) {
    //System.out.println("QueueRunner");
    return new Thread(r, "QueueRunner");

  }

  
  
}
