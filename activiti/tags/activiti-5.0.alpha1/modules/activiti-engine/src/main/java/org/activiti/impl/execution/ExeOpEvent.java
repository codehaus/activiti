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
package org.activiti.impl.execution;

import org.activiti.ActivitiException;
import org.activiti.activity.EventActivityBehavior;
import org.activiti.impl.definition.ActivityImpl;


/**
 * @author Tom Baeyens
 */
public class ExeOpEvent implements ExeOp {

  Object event;
  
  public ExeOpEvent(Object event) {
    this.event = event;
  }

  public boolean isAsync() {
    return false;
  }

  public void execute(ExecutionImpl execution) {
    ActivityImpl activity = execution.getActivity();
    EventActivityBehavior activityBehavior = (EventActivityBehavior) activity.getActivityBehavior();
    try {
      activityBehavior.event(execution, event);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ActivitiException("couldn't process event '"+event+"' on activity '"+activity.getName()+"': "+e.getMessage(), e);
    }
  }
  
  public String toString() {
    return "FireEvent["+event+"]";
  }
}
