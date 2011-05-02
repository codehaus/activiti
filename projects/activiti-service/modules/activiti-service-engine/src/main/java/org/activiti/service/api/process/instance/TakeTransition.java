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

import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.Transition;


/**
 * @author Tom Baeyens
 */
public class TakeTransition implements AtomicOperation {

  ActivityInstance activityInstance;
  Transition transition;

  public TakeTransition(ActivityInstance activityInstance, Transition transition) {
    this.activityInstance = activityInstance;
    this.transition = transition;
  }

  public void execute() {
    FlowInstance flowInstance = activityInstance.getFlowInstance();
    String destinationActivityDefinitionId = transition.getTo();
    
    ActivityDefinition activityDefinition = activityInstance
      .getFlowInstance()
      .getFlowDefinition()
      .findActivityDefinition(destinationActivityDefinitionId);

    flowInstance.startActivityInstance(activityDefinition);
  }
}
