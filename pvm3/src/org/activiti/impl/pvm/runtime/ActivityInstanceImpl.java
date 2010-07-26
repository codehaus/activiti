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

import org.activiti.impl.pvm.process.ActivityImpl;


/**
 * @author Tom Baeyens
 */
public class ActivityInstanceImpl extends ScopeInstanceImpl {

  protected ActivityImpl activity;
  protected ScopeInstanceImpl parent;
  protected ExecutionContextImpl executionContext;
  
  public ActivityInstanceImpl(ActivityImpl activity, ScopeInstanceImpl parent) {
    super(parent.getProcessDefinition(), activity);
    this.activity = activity;
    this.parent = parent;
  }

  public void signal(String signalName, Object data) {
    new ExecutionContextImpl().signal(this, signalName, data);
  }

  
  public ActivityImpl getActivity() {
    return activity;
  }

  
  public void setActivity(ActivityImpl activity) {
    this.activity = activity;
  }

  
  public ScopeInstanceImpl getParent() {
    return parent;
  }

  
  public void setParent(ScopeInstanceImpl parent) {
    this.parent = parent;
  }

  
  public ExecutionContextImpl getExecutionContext() {
    return executionContext;
  }

  
  public void setExecutionContext(ExecutionContextImpl executionContext) {
    this.executionContext = executionContext;
  }
}
