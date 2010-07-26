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

import org.activiti.impl.pvm.process.Activity;
import org.activiti.impl.pvm.process.Transition;
import org.activiti.impl.pvm.spi.SignallableActivityBehaviour;


/**
 * @author Tom Baeyens
 */
public class ActivityInstance extends ScopeInstance {

  protected Activity activity;
  protected ScopeInstance parent;
  protected ExecutionContext executionContext;
  
  public ActivityInstance(Activity activity, ScopeInstance parent) {
    super(parent.getProcessDefinition(), activity);
    this.activity = activity;
    this.parent = parent;
  }

  public void take(Transition transition) {
    executionContext.takeTransition(this, transition);
  }

  
  public Activity getActivity() {
    return activity;
  }

  
  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  
  public ScopeInstance getParent() {
    return parent;
  }

  
  public void setParent(ScopeInstance parent) {
    this.parent = parent;
  }

  
  public ExecutionContext getExecutionContext() {
    return executionContext;
  }

  
  public void setExecutionContext(ExecutionContext executionContext) {
    this.executionContext = executionContext;
  }

  public void signal(String signalName, Object data) {
    SignallableActivityBehaviour signallableActivityBehaviour = (SignallableActivityBehaviour) activity.getActivityBehaviour();
    signallableActivityBehaviour.signal(this, signalName, data);
  }
}
