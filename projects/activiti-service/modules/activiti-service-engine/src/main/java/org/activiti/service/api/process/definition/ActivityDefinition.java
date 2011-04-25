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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.service.api.process.instance.ActivityInstance;
import org.activiti.service.impl.persistence.PersistentList;


/**
 * @author Tom Baeyens
 */
public class ActivityDefinition extends ScopeDefinition {

  ActivityType type;
  
  @PersistentList(type=Transition.class)
  List<Transition> transitions = new ArrayList<Transition>();
  
  public ActivityInstance createActivityInstance() {
    ActivityInstance clone = null;
    // TODO clone activity instance configuration
    return clone;
  }

  // customized getters and setters ///////////////////////////////////////////
  
  public ActivityDefinition setDescription(String description) {
    super.setDescription(description);
    return this;
  }

  public ActivityDefinition addActivity(ActivityDefinition activityDefinition) {
    super.addActivity(activityDefinition);
    return this;
  }

  public ActivityDefinition removeActivity(ActivityDefinition activityDefinition) {
    super.removeActivity(activityDefinition);
    return this;
  }

  public ActivityDefinition setId(String id) {
    super.setId(id);
    return this;
  }

  // getters and setters //////////////////////////////////////////////////////

  public Map<String, ActivityDefinition> getActivities() {
    return activityDefinitions;
  }
  
  public List<Transition> getTransitions() {
    return transitions;
  }

  public ActivityDefinition addTransition(Transition transition) {
    transitions.add(transition);
    return this;
  }

  public ActivityType getType() {
    return type;
  }

  public ActivityDefinition setType(ActivityType type) {
    this.type = type;
    return this;
  }
}
