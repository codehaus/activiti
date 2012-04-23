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

package org.activiti.engine.impl.pvm.runtime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.StartAuthorizationException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.ScopeImpl;


/**
 * @author Tom Baeyens
 * @author Daniel Meyer
 */
public class AtomicOperationProcessStart extends AbstractEventAtomicOperation {

  @Override
  protected ScopeImpl getScope(InterpretableExecution execution) {
    return execution.getProcessDefinition();
  }

  @Override
  protected String getEventName() {
    return org.activiti.engine.impl.pvm.PvmEvent.EVENTNAME_START;
  }

  @Override
  protected void eventNotificationsCompleted(InterpretableExecution execution) {
    ProcessDefinitionImpl processDefinition = execution.getProcessDefinition();
    StartingExecution startingExecution = execution.getStartingExecution();
    checkStartAuthorization(execution, processDefinition);
    List<ActivityImpl> initialActivityStack = processDefinition.getInitialActivityStack(startingExecution.getInitial());  
    execution.setActivity(initialActivityStack.get(0));    
    execution.performOperation(PROCESS_START_INITIAL);
  }
  
  protected void checkStartAuthorization(InterpretableExecution execution, ProcessDefinitionImpl processDefinition) {
    Set<String> authorizedUsers = new HashSet<String>();
    Set<String> authorizedGroups = new HashSet<String>();

    /*
     * if no starter user or group is defined, then no security policy is
     * defined. Allow access for backward compatibility
     */
    if (processDefinition.getCandidateStarterGroupIdExpressions().isEmpty() && processDefinition.getCandidateStarterUserIdExpressions().isEmpty())
      return;

    convertStartersToList(execution, processDefinition, authorizedUsers, authorizedGroups);

    // check if user is in authorized users
    String userId = Authentication.getAuthenticatedUserId();

    if (authorizedUsers.contains(userId))
      return;

    // check if user is in authorized groups
    CommandContext commandContext = Context.getCommandContext();
    List<Group> userGroups = commandContext.getUserManager().findGroupsByUser(userId);
    Iterator<Group> groupIterator = userGroups.iterator();
    while (groupIterator.hasNext()) {
      Group group = groupIterator.next();
      if (authorizedGroups.contains(group.getId()))
        return;
    }

    throw new StartAuthorizationException("user \"" + userId + "\"has not enough rights to start process");

  }

  void convertStartersToList(InterpretableExecution execution, ProcessDefinitionImpl processDefinition, Set<String> authorizedUsers, Set<String> authorizedGroups) {
    
    if (!processDefinition.getCandidateStarterUserIdExpressions().isEmpty()) {
      for (Expression userIdExpr : processDefinition.getCandidateStarterUserIdExpressions()) {
        Object value = userIdExpr.getExpressionText();
        if (value instanceof String) {
          authorizedUsers.add((String) value);
        } else {
          throw new ActivitiException("Expression did not resolve to a string");
        }
      }
    }

    if (!processDefinition.getCandidateStarterGroupIdExpressions().isEmpty()) {
      for (Expression groupIdExpr : processDefinition.getCandidateStarterGroupIdExpressions()) {
        Object value = groupIdExpr.getExpressionText();
        if (value instanceof String) {
          authorizedGroups.add((String) value);
        } else {
          throw new ActivitiException("Expression did not resolve to a string ");
        }
      }
    }
  }
}
