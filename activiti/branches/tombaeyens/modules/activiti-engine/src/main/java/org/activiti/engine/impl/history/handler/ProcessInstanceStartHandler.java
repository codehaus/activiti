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

package org.activiti.engine.impl.history.handler;

import org.activiti.engine.impl.history.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.runtime.ExecutionEntity;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.event.EventListenerExecution;


/**
 * @author Tom Baeyens
 */
public class ProcessInstanceStartHandler implements EventListener {

  public void notify(EventListenerExecution execution) {
    ExecutionEntity executionEntity = (ExecutionEntity) execution;
    String processInstanceId = executionEntity.getId();
    String processDefinitionId = executionEntity.getProcessDefinitionId();
    String businessKey = executionEntity.getBusinessKey();

    HistoricProcessInstanceEntity historicProcessInstance = new HistoricProcessInstanceEntity();
    historicProcessInstance.setId(processInstanceId);
    historicProcessInstance.setProcessInstanceId(processInstanceId);
    historicProcessInstance.setBusinessKey(businessKey);
    historicProcessInstance.setProcessDefinitionId(processDefinitionId);
    historicProcessInstance.setStartTime(ClockUtil.getCurrentTime());
    
    CommandContext
      .getCurrent()
      .getDbSqlSession()
      .insert(historicProcessInstance);
  }
}
