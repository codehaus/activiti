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

package org.activiti.service.api.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.service.api.identity.GroupLink;
import org.activiti.service.api.identity.UserLink;
import org.activiti.service.impl.persistence.AbstractPersistable;
import org.activiti.service.impl.persistence.PersistentList;
import org.activiti.service.impl.persistence.PersistentMap;


/**
 * @author Tom Baeyens
 */
public class Case extends AbstractPersistable {
  
  String name;
  String description;

  @PersistentMap(type=UserLink.class, key="userId")
  Map<String, UserLink> users = new HashMap<String, UserLink>();
  
  @PersistentList(type=GroupLink.class)
  List<GroupLink> groups = new ArrayList<GroupLink>();
  
  public Case addUserLink(String userId, String role) {
    UserLink userLink = new UserLink().setUserId(userId).setRole(role);
    users.put(userId, userLink);
    return this;
  }

  public Case setAssignee(String userId) {
    return addUserLink(userId, UserLink.ROLE_ASSIGNEE);
  }

  public Case setOwner(String userId) {
    return addUserLink(userId, UserLink.ROLE_OWNER);
  }

  public String getName() {
    return name;
  }
  
  public Case setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public Case setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public Map<String, UserLink> getUsers() {
    return users;
  }
}
