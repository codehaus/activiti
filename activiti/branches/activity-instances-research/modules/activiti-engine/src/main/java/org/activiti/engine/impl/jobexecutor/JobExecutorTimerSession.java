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

import java.util.Date;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cfg.RuntimeSession;
import org.activiti.engine.impl.cfg.TimerSession;
import org.activiti.engine.impl.cfg.TransactionState;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.runtime.ActivityInstanceEntity;
import org.activiti.engine.impl.persistence.runtime.TimerEntity;
import org.activiti.engine.impl.util.ClockUtil;


/**
 * @author Tom Baeyens
 */
public class JobExecutorTimerSession implements TimerSession, Session {

  private final CommandContext commandContext;
  private final JobExecutor jobExecutor;
  
  public JobExecutorTimerSession() {
    this.commandContext = CommandContext.getCurrent();
    this.jobExecutor = commandContext.getProcessEngineConfiguration().getJobExecutor();
  }

  public void schedule(TimerEntity timer) {
    Date duedate = timer.getDuedate();
    if (duedate==null) {
      throw new ActivitiException("duedate is null");
    }
    
    commandContext
      .getRuntimeSession()
      .insertJob(timer);
    
    // Check if this timer fires before the next time the job executor will check for new timers to fire.
    // This is highly unlikely because normally waitTimeInMillis is 5000 (5 seconds)
    // and timers are usually set further in the future
    int waitTimeInMillis = jobExecutor.getWaitTimeInMillis();
    if (duedate.getTime() < (ClockUtil.getCurrentTime().getTime()+waitTimeInMillis)) {
      // then notify the job executor.
      commandContext
        .getTransactionContext()
        .addTransactionListener(TransactionState.COMMITTED, new MessageAddedNotification(jobExecutor));
    }
  }

  public void cancelTimers(ActivityInstanceEntity activityInstance) {
    RuntimeSession runtimeSession = commandContext.getRuntimeSession();
    List<TimerEntity> timers = runtimeSession.findTimersByActivityInstanceId(null); 
    for (TimerEntity timer: timers) {
      runtimeSession.deleteJob(timer.getId());
    }
  }

  public void close() {
  }

  public void flush() {
  }
}
