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
package org.activiti.engine.impl.cmd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.jobexecutor.AcquiredJobs;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.runtime.JobEntity;
import org.activiti.engine.impl.runtime.MessageEntity;
import org.activiti.engine.impl.runtime.TimerEntity;
import org.activiti.engine.impl.util.ClockUtil;

/**
 * @author Nick Burch
 * @author Ronald van Kuijk
 */
public class AcquireJobsCmd implements Command<AcquiredJobs> {

  private static Logger log = Logger.getLogger(AcquireJobsCmd.class.getName());

  private final JobExecutor jobExecutor;

  public AcquireJobsCmd(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

  public AcquiredJobs execute(CommandContext commandContext) {

    int maxJobsPerAcquisition = jobExecutor.getMaxJobsPerAcquisition();
    int free;
    int jobsToAcquire;

    AcquiredJobs acquiredJobs = new AcquiredJobs();

    int totalJobsAcquired = 0;

    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Current status: " + jobExecutor.dumpStatistics());
    }
    if (jobExecutor.isAcquisitionPerQueue()) {

      for (String openQueue : jobExecutor.getOpenQueueNames()) {

        free = jobExecutor.getLogicalQueue(openQueue).getQueue().remainingCapacity();
        jobsToAcquire = free > maxJobsPerAcquisition ? maxJobsPerAcquisition : free;
        if (openQueue != "Timers") {
          List<JobEntity> jobs = commandContext.getRuntimeSession().findNextJobsToExecutePerQueue(openQueue, jobExecutor.getLockOwner(),
                  new Page(0, maxJobsPerAcquisition));
          lockJobs(openQueue, acquiredJobs, jobs);
          totalJobsAcquired = totalJobsAcquired + jobs.size();
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Jobs acquired for '" + openQueue + "': " + jobs.size());
          }
          // TODO, something per queue
          if ((jobsToAcquire < maxJobsPerAcquisition && jobs.size() != 0) || jobs.size() == maxJobsPerAcquisition)
            acquiredJobs.setRetryImmediate(true);
        }
      }

      List<JobEntity> jobs = commandContext.getRuntimeSession().findUnlockedJobTimersByDuedate(ClockUtil.getCurrentTime(), new Page(0, maxJobsPerAcquisition));
      lockJobs("Timers", acquiredJobs, jobs);
      totalJobsAcquired = totalJobsAcquired + jobs.size();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Jobs acquired for 'Timers': " + jobs.size());
      }
    } else {
      List<JobEntity> jobs = commandContext.getRuntimeSession().findNextJobsToExecute(new Page(0, maxJobsPerAcquisition));
      lockJobs(null, acquiredJobs, jobs);
      totalJobsAcquired = jobs.size();
      if (totalJobsAcquired == maxJobsPerAcquisition)
        acquiredJobs.setRetryImmediate(true);
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Total Jobs Acquired: " + totalJobsAcquired);
    }
    return acquiredJobs;
  }

  void lockJobs(String openQueue, AcquiredJobs acquiredJobs, List<JobEntity> jobs) {

    if (jobs.size() == 0)
      return;

    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Locking " + jobs.size() + " retrieved jobs for " + openQueue);
    }
    String lockOwner = jobExecutor.getLockOwner();
    int lockTimeInMillis = jobExecutor.getLockTimeInMillis();

    String localQueue = openQueue;
    for (JobEntity job : jobs) {
      List<String> jobIds = new ArrayList<String>();

      if (job != null) {
        job.setLockOwner(lockOwner);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(ClockUtil.getCurrentTime());
        gregorianCalendar.add(Calendar.MILLISECOND, lockTimeInMillis);
        job.setLockExpirationTime(gregorianCalendar.getTime());
        jobIds.add(job.getId());
        if (job.isExclusive()) {
          // TODO acquire other exclusive jobs for the same process instance.
        }
      }
      if (openQueue == null) {
        localQueue = (job instanceof MessageEntity) ? ((MessageEntity) job).getQueue() : (job instanceof TimerEntity ? "Timers" : "Default");
      } else {
        localQueue = openQueue;
      }

      // This will only be true if all jobs are retrieved in one queuery.
      // Otherwise these messaged for closed queues are not even retrieved
      // Job IS locked to prevent it showing up in the next 'acquire'...
      // Should be done differently if performance bottleneck of retrieving per
      // queue remains.
      // Maybe pausing/resuming queueus should only be allowed if retrieval is
      // also done per queue. Due to the changed way op putting job's in the
      // queue this can be done.
      // getLogicalQueue is null if queue does not exist yet.
      if (jobExecutor.getLogicalQueue(localQueue) == null || !jobExecutor.getLogicalQueue(localQueue).isPaused()) {
        acquiredJobs.addJobIds(localQueue, jobIds);
      }
    }
  }
}
