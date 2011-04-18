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
public class Group {

  String oid;
  String id;
  String name;

  public Group() {
  }

  public Group(DBObject userJson) {
    this.oid = (String) userJson.get("_id").toString();
    this.id = (String) userJson.get("id");
    this.name = (String) userJson.get("name");
  }

  public DBObject toJson() {
    BasicDBObject oid = new BasicDBObject();
    if (id!=null) {
      oid.put("_id", new ObjectId(id));
    }
    oid.put("id", id);
    oid.put("name", name);
    return oid;
  }

  
  public String getOid() {
    return oid;
  }

  
  public Group setOid(String oid) {
    this.oid = oid;
    return this;
  }

  
  public String getId() {
    return id;
  }

  
  public Group setId(String id) {
    this.id = id;
    return this;
  }

  
  public String getName() {
    return name;
  }

  
  public Group setName(String name) {
    this.name = name;
    return this;
  }
}
