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

package org.activiti.service.api.process.definition;

import org.activiti.service.api.model.process.activity.WaitState;
import org.activiti.service.api.process.instance.FlowInstance;



/**
 * @author Tom Baeyens
 */
public class FlowDefinition extends ScopeDefinition {

  String owner;
  
  // not required in modelling processes.  Initialized in buildExecutableProcess; 
  String startActivityId;

  public FlowDefinition buildExecutableProcess() {
    for (ActivityDefinition activityDefinition: activityDefinitions.values()) {
      if (WaitState.class.equals(activityDefinition.getActivityType().getClass())) {
        startActivityId = activityDefinition.getId();
        break;
      }
    }
    return this;
  }

  public FlowInstance createNewFlowInstance() {
    FlowInstance flowInstance = new FlowInstance()
      .setFlowDefinition(this);

    if (startActivityId==null) {
      buildExecutableProcess();
    }
    
    ActivityDefinition startActivityDefinition = activityDefinitions.get(startActivityId);
    flowInstance.startActivityInstance(startActivityDefinition);

    return flowInstance;
  }

  
  // getters and setters //////////////////////////////////////////////////////
  
  public FlowDefinition setId(String id) {
    super.setId(id);
    return this;
  }

  public FlowDefinition setDescription(String description) {
    super.setDescription(description);
    return this;
  }

  public FlowDefinition addActivity(ActivityDefinition activityDefinition) {
    super.addActivity(activityDefinition);
    return this;
  }

  public FlowDefinition removeActivity(ActivityDefinition activityDefinition) {
    super.removeActivity(activityDefinition);
    return this;
  }

  // getters and setters //////////////////////////////////////////////////////
  
  public FlowDefinition setOwner(String owner) {
    this.owner = owner;
    return this; 
  }
  
  public String getOwner() {
    return owner;
  }

  public String getStartActivityId() {
    return startActivityId;
  }
  
  public FlowDefinition setStartActivityId(String startActivityId) {
    this.startActivityId = startActivityId;
    return this;
  }  
}
