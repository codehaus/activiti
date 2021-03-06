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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.activiti.engine.impl.cfg.MessageSession;
import org.activiti.engine.impl.cfg.TransactionState;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.runtime.JobEntity;
import org.activiti.engine.impl.runtime.MessageEntity;
import org.activiti.engine.impl.util.ClockUtil;


/**
 * @author Tom Baeyens
 */
public class JobExecutorMessageSession implements Session, MessageSession {

  private final CommandContext commandContext;
  private final JobExecutor jobExecutor;
  
  public JobExecutorMessageSession(CommandContext commandContext, JobExecutor jobExecutor) {
    this.commandContext = commandContext;
    this.jobExecutor = jobExecutor;
  }

  public JobExecutorMessageSession() {
    this.commandContext = CommandContext.getCurrent();
    this.jobExecutor = commandContext.getProcessEngineConfiguration().getJobExecutor();
  }

  public void send(MessageEntity message) {
    
    if (message.getQueue() == null) {
      message.setQueue("Default");
    }
    
    // Immediately pin the message to this server... and for now pass a lock expiration time to it to...
    if (message.getLockOwner() == null) {
      message.setLockOwner(jobExecutor.getLockOwner());
      GregorianCalendar gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTime(ClockUtil.getCurrentTime());
      
      //FIX make convenience method for this combined or
      if (jobExecutor.isActive() && (jobExecutor.getLogicalQueue(message.getQueue()) == null || !jobExecutor.getLogicalQueue(message.getQueue()).isPaused())) {
        gregorianCalendar.add(Calendar.MILLISECOND, jobExecutor.lockTimeInMillis);
        message.setLockExpirationTime(gregorianCalendar.getTime());
      } else {
        message.setDuedate(gregorianCalendar.getTime());
      }
    }
    
    commandContext
      .getDbSqlSession()
      .insert(message);
    
    commandContext
      .getTransactionContext()
      .addTransactionListener(TransactionState.COMMITTED, new MessageAddedNotification(jobExecutor, message.getQueue(), message.getId()));
  }

  public void close() {
  }

  public void flush() {
  }
}
