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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.AcquireJobsCmd;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.runtime.TimerEntity;
import org.activiti.engine.impl.util.ClockUtil;

/**
 * Background thread responsible for retrieving the list of Jobs currently
 * awaiting processing from the queue, and passing them to the
 * {@link JobExecutor} to be run. There should only ever be one of these per
 * {@link JobExecutor}. Note that in a clustered Environment, there can be
 * multiple of these, so we need locking/transactions to ensure we don't fetch
 * Jobs someone else already has.
 */
public class JobAcquisitionThread extends Thread {

  private static Logger log = Logger.getLogger(JobAcquisitionThread.class.getName());

  private final AcquireJobsCmd acquireJobsCmd;

  private JobExecutor jobExecutor;
  private boolean isActive = false;
  private boolean isJobAdded = false;

  public JobAcquisitionThread(JobExecutor jobExecutor) {
    super("JobAcquisitionThread");
    this.jobExecutor = jobExecutor;
    this.acquireJobsCmd = new AcquireJobsCmd(jobExecutor);
  }

  public void run() {
    log.info(getName() + " starting to acquire jobs");
    this.isActive = true;

    CommandExecutor commandExecutor = jobExecutor.getCommandExecutor();
    long millisToWait = 0;
    float waitIncreaseFactor = 2;
    long maxWait = 60 * 1000;

    while (isActive) {

      try {
        AcquiredJobs acquiredJobs = commandExecutor.execute(acquireJobsCmd);

        for (String queueName : acquiredJobs.getJobIdsList().keySet()) {
          // Non-existing queues are 'created' dynamically
          for (String jobId : acquiredJobs.getJobIdListForQueue(queueName)) {
            // Only 1 for now ;-)
            List<String> jobIdList = new ArrayList<String>();
            jobIdList.add(jobId);
            jobExecutor.executeJobs(queueName, jobIdList);
          }

        }

        // if all jobs were executed
        millisToWait = jobExecutor.getWaitTimeInMillis();
        // TODO optimize per queue or something...
        if (!acquiredJobs.getRetryImmediate()) {

          if (log.isLoggable(Level.FINE)) {
            log.log(Level.INFO, "Waiting since nowhere max is reached and free in queue was higher than max");
          }
          isJobAdded = false;

          // check if the next timer should fire before the normal sleep time is
          // over
          // TODO -1000 since otherwise there is a strange burst in the second
          // before the job is released
          // It thinks there is a job available, but retrieving it does not
          // work. Only after this second...
          Date duedate = new Date(ClockUtil.getCurrentTime().getTime() + millisToWait - 1000);
          List<TimerEntity> nextTimers = commandExecutor.execute(new GetUnlockedTimersByDuedateCmd(duedate, new Page(0, 1)));

          if (!nextTimers.isEmpty()) {
            long millisTillNextTimer = nextTimers.get(0).getDuedate().getTime() - ClockUtil.getCurrentTime().getTime();
            if (millisTillNextTimer < millisToWait) {
              millisToWait = millisTillNextTimer;
            }
          }

        } else {
          millisToWait = 0;
        }

        if (log.isLoggable(Level.FINE)) {
          log.info("Millis to wait: " + millisToWait);
        }

      } catch (Exception e) {
        log.log(Level.SEVERE, "exception during job acquisition: " + e.getMessage(), e);
        millisToWait *= waitIncreaseFactor;
        if (millisToWait > maxWait) {
          millisToWait = maxWait;
        }
      }

      if ((millisToWait > 0) && (!isJobAdded)) {
        try {
          if (log.isLoggable(Level.FINE)) {
            log.fine("job acquisition thread sleeping for " + millisToWait + " millis");
          }
          Thread.sleep(millisToWait);
          if (log.isLoggable(Level.FINE)) {
            log.fine("job acquisition thread woke up");
          }
        } catch (InterruptedException e) {
          {
            log.fine("job acquisition wait interrupted");
          }
        }
      }
    }
    log.info(getName() + " stopped");
  }

  public void jobWasAdded() {
    isJobAdded = true;
    log.fine("Job was added. Interrupting " + this);
    interrupt();
  }

  /**
   * Triggers a shutdown
   */
  public void shutdown() {
    if (isActive) {
      log.info(getName() + " is shutting down");
      isActive = false;
      interrupt();
      try {
        join();
      } catch (InterruptedException e) {
        log.log(Level.WARNING, "Interruption while shutting down " + this.getClass().getName(), e);
      }
    }
  }

  public JobExecutor getJobExecutor() {
    return jobExecutor;
  }

  public boolean isActive() {
    return isActive;
  }
}
