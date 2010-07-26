package org.activiti.test.pvm.activities;
import org.activiti.impl.pvm.process.Transition;
import org.activiti.impl.pvm.runtime.ActivityInstance;
import org.activiti.impl.pvm.spi.ActivityBehaviour;



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

/**
 * @author Tom Baeyens
 */
public class Automatic implements ActivityBehaviour {

  @Override
  public void start(ActivityInstance activityInstance) {
    Transition transition = activityInstance.getActivity().getOutgoingTransitions().get(0);
    activityInstance.take(transition);
  }

}
