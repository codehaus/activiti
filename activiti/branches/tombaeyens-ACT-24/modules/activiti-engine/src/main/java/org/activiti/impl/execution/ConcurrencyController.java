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

package org.activiti.impl.execution;

import java.util.ArrayList;
import java.util.List;

import org.activiti.pvm.Activity;
import org.activiti.pvm.ActivityExecution;
import org.activiti.pvm.Transition;


/**
 * @author Tom Baeyens
 */
public class ConcurrencyController {

  ActivityExecution execution;

  public ConcurrencyController(ActivityExecution execution) {
    this.execution = (ExecutionImpl) execution;
  }

  public void inactivate() {
    execution.setActive(false);
  }

  public List<ActivityExecution> findInactiveConcurrentExecutions(Activity activity) {
    List<ActivityExecution> inactiveConcurrentExecutions = new ArrayList<ActivityExecution>();
    if (execution.isConcurrent()) {
      for (ActivityExecution concurrentExecution: execution.getParent().getExecutions()) {
        if (!concurrentExecution.isActive()) {
          inactiveConcurrentExecutions.add(concurrentExecution);
        }
      }
    } else {
      if (!execution.isActive()) {
        inactiveConcurrentExecutions.add(execution);
      }
    }
    return inactiveConcurrentExecutions;
  }

  public void takeAll(List<Transition> transitions, List<ActivityExecution> joinedExecutions) {
    transitions = new ArrayList<Transition>(transitions);
    joinedExecutions = new ArrayList<ActivityExecution>(joinedExecutions);
    
    Activity activity = execution.getActivity();
    ActivityExecution concurrentRoot = (execution.isConcurrent() ? execution.getParent() : execution);
    
    if ( (transitions.size()==1)
         && (joinedExecutions.size()==concurrentRoot.getExecutions().size())
       ) {

      for (ActivityExecution prunedExecution: joinedExecutions) {
        prunedExecution.end();
      }

      concurrentRoot.setActive(true);
      concurrentRoot.setActivity(activity);
      concurrentRoot.setConcurrent(false);
      concurrentRoot.take(transitions.get(0));

    } else {
      joinedExecutions.remove(concurrentRoot);
      while (!transitions.isEmpty()) {
        Transition outgoingTransition = transitions.remove(0);
        ActivityExecution outgoingExecution = null;
        
        if (joinedExecutions.isEmpty()) {
          outgoingExecution = concurrentRoot.createExecution();
        } else {
          outgoingExecution = joinedExecutions.remove(0);
        }
        
        outgoingExecution.setActive(true);
        outgoingExecution.setActivity(activity);
        outgoingExecution.setConcurrent(true);
        outgoingExecution.take(outgoingTransition);
      }

      for (ActivityExecution prunedExecution: joinedExecutions) {
        prunedExecution.end();
      }
    }
  }
}
