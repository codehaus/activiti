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
package org.activiti.rest.api.identity;

import java.util.Map;

import org.activiti.rest.util.ActivitiPagingWebScript;
import org.activiti.rest.util.ActivitiRequest;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryProperty;
import org.springframework.extensions.webscripts.*;

/**
 * Returns details about a user's groups.
 *
 * @author Erik Winlof
 */
public class UserGroupsGet extends ActivitiPagingWebScript
{

  public UserGroupsGet() {
    properties.put("id", GroupQueryProperty.GROUP_ID);
    properties.put("name", GroupQueryProperty.NAME);
    properties.put("type", GroupQueryProperty.TYPE);
  }

  /**
   * Collects details about a user's groups for the webscript template.
   *
   * @param req The webscripts request
   * @param status The webscripts status
   * @param cache The webscript cache
   * @param model The webscripts template model
   */
  @Override
  protected void executeWebScript(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {
    String userId = req.getMandatoryPathParameter("userId");
    String groupType = req.getString("type", null);
    GroupQuery query = getIdentityService().createGroupQuery().groupMember(userId);
    if (groupType != null) {

      query.groupType(groupType);
    }
    paginateList(req, query, "groups", model, "id");
  }

}
