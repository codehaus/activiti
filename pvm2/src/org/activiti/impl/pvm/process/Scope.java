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

package org.activiti.impl.pvm.process;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tom Baeyens
 */
public class Scope extends EventDispatcher {

  protected String name;
  protected List<Activity> activities = new ArrayList<Activity>();
  
  public Activity findActivity(String activityName) {
    for (Activity activity: activities) {
      if (activityName.equals(activity.getName())) {
        return activity;
      }
    }
    for (Activity activity: activities) {
      Activity nestedActivity = activity.findActivity(activityName);
      if (nestedActivity!=null) {
        return nestedActivity;
      }
    }
    return null;
  }

  public Activity createActivity() {
    Activity activity = new Activity();
    activity.setParent(this);
    activities.add(activity);
    return  activity;
  }

  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public List<Activity> getActivities() {
    return activities;
  }
  
  public void setActivities(List<Activity> activities) {
    this.activities = activities;
  }
}
