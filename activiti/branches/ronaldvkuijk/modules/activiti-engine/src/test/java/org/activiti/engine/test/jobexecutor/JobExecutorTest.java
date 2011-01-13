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
package org.activiti.engine.test.jobexecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.JobQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.MessageSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.jobexecutor.IThreadPoolExecutor;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.runtime.JobEntity;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.runtime.JobQuery;

import antlr.DumpASTVisitor;

/**
 * @author Tom Baeyens
 */
public class JobExecutorTest extends JobExecutorTestCase {

  private static Logger log = Logger.getLogger(JobExecutorTest.class.getName());

  public void testBasicJobExecutorOperation() throws Exception {

    JobExecutor jobExecutor = ((ProcessEngineImpl) processEngine).getJobExecutor();
    jobExecutor.setLockTimeInMillis(30 * 1000);
    // Does not need to be high, since if they are retrieved to the max, a next
    // query is automaticaly done.
    // Should not be to low either since it results in to many queries. 50 seems
    // a nice mix for a stp system.
    jobExecutor.setMaxJobsPerAcquisition(250);
    jobExecutor.setCorePoolSize(18);
    jobExecutor.setMaxPoolSize(18);
    jobExecutor.setQueueSize(750);
    jobExecutor.hasAcquisitionPerQueue(true);
    // jobExecutor.start();

    log.info("Starting adding work");
    int outerLoop = 6;
    int innerLoop = 6;
    int messagesInLoop = 6;

    long start = new Date().getTime();

    Set<String> expectedMessages = createMessages(outerLoop, innerLoop);

    // if (i < (outerLoop -1)) {
    // //jobExecutor.dumpStatistics();
    // Long sleep = 50L;
    // Thread.sleep(sleep);
    // log.info("Sleep "+ sleep);
    // }

    log.info("All threads started");

    // Thread.sleep(10000);
    // jobExecutor.getLogicalQueue("Default").pause();
    // log.info("Pauzing");
    // Thread.sleep(20000);
    // jobExecutor.getLogicalQueue("Default").resume();
    // log.info("Resuming");

    waitForAllJobsToAppear(innerLoop * outerLoop * messagesInLoop, 10000L, 1000L);
    
    waitForJobExecutorToProcessAllJobs(40000L, 1000L);
    Set<String> messages = new HashSet<String>(tweetHandler.getMessages());

    assertEquals(outerLoop * innerLoop * messagesInLoop, messages.size());
    // compares the content and since the content of each message is unique,
    // they either fully match or not after internally both being sorted
    assertEquals(expectedMessages, messages);
    long end = new Date().getTime();
    System.out.println("Completed " + outerLoop * innerLoop * messagesInLoop + " in " + (end - start) + "ms, so "
            + new Long(outerLoop * innerLoop * messagesInLoop * 1000) / (end - start) + " jobs/s");
  }

  private Set<String> createMessages(int outerLoop, int innerLoop) {

    Set<String> expectedMessages = new HashSet<String>();
    ThreadPoolExecutor tpe = new ThreadPoolExecutor(40, 40, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(7200));

    for (int i = 0; i < outerLoop; i++) {
      for (int j = 0; j < innerLoop; j++) {

        int count = i * innerLoop + j;

        tpe.execute(new doCommand(count));

        expectedMessages.add("message-one:" + count);
        expectedMessages.add("message-two:" + count);
        // expectedMessages.add("message-three:" + count);

        expectedMessages.add("High-message-one:" + count);
        expectedMessages.add("High-message-two:" + count);

        expectedMessages.add("Low-message-one:" + count);
        expectedMessages.add("Low-message-two:" + count);

        // expectedMessages.add("timer-one:" + count);
        // expectedMessages.add("timer-two:" + count);
      }
    }
    return expectedMessages;

  }

  class doCommand implements Runnable {

    int number;

    public doCommand(int i) {
      this.number = i;
    }

