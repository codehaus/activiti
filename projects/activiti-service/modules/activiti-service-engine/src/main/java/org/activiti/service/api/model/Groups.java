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
public class Groups {

  protected Activiti activiti;
  protected DBCollection groups;

  public Groups(Activiti activiti, DBCollection groups) {
    this.activiti = activiti;
    this.groups = groups;
  }

  public void createGroup(Group group) {
    DBObject taskJson = group.toJson();
    groups.insert(taskJson);
    group.setOid(taskJson.get("_id").toString());
  }

  public void deleteGroup(String groupOid) {
    DBObject query = new Group().setOid(groupOid).toJson();
    groups.remove(query);
  }


}
