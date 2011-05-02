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
import org.activiti.service.impl.json.JsonBeanMap;
import org.activiti.service.impl.json.JsonIgnore;
import org.activiti.service.impl.json.JsonList;
import org.activiti.service.impl.persistence.AbstractPersistable;



/**
 * @author Tom Baeyens
 */
public abstract class ScopeInstance extends AbstractPersistable {

  
  @JsonIgnore // initialized in FlowInstance.toBeanCompleted
  ScopeInstance parentScopeInstance;

  @JsonIgnore // initialized in FlowInstance.toBeanCompleted
  FlowInstance flowInstance;
  
  @JsonBeanMap
  Map<String,Object> variables = new HashMap<String, Object>();
  
  @JsonList(type=ActivityInstance.class)
  List<ActivityInstance> activityInstances = new ArrayList<ActivityInstance>();
  
  public void startActivityInstance(ActivityDefinition activityDefinition) {
    ActivityInstance activityInstance = new ActivityInstance()
      .setFlowInstance(flowInstance)
      .setActivityDefinition(activityDefinition)
      .setParentScopeInstance(this);
    activityInstances.add(activityInstance);
    activityInstance.start();
  }

  public Trigger getTrigger(String triggerName) {
    for (ActivityInstance activityInstance: activityInstances) {
      Trigger trigger = activityInstance.getTrigger(triggerName);
      if (trigger!=null) {
        return trigger;
      }
    }
    return null;
  }
  
  public abstract void end();

  public void setVariable(String variableName, Object value) {
    if (variables.containsKey(variableName) || parentScopeInstance==null) {
      variables.put(variableName, value);
    } else {
      parentScopeInstance.setVariable(variableName, value);
    }
  }
  
  public List<ActivityInstance> getActivityInstances() {
    return activityInstances;
  }

  
  public FlowInstance getFlowInstance() {
    return flowInstance;
  }

  
  public ScopeInstance setFlowInstance(FlowInstance flowInstance) {
    this.flowInstance = flowInstance;
    return this;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public ScopeInstance getParentScopeInstance() {
    return parentScopeInstance;
  }

  public ScopeInstance setParentScopeInstance(ScopeInstance parentScopeInstance) {
    this.parentScopeInstance = parentScopeInstance;
    return this;
  }
}
