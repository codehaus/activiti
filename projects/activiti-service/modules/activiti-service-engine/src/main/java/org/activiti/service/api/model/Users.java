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

package org.activiti.service.api.model;


import org.activiti.service.api.Activiti;
import org.activiti.service.impl.persistence.Manager;

import com.mongodb.DBCollection;


/**
 * @author Tom Baeyens
 */
public class Users extends Manager<User>{

  public Users(Activiti activiti, Class<User> persistableType, DBCollection dbCollection) {
    super(activiti, persistableType, dbCollection);
  }

  public User findUserById(String userId) {
    User example = new User().setId(userId);
    return findOneByExample(example);
  }
}
