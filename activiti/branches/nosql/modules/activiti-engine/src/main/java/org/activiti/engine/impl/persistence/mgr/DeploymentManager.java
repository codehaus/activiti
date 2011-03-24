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

package org.activiti.engine.impl.persistence.mgr;

import java.util.List;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.repository.DeploymentEntity;
import org.activiti.engine.impl.repository.ResourceEntity;
import org.activiti.engine.repository.ProcessDefinition;


/**
 * @author Tom Baeyens
 */
public class DeploymentManager extends AbstractManager {
  
  public void insertDeployment(DeploymentEntity deployment) {
    persistenceSession.insert(deployment);
    for (ResourceEntity resource : deployment.getResources().values()) {
      resource.setDeploymentId(deployment.getId());
      persistenceSession.insert(resource);
    }
  }
  
  public void deleteDeployment(String deploymentId, boolean cascade) {
    if (cascade) {
      List<ProcessDefinition> processDefinitions = persistenceSession
        .createProcessDefinitionQuery()
        .deploymentId(deploymentId)
        .list();

      HistoricProcessInstanceManager historicProcessInstanceManager = Context
        .getCommandContext()
        .getHistoricProcessInstanceManager();
      
      ExecutionManager processInstanceManager = Context
        .getCommandContext()
        .getProcessInstanceManager();

      for (ProcessDefinition processDefinition: processDefinitions) {
        historicProcessInstanceManager.deleteHistoricProcessInstancesByProcessDefinition(processDefinition);
        processInstanceManager.deleteProcessInstancesByProcessDefinition(processDefinition, "deleted deployment");
      }
    }
    persistenceSession.delete("deleteProcessDefinitionsByDeploymentId", deploymentId);
    persistenceSession.delete("deleteResourcesByDeploymentId", deploymentId);
    persistenceSession.delete("deleteDeployment", deploymentId);
  }


  public DeploymentEntity findLatestDeploymentByName(String deploymentName) {
    List<?> list = persistenceSession.selectList("selectDeploymentsByName", deploymentName, new Page(0, 1));
    if (list!=null && !list.isEmpty()) {
      return (DeploymentEntity) list.get(0);
    }
    return null;
  }
  
  public DeploymentEntity findDeploymentById(String deploymentId) {
    return (DeploymentEntity) persistenceSession.selectOne("selectDeploymentById", deploymentId);
  }

  public void close() {
  }

  public void flush() {
  }
}
