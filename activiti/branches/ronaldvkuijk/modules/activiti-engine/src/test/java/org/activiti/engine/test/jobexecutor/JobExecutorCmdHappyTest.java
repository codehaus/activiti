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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.cmd.AcquireJobsCmd;
import org.activiti.engine.impl.cmd.ExecuteJobsCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.jobexecutor.AcquiredJobs;
import org.activiti.engine.impl.jobexecutor.ExecuteJobsRunnable;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.runtime.MessageEntity;
import org.activiti.engine.impl.runtime.TimerEntity;
import org.activiti.engine.impl.util.ClockUtil;

/**
 * @author Tom Baeyens
 */
public class JobExecutorCmdHappyTest extends JobExecutorTestCase {

  public void testJobCommandsWithMessage() {
    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    JobExecutor jobExecutor = processEngineConfiguration.getJobExecutor();
    jobExecutor.hasAcquisitionPerQueue(true);
    // so job goes to DB and not to threadpool
    jobExecutor.getLogicalQueue("Default").pause();
    
    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetMessage("i'm coding a test");
        commandContext.getMessageSession().send(message);
        return message.getId();
      }
    });
    
    // resume so the job can be retrieved... but do NOT start the jobExcecutor!!
    jobExecutor.getLogicalQueue("Default").resume();
    
    AcquiredJobs acquiredJobs = commandExecutor.execute(new AcquireJobsCmd(jobExecutor));
    Map<String, List<String>> jobIdsList = acquiredJobs.getJobIdsList();
    assertEquals(1, jobIdsList.size());

    List<String> expectedJobIds = new ArrayList<String>();
    expectedJobIds.add(jobId);
    
    List<String> jobIds = jobIdsList.get("Default");

    assertEquals(expectedJobIds, new ArrayList<String>(jobIds));
    assertEquals(0, tweetHandler.getMessages().size());

    commandExecutor.execute(new ExecuteJobsCmd(jobId));

    assertEquals("i'm coding a test", tweetHandler.getMessages().first());
    assertEquals(1, tweetHandler.getMessages().size());
  }

  static final long SOME_TIME = 928374923546L;
  static final long SECOND = 1000;

  public void testJobCommandsWithTimer() {
    // clock gets automatically reset in LogTestCase.runTest
    ClockUtil.setCurrentTime(new Date(SOME_TIME));

    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    JobExecutor jobExecutor = processEngineConfiguration.getJobExecutor();
    // so job goes to DB and not to threadpool... 
    //TODO Document at top of the testcase.
    jobExecutor.getLogicalQueue("Timers").pause();

    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        TimerEntity timer = createTweetTimer("i'm coding a test", new Date(SOME_TIME + (10 * SECOND)));
        commandContext.getTimerSession().schedule(timer);
        return timer.getId();
      }
    });

    // resume so the job can be retrieved... but do NOT start the jobExcecutor!!
    jobExecutor.getLogicalQueue("Timers").resume();
    AcquiredJobs acquiredJobs = commandExecutor.execute(new AcquireJobsCmd(jobExecutor));
    Map<String,List<String>> jobIdsList = acquiredJobs.getJobIdsList();
    
    assertEquals(0, jobIdsList.size());

    List<String> expectedJobIds = new ArrayList<String>();

    ClockUtil.setCurrentTime(new Date(SOME_TIME + (20 * SECOND)));

    acquiredJobs = commandExecutor.execute(new AcquireJobsCmd(jobExecutor));
    jobIdsList = acquiredJobs.getJobIdsList();
    assertEquals(1, jobIdsList.size());

    List<String> jobIds = jobIdsList.get("Timers");

    expectedJobIds.add(jobId);
    assertEquals(expectedJobIds, new ArrayList<String>(jobIds));

    assertEquals(0, tweetHandler.getMessages().size());

    commandExecutor.execute(new ExecuteJobsCmd(jobId));

    assertEquals("i'm coding a test", tweetHandler.getMessages().first());
    assertEquals(1, tweetHandler.getMessages().size());
  }
}
