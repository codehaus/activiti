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

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.history.HistoricDetailEntity;


/**
 * @author Tom Baeyens
 */
public class HistoricDetailManager extends AbstractHistoricManager {

  public void deleteHistoricDetail(HistoricDetailEntity historicDetail) {
    persistenceSession.delete(HistoricDetailEntity.class, historicDetail.getId());
  }

  @SuppressWarnings("unchecked")
  public void deleteHistoricDetailsByProcessInstanceId(String historicProcessInstanceId) {
    if (historyLevel>=ProcessEngineConfigurationImpl.HISTORYLEVEL_AUDIT) {
      List<HistoricDetailEntity> historicDetails = (List) persistenceSession
        .createHistoricDetailQuery()
        .processInstanceId(historicProcessInstanceId)
        .list();
      
      HistoricDetailManager historicDetailManager = Context
        .getCommandContext()
        .getHistoricDetailManager();
      
      for (HistoricDetailEntity historicDetail: historicDetails) {
        historicDetailManager.deleteHistoricDetail(historicDetail);
      }
    }
  }
}
