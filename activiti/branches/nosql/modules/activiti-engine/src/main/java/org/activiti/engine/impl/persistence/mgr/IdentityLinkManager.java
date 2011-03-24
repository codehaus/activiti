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

import org.activiti.engine.impl.task.IdentityLinkEntity;


/**
 * @author Tom Baeyens
 */
public class IdentityLinkManager extends AbstractManager {

  @SuppressWarnings("unchecked")
  public List<IdentityLinkEntity> findIdentityLinksByTaskId(String taskId) {
    return persistenceSession.selectList("selectIdentityLinksByTask", taskId);
  }

  public void deleteIdentityLink(IdentityLinkEntity identityLink) {
    persistenceSession.delete(IdentityLinkManager.class, identityLink.getId());
  }
}
