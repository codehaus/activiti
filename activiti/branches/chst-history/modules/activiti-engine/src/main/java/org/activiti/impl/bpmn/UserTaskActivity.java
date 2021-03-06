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
package org.activiti.impl.bpmn;

import org.activiti.impl.el.ExpressionManager;
import org.activiti.impl.event.ActivityInstanceEndedEvent;
import org.activiti.impl.event.ActivityInstanceStartedEvent;
import org.activiti.impl.execution.ExecutionImpl;
import org.activiti.impl.task.TaskDefinition;
import org.activiti.impl.task.TaskImpl;
import org.activiti.pvm.ActivityExecution;

/**
 * activity implementation for the user task.
 * 
 * @author Joram Barrez
 */
public class UserTaskActivity extends TaskActivity {

  private final TaskDefinition taskDefinition;
  private final ExpressionManager expressionManager;

  public UserTaskActivity(ExpressionManager expressionManager, TaskDefinition taskDefinition) {
    this.expressionManager = expressionManager;
    this.taskDefinition = taskDefinition;
  }

  public void execute(ActivityExecution execution) throws Exception {
    execution.fireEvent(new ActivityInstanceStartedEvent(execution.getProcessInstance(), execution.getActivity(), createActivityInstanceId(execution)));

    TaskImpl task = TaskImpl.createAndInsert();
    task.setExecution(execution);

    if (taskDefinition.getName() != null) {
      String name = evaluateExpression(taskDefinition.getName(), execution);
      task.setName(name);
    }

    if (taskDefinition.getDescription() != null) {
      String description = evaluateExpression(taskDefinition.getDescription(), execution);
      task.setDescription(description);
    }

    handleAssignments(task, execution);
  }

  public void event(ActivityExecution execution, Object event) throws Exception {
    execution.fireEvent(new ActivityInstanceEndedEvent(execution.getProcessInstance(), execution.getActivity(), createActivityInstanceId(execution)));

    leave(execution);
  }

  protected void handleAssignments(TaskImpl task, ActivityExecution execution) {
    if (taskDefinition.getAssignee() != null) {
      task.setAssignee(evaluateExpression(taskDefinition.getAssignee(), execution));
    }

    if (!taskDefinition.getCandidateGroupIds().isEmpty()) {
      for (String groupId : taskDefinition.getCandidateGroupIds()) {
        task.addCandidateGroup(evaluateExpression(groupId, execution));
      }
    }

    if (!taskDefinition.getCandidateUserIds().isEmpty()) {
      for (String userId : taskDefinition.getCandidateUserIds()) {
        task.addCandidateUser(evaluateExpression(userId, execution));
      }
    }
  }

  protected String evaluateExpression(String expr, ActivityExecution execution) {
    // FIXME: downcast
    return (String) expressionManager.createValueExpression(expr).getValue((ExecutionImpl) execution);
  }

  private static String createActivityInstanceId(ActivityExecution execution) {
    // TODO: this does not work for loops where the same execution is executing the one activity several times
    // TODO: this id changes if end signal is sent be different execution as the one that started the activity
    return execution.getId() + ":" + execution.getActivity().getId();
  }
}
