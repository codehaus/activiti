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

package org.activiti.service.api.model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.service.impl.persistence.PersistentList;
import org.activiti.service.impl.persistence.PersistentMap;


/**
 * @author Tom Baeyens
 */
public class Activity {

  String id;
  
  @PersistentMap(type=Activity.class, key="id")
  Map<String, Activity> activities = new HashMap<String, Activity>();

  @PersistentList(type=Transition.class)
  List<Transition> transitions = new ArrayList<Transition>();

  public String getId() {
    return id;
  }
  
  public Activity setId(String id) {
    this.id = id;
    return this;
  }
  
  public Map<String, Activity> getActivities() {
    return activities;
  }
  
  public List<Transition> getTransitions() {
    return transitions;
  }

  public Activity addTransition(Transition transition) {
    transitions.add(transition);
    return this;
  }
}