    public void run() {

      CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();

      commandExecutor.execute(new Command<Void>() {

        public Void execute(CommandContext commandContext) {
          MessageSession messageSession = commandContext.getMessageSession();

          // Going to default queue
          
          messageSession.send(createTweetMessage("message-one:" + number));
          messageSession.send(createTweetMessage("message-two:" + number));

          messageSession.send(createTweetMessage("High", "High-message-one:" + number));
          messageSession.send(createTweetMessage("High", "High-message-two:" + number));
          
          messageSession.send(createTweetMessage("Low", "Low-message-one:" + number));
          messageSession.send(createTweetMessage("Low", "Low-message-two:" + number));

          // TimerSession timerSession = commandContext.getTimerSession();
          //
          // Date date = new Date(new Date().getTime() + (new
          // Random()).nextInt(30000));
          // timerSession.schedule(createTweetTimer("timer-one:" + number,
          // date));
          // timerSession.schedule(createTweetTimer("timer-two:" + number,
          // date));



          return null;
        }
      });
    }
  }

  public void SlowTestLockTimeOutMessage() {

    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();

    JobExecutor jobExecutor = ((ProcessEngineImpl) processEngine).getJobExecutor();
    jobExecutor.setLockTimeInMillis(30 * 1000);
    jobExecutor.setMaxJobsPerAcquisition(250); // 90 if 'per queue' if
                                               // hasAcquisitionPerQueue is
                                               // true, 250 otherwise
    jobExecutor.setCorePoolSize(30);
    jobExecutor.setMaxPoolSize(30);
    jobExecutor.setQueueSize(600);
    jobExecutor.hasAcquisitionPerQueue(false);
    jobExecutor.start();

    commandExecutor.execute(new Command<Void>() {

      public Void execute(CommandContext commandContext) {

        System.out.println("0");

        MessageSession messageSession = commandContext.getMessageSession();

        messageSession.send(createTweetMessage("message-one"));
        messageSession.send(createTweetMessage("message-two"));
        return null;
      }
    });

    commandExecutor.execute(new Command<Void>() {

      public Void execute(CommandContext commandContext) {

        System.out.println("1");

        String lockOwner = UUID.randomUUID().toString();
        int lockTimeInMillis = 20 * 1000;

        List<JobEntity> jobs = commandContext.getRuntimeSession().findNextJobsToExecutePerQueue("Default", lockOwner, new Page(0, 2));

        for (JobEntity job : jobs) {
          List<String> jobIds = new ArrayList<String>();

          if (job != null) {
            job.setLockOwner(lockOwner);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(ClockUtil.getCurrentTime());
            gregorianCalendar.add(Calendar.MILLISECOND, lockTimeInMillis);
            job.setLockExpirationTime(gregorianCalendar.getTime());
            jobIds.add(job.getId());
          }
        }

        return null;
      }
    });

    commandExecutor.execute(new Command<Void>() {

      public Void execute(CommandContext commandContext) {

        System.out.println("2");

        List<JobEntity> jobs = new ArrayList<JobEntity>();

        try {
          Timer timer = new Timer();
          InteruptTask task = new InteruptTask(Thread.currentThread());
          timer.schedule(task, 60000);
          try {
            while (jobs.size() == 0) {
              System.out.println("loop");
              Thread.sleep(5000);
              // jobs =
              // commandContext.getRuntimeSession().findNextJobsToExecutePerQueue("Default",
              // new Page(0, 2));
              JobQuery jc = new JobQueryImpl().queue("Default");
              jobs = commandContext.getRuntimeSession().findJobEntitiesByQueryCriteria((JobQueryImpl) jc, new Page(0, 2));
            }
          } catch (InterruptedException e) {
          } finally {
            timer.cancel();
          }
          if (jobs.size() == 0) {
            throw new ActivitiException("time limit of " + 60000 + " was exceeded");
          } else if (jobs.size() == 2) {
            jobs.get(0).delete();
            jobs.get(1).delete();
          }

        } catch (Exception e) {

        }
        return null;
      }
    });

  }

  private static class InteruptTask extends TimerTask {

    protected boolean timeLimitExceeded = false;
    protected Thread thread;

    public InteruptTask(Thread thread) {
      this.thread = thread;
    }
    public boolean isTimeLimitExceeded() {
      return timeLimitExceeded;
    }
    public void run() {
      timeLimitExceeded = true;
      thread.interrupt();
    }
  }

}
