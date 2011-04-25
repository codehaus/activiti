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

import java.util.HashMap;
import java.util.Map;

import org.activiti.service.api.process.instance.ActivityInstance;
import org.activiti.service.api.process.instance.FlowInstance;


/**
 * @author Tom Baeyens
 */
public class Trigger {

  String name;
  FlowInstance flowInstance;
  ActivityInstance activityInstance;
  Map<String, Object> data = new HashMap<String, Object>();
  
  public Trigger(String name, FlowInstance flowInstance, ActivityInstance activityInstance) {
    this.name = name;
    this.flowInstance = flowInstance;
    this.activityInstance = activityInstance;
  }

  public String getName() {
    return name;
  }
  
  public Trigger setName(String name) {
    this.name = name;
    return this;
  }
  
  public Trigger set(String key, Object value) {
    this.data.put(key, value);
    return this;
  }

  public void fire() {
    activityInstance.complete();
  }
}
