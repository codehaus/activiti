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

package org.activiti.test.pvm.activities;

import java.util.Map;

import org.activiti.impl.pvm.activity.SignallableActivityBehaviour;
import org.activiti.impl.pvm.process.Transition;
import org.activiti.impl.pvm.runtime.ActivityInstance;


/**
 * @author Tom Baeyens
 */
public class WaitState implements SignallableActivityBehaviour {

  @Override
  public void start(ActivityInstance activityInstance) {
  }
  
  @Override
  public Map<String, Class< ? >> getSignalDescriptors(ActivityInstance activityInstance) {
    return null;
  }

  @Override
  public void signal(ActivityInstance activityInstance, String signal, Object data) {
    Transition transition = activityInstance.getActivity().getOutgoingTransitions().get(0);
    activityInstance.take(transition);
  }
}
