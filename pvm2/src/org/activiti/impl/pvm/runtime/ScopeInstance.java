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

package org.activiti.impl.pvm.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.impl.pvm.process.Activity;
import org.activiti.impl.pvm.process.ProcessDefinition;
import org.activiti.impl.pvm.process.Scope;



/**
 * @author Tom Baeyens
 */
public class ScopeInstance {

  protected ProcessDefinition processDefinition;
  protected Scope scope;
  protected Set<ActivityInstance> activityInstances = new HashSet<ActivityInstance>();

  public ScopeInstance(ProcessDefinition processDefinition, Scope scope) {
    this.processDefinition = processDefinition;
    this.scope = scope;
  }

  protected ActivityInstance createActivityInstance(Activity activity) {
    ActivityInstance activityInstance = new ActivityInstance(activity, this);
    activityInstances.add(activityInstance);
    return activityInstance;
  }

  public void removeActivityInstance(ActivityInstance activityInstance) {
    activityInstances.remove(activityInstance);
    activityInstance.setParent(null);
  }
  
  public List<ActivityInstance> findActivityInstances(String activityId) {
    List<ActivityInstance> foundActivityInstances = new ArrayList<ActivityInstance>();
    collectActivityInstances(foundActivityInstances, activityId);
    return foundActivityInstances;
  }
  
  public ActivityInstance findActivityInstance(String activityId) {
    List<ActivityInstance> activityInstances = findActivityInstances(activityId);
    if (activityInstances.isEmpty()) {
      return null;
    }
    return activityInstances.get(0);
  }

  protected void collectActivityInstances(List<ActivityInstance> activityInstanceCollection, String activityId) {
    if (activityId==null) {
      throw new RuntimeException("activitId is null");
    }
    for (ActivityInstance activityInstance: activityInstances) {
      if (activityId.equals(activityInstance.getActivity().getId())) {
        activityInstanceCollection.add(activityInstance);
      }
      activityInstance.collectActivityInstances(activityInstanceCollection, activityId);
    }
  }
  
  public Scope getScope() {
    return scope;
  }

  
  public void setScope(Scope scope) {
    this.scope = scope;
  }

  
  public Set<ActivityInstance> getActivityInstances() {
    return activityInstances;
  }

  
  public void setActivityInstances(Set<ActivityInstance> activityInstances) {
    this.activityInstances = activityInstances;
  }

  
  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }

  
  public void setProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }
}
