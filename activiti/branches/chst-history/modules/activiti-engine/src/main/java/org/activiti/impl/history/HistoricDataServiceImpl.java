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

import org.activiti.HistoricDataService;
import org.activiti.history.HistoricActivityInstance;
import org.activiti.history.HistoricProcessInstance;
import org.activiti.impl.event.ActivityInstanceEndedEvent;
import org.activiti.impl.event.ActivityInstanceStartedEvent;
import org.activiti.impl.event.ProcessInstanceEndedEvent;
import org.activiti.impl.event.ProcessInstanceStartedEvent;
import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.interceptor.CommandExecutor;
import org.activiti.impl.time.Clock;
import org.activiti.pvm.event.ProcessEventBus;
import org.activiti.pvm.event.ProcessEventConsumer;

/**
 * @author Christian Stettler
 */
// TODO: define/implement semantics of historic data: only completed processes vs. also ongoing ones
public class HistoricDataServiceImpl implements HistoricDataService {

  private final CommandExecutor commandExecutor;

  public HistoricDataServiceImpl(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  public void registerEventConsumers(ProcessEventBus processEventBus) {
    // TODO: where/how to register historic data service with event bus?
    processEventBus.subscribe(new ProcessInstanceStartedEventConsumer(), ProcessInstanceStartedEvent.class);
    processEventBus.subscribe(new ProcessInstanceEndedEventConsumer(), ProcessInstanceEndedEvent.class);
    processEventBus.subscribe(new ActivityStartedEventConsumer(), ActivityInstanceStartedEvent.class);
    processEventBus.subscribe(new ActivityEndedEventConsumer(), ActivityInstanceEndedEvent.class);
  }

  public HistoricProcessInstance findHistoricProcessInstance(final String processInstanceId) {
    return commandExecutor.execute(new Command<HistoricProcessInstance>() {
      public HistoricProcessInstance execute(CommandContext commandContext) {
        return commandContext.getPersistenceSession().findHistoricProcessInstance(processInstanceId);
      }
    });
  }

  public HistoricActivityInstance findHistoricActivityInstance(final String activityInstanceId, final String processInstanceId) {
    return commandExecutor.execute(new Command<HistoricActivityInstance>() {
      public HistoricActivityInstance execute(CommandContext commandContext) {
        return commandContext.getPersistenceSession().findHistoricActivityInstance(activityInstanceId, processInstanceId);
      }
    });
  }

  private static void ensureCommandContextAvailable() {
    if (CommandContext.getCurrentCommandContext() == null) {
      throw new IllegalStateException("History events can only be processed in the context of a command execution");
    }
  }

  private static class ProcessInstanceStartedEventConsumer implements ProcessEventConsumer<ProcessInstanceStartedEvent> {
    public void consumeEvent(ProcessInstanceStartedEvent event) {
      ensureCommandContextAvailable();

      String processInstanceId = event.getProcessInstanceId();
      String processDefinitionId = event.getProcessDefinitionId();
      Date startTime = Clock.getCurrentTime();

      HistoricProcessInstanceImpl historicProcessInstance = new HistoricProcessInstanceImpl(processInstanceId, processDefinitionId, startTime);

      CommandContext.getCurrentCommandContext().getPersistenceSession().saveHistoricProcessInstance(historicProcessInstance);
    }
  }

  private static class ProcessInstanceEndedEventConsumer implements ProcessEventConsumer<ProcessInstanceEndedEvent> {
    public void consumeEvent(ProcessInstanceEndedEvent event) {
      ensureCommandContextAvailable();

      HistoricProcessInstanceImpl historicProcessInstance = CommandContext.getCurrentCommandContext().getPersistenceSession().findHistoricProcessInstance(event.getProcessInstanceId());

      if (historicProcessInstance == null) {
        throw new IllegalArgumentException("No historic process instance found for process instance id '" + event.getProcessInstanceId() + "'");
      }

      Date endTime = Clock.getCurrentTime();
      historicProcessInstance.markEnded(endTime);

      CommandContext.getCurrentCommandContext().getPersistenceSession().saveHistoricProcessInstance(historicProcessInstance);
    }
  }

  private static class ActivityStartedEventConsumer implements ProcessEventConsumer<ActivityInstanceStartedEvent> {
    public void consumeEvent(ActivityInstanceStartedEvent event) {
      ensureCommandContextAvailable();

      String activityInstanceId = event.getActivityInstanceId();
      String activityId = event.getActivityId();
      String activityName = event.getPayload().getName();
      String activityType = event.getPayload().getType();
      String processInstanceId = event.getProcessInstanceId();
      String processDefinitionId = event.getProcessDefinitionId();
      Date startTime = Clock.getCurrentTime();

      HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl(activityInstanceId, activityId, activityName, activityType, processInstanceId, processDefinitionId, startTime);

      CommandContext.getCurrentCommandContext().getPersistenceSession().saveHistoricActivityInstance(historicActivityInstance);
    }
  }

  private static class ActivityEndedEventConsumer implements ProcessEventConsumer<ActivityInstanceEndedEvent> {
    public void consumeEvent(ActivityInstanceEndedEvent event) {
      ensureCommandContextAvailable();

      HistoricActivityInstanceImpl historicActivityInstance = CommandContext.getCurrentCommandContext().getPersistenceSession().findHistoricActivityInstance(event.getActivityInstanceId(), event.getProcessInstanceId());

      if (historicActivityInstance == null) {
        throw new IllegalArgumentException("No historic activity instance found for activity instance id '" + event.getActivityInstanceId() + "' and process instance id '" + event.getProcessInstanceId() + "'");
      }

      historicActivityInstance.markEnded(Clock.getCurrentTime());

      CommandContext.getCurrentCommandContext().getPersistenceSession().saveHistoricActivityInstance(historicActivityInstance);
    }
  }
}
