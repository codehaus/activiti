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

import com.mongodb.DBCollection;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Users {

  protected Activiti activiti;
  protected DBCollection users;

  public Users(Activiti activiti, DBCollection users) {
    this.activiti = activiti;
    this.users = users;
  }

  public void insertUser(User user) {
    DBObject userJson = user.toJsonMongo();
    users.insert(userJson);
    user.setOid(userJson.get("_id").toString());
  }

  public void deleteUser(String userId) {
    DBObject query = new User().setId(userId).toJsonMongo();
    users.remove(query);
  }

  public User findUser(String userId) {
    DBObject query = new User().setId(userId).toJsonMongo();
    DBObject userJson = users.findOne(query);
    if (userJson==null) {
      return null;
    }
    return new User(userJson);
  }
}
