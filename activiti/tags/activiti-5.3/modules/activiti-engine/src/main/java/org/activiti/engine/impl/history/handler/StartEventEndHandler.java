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

import java.util.List;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.history.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.pvm.delegate.ExecutionListener;
import org.activiti.engine.impl.pvm.delegate.ExecutionListenerExecution;
import org.activiti.engine.impl.runtime.ExecutionEntity;


/**
 * @author Tom Baeyens
 */
public class StartEventEndHandler implements ExecutionListener {

  public void notify(ExecutionListenerExecution execution) throws Exception {
    String executionId = execution.getId();
    String activityId = ((ExecutionEntity)execution).getActivityId();

    CommandContext commandContext = Context.getCommandContext();
    // search for the historic activity instance in the dbsqlsession cache
    DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
    List<HistoricActivityInstanceEntity> cachedHistoricActivityInstances = dbSqlSession.findInCache(HistoricActivityInstanceEntity.class);
    for (HistoricActivityInstanceEntity cachedHistoricActivityInstance: cachedHistoricActivityInstances) {
      if ( executionId.equals(cachedHistoricActivityInstance.getExecutionId())
           && (activityId.equals(cachedHistoricActivityInstance.getActivityId()))
           && (cachedHistoricActivityInstance.getEndTime()==null)
         ) {
        cachedHistoricActivityInstance.markEnded(null);
        return;
      }
    }
  }

}
