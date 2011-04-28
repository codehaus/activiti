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

package org.activiti.service.api.process.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.Condition;
import org.activiti.service.api.process.definition.Transition;
import org.activiti.service.impl.json.JsonIgnore;
import org.activiti.service.impl.json.JsonMap;


/**
 * @author Tom Baeyens
 */
public class ActivityInstance extends ScopeInstance {

  String activityDefinitionId;
  
  @JsonIgnore
  ActivityDefinition activityDefinition;
  
  @JsonMap(type=Trigger.class, key="id")
  Map<String, Trigger> triggers = new HashMap<String, Trigger>();
  
  boolean isFinished = false;
  
  public void start() {
    activityDefinition
      .getActivityType()
      .start(this);
  }
  
  public void finished() {
    isFinished = true;
  }
  
  public void end() {
    List<AtomicOperation> atomicOperations = new ArrayList<AtomicOperation>(); 
    
    for (Transition transition: activityDefinition.getTransitions()) {
      Condition condition = transition.getCondition();
      if (condition==null || condition.evaluate(this)) {
        atomicOperations.add(new TakeTransition(this, transition));
      }
    }
    
    parentScopeInstance.getActivityInstances().remove(this);
    
    flowInstance.getEngine().execute(atomicOperations);
  }
  
  // getters and setters //////////////////////////////////////////////////////
  
  public ActivityDefinition getActivityDefinition() {
    if (activityDefinition==null && activityDefinitionId!=null) {
      activityDefinition = flowInstance.getFlowDefinition().findActivityDefinition(activityDefinitionId);
    }
    return activityDefinition;
  }
  
  public ActivityInstance setActivityDefinition(ActivityDefinition activityDefinition) {
    this.activityDefinition = activityDefinition;
    this.activityDefinitionId = activityDefinition.getId();
    return this;
  }
  
  public ActivityInstance setParentScopeInstance(ScopeInstance parentScopeInstance) {
    super.setParentScopeInstance(parentScopeInstance);
    return this;
  }

  // getters and setters //////////////////////////////////////////////////////

  public Trigger getTrigger(String triggerName) {
    return triggers.get(triggerName);
  }

  public void addTrigger(Trigger trigger) {
    triggers.put(trigger.getId(), trigger);
  }

  public void removeTrigger(Trigger trigger) {
    triggers.remove(trigger.getId());
  }

  public FlowInstance getFlowInstance() {
    return flowInstance;
  }
  
  public ActivityInstance setFlowInstance(FlowInstance flowInstance) {
    this.flowInstance = flowInstance;
    return this;
  }
}
