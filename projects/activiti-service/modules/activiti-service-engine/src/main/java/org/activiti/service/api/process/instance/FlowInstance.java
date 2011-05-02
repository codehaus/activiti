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

package org.activiti.service.api.process.instance;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ExecutableFlowDefinitions;
import org.activiti.service.api.process.definition.FlowDefinition;
import org.activiti.service.impl.json.JsonIgnore;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class FlowInstance extends ScopeInstance {

  String flowDefinitionOid;
  
  @JsonIgnore
  FlowDefinition flowDefinition;
  
  @JsonIgnore
  Engine engine = new Engine();
  
  public FlowInstance() {
    flowInstance = this;
  }
  
  public FlowInstance setFlowDefinition(FlowDefinition flowDefinition) {
    this.flowDefinition = flowDefinition;
    this.flowDefinitionOid = flowDefinition.getOid();
    return this;
  }
  
  public void toBeanCompleted(DBObject json, Activiti activiti) {
    if (flowDefinition==null && flowDefinitionOid!=null) {
      flowDefinition = activiti
        .getManager(ExecutableFlowDefinitions.class)
        .findOneByOid(flowDefinitionOid);
    }
    
    parentScopeInstance = null;
    
    toBeanCompleted(this, this);
  }

  protected void toBeanCompleted(ScopeInstance scopeInstance, FlowInstance flowInstance) {
    // initialize flowInstance and parentScopeInstance
    for (ActivityInstance activityInstance: scopeInstance.activityInstances) {
      activityInstance.flowInstance = flowInstance;
      activityInstance.parentScopeInstance = scopeInstance;
      toBeanCompleted(activityInstance, flowInstance);
    }
  }
  
  public void end() {
  }



  // getters and setters //////////////////////////////////////////////////////

  public String getFlowDefinitionOid() {
    return flowDefinitionOid;
  }

  public FlowInstance setFlowDefinitionOid(String flowDefinitionOid) {
    this.flowDefinitionOid = flowDefinitionOid;
    return this;
  }

  public Engine getEngine() {
    return engine;
  }
  
  public FlowDefinition getFlowDefinition() {
    return flowDefinition;
  }
}
