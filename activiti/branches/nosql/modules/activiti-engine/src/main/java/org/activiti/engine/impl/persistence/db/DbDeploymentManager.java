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

package org.activiti.engine.impl.persistence.db;

import java.util.List;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.DeploymentManager;
import org.activiti.engine.impl.repository.DeploymentEntity;
import org.activiti.engine.impl.repository.ResourceEntity;


/**
 * @author Tom Baeyens
 */
public class DbDeploymentManager implements DeploymentManager, Session {
  
  protected DbSqlSession dbSqlSession = Context.getCommandContext().getDbSqlSession();
  
  public void insertDeployment(DeploymentEntity deployment) {
    dbSqlSession.insert(deployment);
    for (ResourceEntity resource : deployment.getResources().values()) {
      resource.setDeploymentId(deployment.getId());
      dbSqlSession.insert(resource);
    }
  }

  public DeploymentEntity findLatestDeploymentByName(String deploymentName) {
    List<?> list = dbSqlSession.selectList("selectDeploymentsByName", deploymentName, new Page(0, 1));
    if (list!=null && !list.isEmpty()) {
      return (DeploymentEntity) list.get(0);
    }
    return null;
  }

  public void close() {
  }

  public void flush() {
  }
}
