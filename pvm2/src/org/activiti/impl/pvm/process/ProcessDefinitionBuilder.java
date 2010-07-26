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

package org.activiti.impl.pvm.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.activiti.impl.pvm.spi.ActivityBehaviour;



/**
 * @author Tom Baeyens
 */
public class ProcessDefinitionBuilder {

  protected ProcessDefinition processDefinition = new ProcessDefinition();
  protected Stack<Scope> scopeStack = new Stack<Scope>();
  protected Transition transition;
  protected List<Object[]> unresolvedTransitions = new ArrayList<Object[]>();
  
  public ProcessDefinitionBuilder() {
    scopeStack.push(processDefinition);
  }

  public ProcessDefinitionBuilder startActivity(String name) {
    Activity activity = scopeStack.peek().createActivity();
    activity.id = name;
    scopeStack.push(activity);
    
    transition = null;
    
    return this;
  }
  
  public ProcessDefinitionBuilder endActivity() {
    scopeStack.pop();

    transition = null;
    
    return this;
  }
  
  public ProcessDefinitionBuilder initial() {
    processDefinition.initial = getActivity();
    return this;
  }
  
  public ProcessDefinitionBuilder transition(String destinationActivityName) {
    Activity activity = getActivity();
    
    transition = new Transition();
    transition.source = activity;
    activity.outgoingTransitions.add(transition);
    
    unresolvedTransitions.add(new Object[]{transition, destinationActivityName});
    
    return this;
  }

  public ProcessDefinitionBuilder name(String transitionName) {
    transition.name = transitionName;
    return this;
  }

  public ProcessDefinitionBuilder behaviour(ActivityBehaviour activityBehaviour) {
    getActivity().activityBehaviour = activityBehaviour;
    return this;
  }

  public ProcessDefinition buildProcessDefinition() {
    for (Object[] unresolvedTransition: unresolvedTransitions) {
      Transition transition = (Transition) unresolvedTransition[0];
      String destinationActivityName = (String) unresolvedTransition[1];
      transition.destination = processDefinition.findActivity(destinationActivityName);
      if (transition.destination == null) {
        throw new RuntimeException("destination '"+destinationActivityName+"' not found");
      }
    }
    return processDefinition;
  }
  
  protected Activity getActivity() {
    return (Activity) scopeStack.peek(); 
  }
}
