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

package org.activiti.spring;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.IdentitySession;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.interceptor.Session;


/**
 * @author Tom Baeyens
 */
public class SpringSecurityIdentitySession implements IdentitySession, Session {
  
  public void createMembership(String userId, String groupId) {
  }

  public Group createNewGroup(String groupId) {
    return null;
  }

  public GroupQuery createNewGroupQuery(CommandExecutor commandExecutor) {
    return null;
  }

  public User createNewUser(String userId) {
    return null;
  }

  public UserQuery createNewUserQuery(CommandExecutor commandExecutor) {
    return null;
  }

  public void deleteGroup(String groupId) {
  }

  public void deleteMembership(String userId, String groupId) {
  }

  public void deleteUser(String userId) {
  }

  public Group findGroupById(String groupId) {
    return null;
  }

  public List<Group> findGroupByQueryCriteria(Object query, Page page) {
    return null;
  }

  public long findGroupCountByQueryCriteria(Object query) {
    return 0;
  }

  public List<Group> findGroupsByUser(String userId) {
    return null;
  }

  public User findUserById(String userId) {
    return null;
  }

  public List<User> findUserByQueryCriteria(Object query, Page page) {
    return null;
  }

  public long findUserCountByQueryCriteria(Object query) {
    return 0;
  }

  public List<User> findUsersByGroupId(String groupId) {
    return null;
  }

  public void insertGroup(Group group) {
  }

  public void insertUser(User user) {
  }

  public boolean isValidUser(String userId) {
    return false;
  }

  public void updateGroup(Group updatedGroup) {
  }

  public void updateUser(User updatedUser) {
  }

  public void close() {
  }

  public void flush() {
  }

}
