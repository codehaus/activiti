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

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Task {
  
  String oid;
  String title;
  String description;
  String assignee;
  
  public Task() {
  }

  public Task(DBObject dbObject) {
    this.oid = dbObject.get("_id").toString();
    this.title = (String) dbObject.get("title");
    this.description = (String) dbObject.get("description");
    this.assignee = (String) dbObject.get("assignee");
  }
  
  public DBObject toJson() {
    DBObject taskJson = new BasicDBObject();
    if (oid!=null) {
      taskJson.put("_id", new ObjectId(oid));
    }
    taskJson.put("title", title);
    if (description!=null) taskJson.put("description", description);
    if (assignee!=null) taskJson.put("assignee", assignee);
    return taskJson;
  }

  public String getTitle() {
    return title;
  }
  
  public Task setTitle(String title) {
    this.title = title;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public Task setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public String getAssignee() {
    return assignee;
  }
  
  public Task setAssignee(String assignee) {
    this.assignee = assignee;
    return this;
  }
  
  public String getOid() {
    return oid;
  }
  
  public Task setOid(String oid) {
    this.oid = oid;
    return this;
  }
}
