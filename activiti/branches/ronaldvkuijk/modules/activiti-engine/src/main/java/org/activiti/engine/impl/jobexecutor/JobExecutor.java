/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.jobexecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

/**
 * Manager class in charge of all background / asynchronous processing. You
 * should generally only have one of these per Activiti instance in a JVM. In
 * clustered situations, you can have multiple of these running against the same
 * queue + pending job list. Uses a {@link NotifyingBlockingThreadPoolExecutor}
 * internally.
 */
public class JobExecutor {

  private static Logger log = Logger.getLogger(JobExecutor.class.getName());

  protected CommandExecutor commandExecutor;
  protected boolean isAutoActivate = false;

  protected int maxJobsPerAcquisition = 3;
  protected int waitTimeInMillis = 5 * 1000;
  protected String lockOwner = UUID.randomUUID().toString();
  protected int lockTimeInMillis = 5 * 60 * 1000;
  protected int queueSize = 5;
  protected int corePoolSize = 3;
  protected boolean acquisitionPerQueue = true;
  private int maxPoolSize = 10;
  final String DEFAULT = "Default";
  final String TIMERS = "Timers";

  protected JobAcquisitionThread jobAcquisitionThread;
  // TODO RKU make a 'concurrent' hashmap and validate if its usage is ok (seems
  // to be, mostly reading)
  protected HashMap<String, IThreadPoolExecutor> threadPoolExecutors = new HashMap<String, IThreadPoolExecutor>();
  protected boolean isActive = false;

  Callable<Boolean> blockingTimeoutCallback = new Callable<Boolean>() {

    public Boolean call() throws Exception {
      // log.log(Level.INFO, "*** Still waiting for task insertion... ***");
      // return true; // keep waiting
      log.log(Level.WARNING, "*** Not waiting for task insertion... ***");
      return false; // do not keep waiting
    }
  };

  
  public JobExecutor() {
 
    //CHECK Should always be available, even if jobExecutor is not started. Should be paused by default I think!
    if (this.threadPoolExecutors.size() == 0) {
   
      this.threadPoolExecutors.put(DEFAULT, createQueue(DEFAULT));
      this.threadPoolExecutors.put(TIMERS, createQueue(TIMERS));
     
    }
    
    pause();
   
  }
  
  public synchronized void start() {

    if (isActive) {
      // Already started, nothing to do
      log.info("Ignoring duplicate JobExecutor start invocation");
      return;
    } else {

      isActive = true;
      
      if (jobAcquisitionThread == null) {
        jobAcquisitionThread = new JobAcquisitionThread(this);
      }
      
      // To 'recreate', mainly for tests where a jobexecutor is shutdown and reused...
      if (this.threadPoolExecutors.size() == 0) {
        
        this.threadPoolExecutors.put(DEFAULT, createQueue(DEFAULT));
        this.threadPoolExecutors.put(TIMERS, createQueue(TIMERS));
       
      }
      
      createAdditionalQueuesFromExistingJobs();
      
      // Create our pending jobs fetcher
      log.fine("JobExecutor is starting the JobAcquisitionThread");
      

      jobAcquisitionThread.start();
      
      // Open all queues
      resume();

    }
  }

  public synchronized void shutdown() {
    
    if (!isActive) {
      log.info("Ignoring request to shut down non-active JobExecutor");
      return;
    }

    log.info("Shutting down the JobExecutor");

    /* 
     * Close the pending jobs task first so no jobs will be added
     * We have multiple threadpools that are shut down sequentially,
     * So shutting down the acquisition (retry/timers) thread first
     * is a good idea. But the loop below should be different to.
     * Maybe the queue should be emptied first?
     */
    jobAcquisitionThread.shutdown();
    
    // Ask the thread pools to finish and exit
    // CHECK Should this be done in parallel? See remark above...
    Set<String> queues = threadPoolExecutors.keySet();
    for (String queue : queues) {
      log.info("Shutting down queue: " + queue);
      try {
        this.threadPoolExecutors.get(queue).shutdown();
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Waits for 1 minute to finish all currently executing jobs
      try {
        this.threadPoolExecutors.get(queue).awaitTermination(60L, TimeUnit.SECONDS);
        dumpStatistics(queue);
      } catch (InterruptedException e) {
        throw new ActivitiException("Timeout during shutdown of job executor for queue " + queue
                + " The current running jobs could not end withing 60 seconds after shutdown operation.", e);
      }

    }

    isActive = false;

    // Clear references to the threadpoolexecutors;
    threadPoolExecutors.clear();

    jobAcquisitionThread = null;

  }

  /**
   * Used to hint that new work exists on the queue, and that the
   * {@link JobAcquisitionThread} should probably re-check for jobs.
   */
  public void enqueue(String queueName, String jobId) {
    if (isActive && jobAcquisitionThread != null && jobAcquisitionThread.isActive()) {
      if (queueName != null) {
        IThreadPoolExecutor tpe = getLogicalQueue(queueName);
        if (tpe == null) {
          log.info("Runtime creating queue: " + queueName);
          tpe = createQueue(queueName);
          threadPoolExecutors.put(queueName, tpe);
        }
        if (!tpe.isPaused()) {
          List<String> jobs = new ArrayList<String>();
          jobs.add(jobId);
          executeJobs(queueName, jobs); // only one!!!!
        }
      }
    }
  }

  public void executeJobs(String queueName, List<String> jobIds) throws RejectedExecutionException {

    // TODO: RejectedExecutionException handling! (how...
    // NotifyingBlockingThreadExecutor has already parts)
    IThreadPoolExecutor threadPoolExecutor = threadPoolExecutors.get(queueName);
    threadPoolExecutor.execute(new ExecuteJobsRunnable(commandExecutor, jobIds));
    // TODO might not be needed anymore
    // jobAcquisitionThread.jobWasAdded();

  }

  // Private methods //////////////////////////////////////////////////////

  private IThreadPoolExecutor createQueue(String queueName) {

    IThreadPoolExecutor threadPoolExecutor;
      threadPoolExecutor = new NotifyingBlockingThreadPoolExecutor(queueName, corePoolSize, maxPoolSize, queueSize, 1000L, TimeUnit.MILLISECONDS, 1000L,
              TimeUnit.MILLISECONDS, blockingTimeoutCallback);
    return threadPoolExecutor;
  }
  private void createAdditionalQueuesFromExistingJobs() {

    log.log(Level.INFO, "Creating queues");
    commandExecutor.execute(new Command<Void>() {
      
      public Void execute(CommandContext commandContext) {

        List<String> queues = commandContext.getRuntimeSession().findJobQueueNames();
        for (String queueName : queues) {
          if (!("Default".equals(queueName) || "Timers".equals(queueName))) {
            threadPoolExecutors.put(queueName, createQueue(queueName));
          }
        }

        return null;
      }
    });
  }

  // getters and setters //////////////////////////////////////////////////////

  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }

