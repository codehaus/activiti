/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.impl.history;

import java.util.Date;

import org.activiti.ProcessInstance;
import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.interceptor.CommandExecutor;
import org.activiti.impl.time.Clock;
import org.activiti.pvm.Activity;

/**
 * @author Christian Stettler
 */
public class HistoricDataServiceImpl implements HistoricDataService {

  private final CommandExecutor commandExecutor;

  public HistoricDataServiceImpl(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  public HistoricProcessInstance createHistoricProcessInstance(final ProcessInstance processInstance) {
    return commandExecutor.execute(new Command<HistoricProcessInstance>() {
      public HistoricProcessInstance execute(CommandContext commandContext) {
        String processInstanceId = processInstance.getId();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        Date startTime = Clock.getCurrentTime();

        HistoricProcessInstanceImpl historicProcessInstance = new HistoricProcessInstanceImpl(processInstanceId, processDefinitionId, startTime);

        commandContext.getPersistenceSession().saveHistoricProcessInstance(historicProcessInstance);

        return historicProcessInstance;
      }
    });
  }

  public HistoricProcessInstance findHistoricProcessInstance(final String processInstanceId) {
    return commandExecutor.execute(new Command<HistoricProcessInstance>() {
      public HistoricProcessInstance execute(CommandContext commandContext) {
        return commandContext.getPersistenceSession().findHistoricProcessInstance(processInstanceId);
      }
    });
  }

  public HistoricProcessInstance markHistoricProcessInstanceEnded(final String processInstanceId, final String endStateName) {
    return commandExecutor.execute(new Command<HistoricProcessInstance>() {
      public HistoricProcessInstance execute(CommandContext commandContext) {
        MutableHistoricProcessInstance historicProcessInstance = commandContext.getPersistenceSession().findHistoricProcessInstance(processInstanceId);

        if (historicProcessInstance == null) {
          throw new IllegalArgumentException("No historic process instance found for process instance id '" + processInstanceId + "'");
        }

        Date endTime = Clock.getCurrentTime();
        historicProcessInstance.markEnded(endTime, endStateName);

        commandContext.getPersistenceSession().saveHistoricProcessInstance(historicProcessInstance);

        return historicProcessInstance;
      }
    });
  }

  public HistoricActivityInstance createHistoricActivityInstance(final Activity activity, final ProcessInstance processInstance) {
    return commandExecutor.execute(new Command<HistoricActivityInstance>() {
      public HistoricActivityInstance execute(CommandContext commandContext) {
        String activityId = activity.getId();
        String activityName = activity.getName();
        String activityType = activity.getType();
        String processInstanceId = processInstance.getId();
        String processDefinitionId = processInstance.getProcessDefinitionId();
        Date startTime = Clock.getCurrentTime();

        HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl(activityId, activityName, activityType, processInstanceId, processDefinitionId, startTime);

        commandContext.getPersistenceSession().saveHistoricActivityInstance(historicActivityInstance);

        return historicActivityInstance;
      }
    });
  }

  public HistoricActivityInstance findHistoricActivityInstance(final String activityId, final String processInstanceId) {
    return commandExecutor.execute(new Command<HistoricActivityInstance>() {
      public HistoricActivityInstance execute(CommandContext commandContext) {
        return commandContext.getPersistenceSession().findHistoricActivityInstance(activityId, processInstanceId);
      }
    });
  }

  public HistoricActivityInstance markHistoricActivityInstanceEnded(final String activityId, final String processInstanceId) {
    return commandExecutor.execute(new Command<HistoricActivityInstance>() {
      public HistoricActivityInstance execute(CommandContext commandContext) {
        MutableHistoricActivityInstance historicActivityInstance = commandContext.getPersistenceSession().findHistoricActivityInstance(activityId, processInstanceId);

        if (historicActivityInstance == null) {
          throw new IllegalArgumentException("No historic activity instance found for activity id '" + activityId + "' and process instance id '" + processInstanceId + "'");
        }

        historicActivityInstance.markEnded(Clock.getCurrentTime());

        commandContext.getPersistenceSession().saveHistoricActivityInstance(historicActivityInstance);

        return historicActivityInstance;
      }
    });
  }
}
