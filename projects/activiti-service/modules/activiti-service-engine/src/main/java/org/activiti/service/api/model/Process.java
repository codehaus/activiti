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

package org.activiti.service.api.model;

import java.util.HashMap;
import java.util.Map;

import org.activiti.service.api.ActivitiException;
import org.activiti.service.api.model.process.Activity;
import org.activiti.service.impl.persistence.Persistable;
import org.activiti.service.impl.persistence.PersistentMap;



/**
 * @author Tom Baeyens
 */
public class Process extends Persistable {

  String name;
  String description;
  String owner;

  @PersistentMap(type=Activity.class, key="id")
  Map<String, Activity> activities = new HashMap<String, Activity>();
  
  public Process setDescription(String description) {
    this.description = description;
    return this; 
  }

  public Process setName(String name) {
    this.name = name;
    return this; 
  }

  public Process setOwner(String owner) {
    this.owner = owner;
    return this; 
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getOwner() {
    return owner;
  }

  public Map<String, Activity> getActivities() {
    return activities;
  }

  public Process addActivity(Activity activity) {
    String activityId = activity.getId();
    if (activityId==null) {
      throw new ActivitiException("activity id is null");
    }
    activities.put(activityId, activity);
    return this;
  }
}
