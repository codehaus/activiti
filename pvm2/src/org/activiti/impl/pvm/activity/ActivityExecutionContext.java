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

package org.activiti.impl.pvm.activity;

import java.util.List;

import org.activiti.impl.pvm.process.ActivityImpl;
import org.activiti.impl.pvm.process.TransitionImpl;


/**
 * @author Tom Baeyens
 */
public interface ActivityExecutionContext {

  void take(TransitionImpl transition);

  List<TransitionImpl> getOutgoingTransitions();

  List<TransitionImpl> getIncomingTransitions();

  List<ActivityImpl> getActivities();
}