  public int getWaitTimeInMillis() {
    return waitTimeInMillis;
  }

  public void setWaitTimeInMillis(int waitTimeInMillis) {
    this.waitTimeInMillis = waitTimeInMillis;
  }

  public int getLockTimeInMillis() {
    return lockTimeInMillis;
  }

  public void setLockTimeInMillis(int lockTimeInMillis) {
    this.lockTimeInMillis = lockTimeInMillis;
  }

  public int getQueueSize() {
    return queueSize;
  }

  public void setQueueSize(int queueSize) {
    this.queueSize = queueSize;
  }

  public int getCorePoolSize() {
    return corePoolSize;
  }

  public void setCorePoolSize(int corePoolSize) {
    this.corePoolSize = corePoolSize;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  public JobAcquisitionThread getJobAcquisitionThread() {
    return jobAcquisitionThread;
  }

  public void setJobAcquisitionThread(JobAcquisitionThread jobAcquisitionThread) {
    this.jobAcquisitionThread = jobAcquisitionThread;
  }

  public boolean isAcquisitionPerQueue() {
    return this.acquisitionPerQueue;
  }

  public void hasAcquisitionPerQueue(boolean acquisitionPerQueue) {
    this.acquisitionPerQueue = acquisitionPerQueue;
  }

  public IThreadPoolExecutor getLogicalQueue(String queueName) {
    return threadPoolExecutors == null ? null : this.threadPoolExecutors.get(queueName);
  }

  public Set<String> getQueueNames() {
    return this.threadPoolExecutors.keySet();
  }

  public List<String> getOpenQueueNames() {
    List<String> openQueues = new ArrayList<String>();
    if (threadPoolExecutors != null) {
      for (String queueName : this.threadPoolExecutors.keySet()) {
        if (this.threadPoolExecutors.get(queueName) != null && !this.threadPoolExecutors.get(queueName).isPaused()) {
          openQueues.add(queueName);
        }
      }
    }
    return openQueues;
  }

  public boolean isActive() {
    return isActive;
  }

  public int getMaxJobsPerAcquisition() {
    return maxJobsPerAcquisition;
  }

  public void setMaxJobsPerAcquisition(int maxJobsPerAcquisition) {
    this.maxJobsPerAcquisition = maxJobsPerAcquisition;
  }

  public String getLockOwner() {
    return lockOwner;
  }

  public void setLockOwner(String lockOwner) {
    this.lockOwner = lockOwner;
  }

  public boolean isAutoActivate() {
    return isAutoActivate;
  }

  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  public void setAutoActivate(boolean isAutoActivate) {
    this.isAutoActivate = isAutoActivate;
  }

  public String dumpStatistics() {
    if (threadPoolExecutors == null)
      return "No queues present";
    Set<String> queueNames = threadPoolExecutors.keySet();
    StringBuffer sb = new StringBuffer();
    sb.append("Full statistics: \n");
    for (String queueName : queueNames) {
      sb.append("\t");
      sb.append(dumpStatistics(queueName));
      sb.append("\n");
    }
    return sb.substring(0, sb.length() - 1);
  }

  public String dumpStatistics(String queueName) {
    IThreadPoolExecutor tpe = threadPoolExecutors.get(queueName);
    if (tpe == null) return "Name: " + queueName + " does not exist";
    return ("Name: " + queueName + (tpe.isPaused() ? "(paused)" : "(running)") + 
            ",\tThreads (pool/act/maxused): " + tpe.getPoolSize() + "/"
            + tpe.getActiveCount() + "/" + tpe.getLargestPoolSize() + ",\tJobs (subm/busy/compl) " + tpe.getTaskCount() + "/" + tpe.getTasksInProcess() + "/"
            + tpe.getCompletedTaskCount() + ",\tQueue (used/left): " + tpe.getQueue().size() + "/" + tpe.getQueue().remainingCapacity());
  }
  
  public void pause() {
    if (jobAcquisitionThread != null) { 
      jobAcquisitionThread.shutdown();
    }
    Set<String> queueNames = threadPoolExecutors.keySet();
    for (String queueName : queueNames) {
      threadPoolExecutors.get(queueName).pause();
    }
  }
  
  public void resume() {
    Set<String> queueNames = threadPoolExecutors.keySet();
    for (String queueName : queueNames) {
      threadPoolExecutors.get(queueName).resume();
    }
    if (jobAcquisitionThread == null) { 
      jobAcquisitionThread = new JobAcquisitionThread(this);
    }
  }
  
}
