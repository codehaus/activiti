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

import java.util.List;

import org.activiti.service.api.ActivitiException;
import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.Engine;
import org.activiti.service.api.process.definition.Transition;
import org.activiti.service.api.process.definition.Trigger;


/**
 * @author Tom Baeyens
 */
public class ActivityInstance {

  FlowInstance flowInstance;
  ActivityInstance parentActivityInstance;
  ActivityDefinition activityDefinition;

  public void complete() {
    List<Transition> transitions = activityDefinition.getTransitions();
    if (transitions==null || transitions.size()!=1) {
      throw new ActivitiException("expected exactly one outgoing transition out of "+activityDefinition.getId());
    }
    Transition singleOutgoingTransition = transitions.get(0);
    Engine.take(this, singleOutgoingTransition);
  }

  public Trigger getTrigger(String triggerName) {
    return null;
  }
  
  public FlowInstance getFlow() {
    return flowInstance;
  }
  
  public ActivityInstance setFlow(FlowInstance flowInstance) {
    this.flowInstance = flowInstance;
    return this;
  }
  
  public ActivityDefinition getActivity() {
    return activityDefinition;
  }
  
  public ActivityInstance setActivity(ActivityDefinition activityDefinition) {
    this.activityDefinition = activityDefinition;
    return this;
  }
}
