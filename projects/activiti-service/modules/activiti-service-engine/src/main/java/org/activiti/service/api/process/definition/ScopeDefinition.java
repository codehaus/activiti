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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.json.JsonMap;
import org.activiti.service.impl.persistence.AbstractPersistable;


/**
 * @author Tom Baeyens
 */
public class ScopeDefinition extends AbstractPersistable {

  String id;
  String description;

  @JsonMap(type=ActivityDefinition.class, key="id")
  Map<String, ActivityDefinition> activityDefinitions = new HashMap<String, ActivityDefinition>();
  
  // collection modifiers /////////////////////////////////////////////////////
  
  public ScopeDefinition addActivity(ActivityDefinition activityDefinition) {
    String activityId = activityDefinition.getId();
    if (activityId==null) {
      throw new ActivitiException("activity id is null");
    }
    activityDefinitions.put(activityId, activityDefinition);
    return this;
  }
  
  public ScopeDefinition removeActivity(ActivityDefinition activityDefinition) {
    String activityId = activityDefinition.getId();
    if (activityId==null) {
      throw new ActivitiException("activity id is null");
    }
    activityDefinitions.remove(activityId);
    return this;
  }

  public Map<String, ActivityDefinition> getActivityDefinitions() {
    return Collections.unmodifiableMap(activityDefinitions);
  }
  
  public ActivityDefinition findActivityDefinition(String activityDefinitionId) {
    ActivityDefinition activityDefinition = activityDefinitions.get(activityDefinitionId);
    if (activityDefinition!=null) {
      return activityDefinition;
    }
    for (ActivityDefinition activityDefinition2: activityDefinitions.values()) {
      ActivityDefinition activityDefinition3 = activityDefinition2.findActivityDefinition(activityDefinitionId);
      if (activityDefinition3!=null) {
        return activityDefinition3;
      }
    }
    return null;
  }


  // getters and setters //////////////////////////////////////////////////////

  public String getId() {
    return id;
  }
  
  public ScopeDefinition setId(String id) {
    this.id = id;
    return this;
  }
  
  public String getDescription() {
    return description;
  }

  public ScopeDefinition setDescription(String description) {
    this.description = description;
    return this;
  }
}
