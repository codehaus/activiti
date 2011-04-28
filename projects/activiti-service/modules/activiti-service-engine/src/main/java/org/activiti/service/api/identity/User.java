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

package org.activiti.service.api.identity;

import java.util.ArrayList;
import java.util.List;

import org.activiti.service.impl.persistence.AbstractPersistable;


/**
 * @author Tom Baeyens
 */
public class User extends AbstractPersistable {

  String id;
  String password;
  
  List<String> groupIds = new ArrayList<String>();
  
  public User addGroupId(String groupId) {
    groupIds.add(groupId);
    return this;
  }

  public User removeGroupId(String groupId) {
    groupIds.remove(groupId);
    return this;
  }

  // getters and setters //////////////////////////////////////////////////////

  public String getId() {
    return id;
  }
  
  public User setId(String userId) {
    this.id = userId;
    return this;
  }
  
  public String getPassword() {
    return password;
  }
  
  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public List<String> getGroupIds() {
    return groupIds;
  }

  public User setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
    return this;
  }
}
