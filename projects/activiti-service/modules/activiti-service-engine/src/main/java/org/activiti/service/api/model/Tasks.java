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

import java.util.ArrayList;
import java.util.List;

import org.activiti.service.api.Activiti;
import org.activiti.service.impl.persistence.Manager;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Tasks extends Manager<Task> {

  public Tasks(Activiti activiti, Class<Task> persistableType, DBCollection dbCollection) {
    super(activiti, persistableType, dbCollection);
  }

  public List<Task> findTasks(String assignee, Integer firstResult, Integer maxResults) {
    List<Task> tasks = new ArrayList<Task>();
    for (DBObject taskJson: findTasksJson(assignee, firstResult, maxResults)) {
      Task task = new Task();
      task.setJson(taskJson);
      tasks.add(task);
    }
    return tasks;
  }

  public List<DBObject> findTasksJson(String assignee, Integer firstResult, Integer maxResults) {
    DBObject query = new BasicDBObject();
    query.put("assignee", assignee);
    DBCursor dbCursor = dbCollection.find(query, null, firstResult, maxResults);
    
    List<DBObject> list = new ArrayList<DBObject>();
    while (dbCursor.hasNext()) {
      list.add(dbCursor.next());
    }
    
    return list;
  }

  public DBObject findTask(String taskId) {
    DBObject query = new BasicDBObject();
    query.put("_id", new ObjectId(taskId));
    DBObject dbObject = dbCollection.findOne(query);
    return dbObject;
  }
}
