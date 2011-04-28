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

import java.util.HashMap;
import java.util.Map;

import org.activiti.service.impl.json.JsonBeanMap;
import org.activiti.service.impl.json.JsonParent;



/**
 * @author Tom Baeyens
 */
public class Trigger {

  String id;
  
  @JsonBeanMap
  Map<String, Object> data = new HashMap<String, Object>();
  
  @JsonParent
  ActivityInstance activityInstance;
  
  public String getId() {
    return id;
  }
  
  public Trigger setId(String id) {
    this.id = id;
    return this;
  }
  
  public Trigger set(String key, Object value) {
    this.data.put(key, value);
    return this;
  }

  public void fire() {
    for (String variableName: data.keySet()) {
      activityInstance.setVariable(variableName, data.get(variableName));
    }
    activityInstance.end();
  }

  public ActivityInstance getActivityInstance() {
    return activityInstance;
  }

  public Trigger setActivityInstance(ActivityInstance activityInstance) {
    this.activityInstance = activityInstance;
    return this;
  }
}
