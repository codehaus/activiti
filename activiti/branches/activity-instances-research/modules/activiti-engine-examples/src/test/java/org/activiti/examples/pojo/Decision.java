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
package org.activiti.examples.pojo;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.el.ActivitiMethodExpression;
import org.activiti.engine.impl.el.ActivitiValueExpression;
import org.activiti.pvm.activity.ActivityBehavior;
import org.activiti.pvm.activity.ActivityContext;
import org.activiti.pvm.process.PvmTransition;


/**
 * @author Tom Baeyens
 */
public class Decision implements ActivityBehavior {

  public static final String KEY_CONDITION = "ConditionExpression";

  public void start(ActivityContext activityContext) throws Exception {
    PvmTransition transition = findOutgoingTransition(activityContext);
    activityContext.take(transition);
  }

  private PvmTransition findOutgoingTransition(ActivityContext activityContext) {
    List<PvmTransition> outgoingTransitions = activityContext.getOutgoingTransitions();
    for (PvmTransition transition: outgoingTransitions) {
      ActivitiValueExpression activitiValueExpression = (ActivitiValueExpression) transition.getProperty(KEY_CONDITION);
      if (activitiValueExpression==null) {
        return transition;
      }
      Boolean result = (Boolean) activitiValueExpression.getValue(activityContext);
      if (result) {
        return transition;
      }
    }
    throw new ActivitiException("no transition for which the condition resolved to true: "+outgoingTransitions);
  }
}
