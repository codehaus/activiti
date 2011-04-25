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

import org.activiti.service.api.cases.Case;
import org.activiti.service.api.process.definition.Trigger;



/**
 * @author Tom Baeyens
 */
public class FlowInstance extends Case {

  String processOid;
  Map<String,Object> variables = new HashMap<String, Object>(); 
  List<ActivityInstance> activityInstances = new ArrayList<ActivityInstance>();

  public Trigger getTrigger(String triggerName) {
    for (ActivityInstance activityInstance: activityInstances) {
      Trigger trigger = activityInstance.getTrigger(triggerName);
      if (trigger!=null) {
        return trigger;
      }
    }
    return null;
  }

  public List<ActivityInstance> getActivityInstances() {
    return activityInstances;
  }

  public String getProcessOid() {
    return processOid;
  }
  
  public FlowInstance setProcessOid(String processOid) {
    this.processOid = processOid;
    return this;
  }
}
