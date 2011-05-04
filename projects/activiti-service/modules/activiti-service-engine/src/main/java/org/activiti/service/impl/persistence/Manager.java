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

package org.activiti.service.impl.persistence;

import java.util.ArrayList;
import java.util.List;

import org.activiti.service.api.Activiti;
import org.activiti.service.impl.json.JsonConverter;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class Manager <T extends Persistable> {

  protected Activiti activiti;
  protected JsonConverter jsonConverter;
  protected DBCollection dbCollection;

  void init(Activiti activiti, DBCollection dbCollection) {
    this.activiti = activiti;
    this.jsonConverter = activiti.getJsonConverter();
    this.dbCollection = dbCollection;
  }
  
  public abstract Class<T> getPersistableType();

  public String insert(T persistable) {
    DBObject jsonMongo = (DBObject) jsonConverter.toJson(persistable);
    dbCollection.insert(jsonMongo);
    String oid = jsonMongo.get("_id").toString();
    persistable.setOid(oid);
    persistable.setActiviti(activiti);
    return oid;
  }
  
  public void delete(T persistable) {
    DBObject jsonMongo = (DBObject) jsonConverter.toJson(persistable);
    dbCollection.remove(jsonMongo);
  }

  public void update(T persistable) {
    DBObject query = new BasicDBObject("_id", new ObjectId(persistable.getOid()));
    DBObject jsonMongo = (DBObject) jsonConverter.toJson(persistable);
    dbCollection.update(query, jsonMongo);
  }

  public List<T> findByExample(T example) {
    return findByExample(example, -1, -1);
  }
  
  @SuppressWarnings("unchecked")
  public List<T> findByExample(T example, int firstResult, int maxResults) {
    List<T> persistables = new ArrayList<T>();
    DBObject jsonMongo = (DBObject) jsonConverter.toJson(example);
    DBCursor dbCursor = null;
    if (firstResult>=0 && maxResults>0) {
      dbCursor = dbCollection.find(jsonMongo, null, firstResult, maxResults);
    } else {
      dbCursor = dbCollection.find(jsonMongo);
    }
    while (dbCursor.hasNext()) {
      DBObject dbObject = dbCursor.next();
      T persistable = (T) jsonConverter.toBean(dbObject, getPersistableType());
      persistable.setActiviti(activiti);
      persistables.add(persistable);
    }
    return persistables;
  }

  public T findOneByExample(T example) {
    DBObject jsonMongo = (DBObject) jsonConverter.toJson(example);
    return findOneJsonByExampleJson(jsonMongo);
  }

  public T findOneByOid(String oid) {
    DBObject jsonMongo = new BasicDBObject();
    jsonMongo.put("_id", new ObjectId(oid));
    return findOneJsonByExampleJson(jsonMongo);
  }

  @SuppressWarnings("unchecked")
  private T findOneJsonByExampleJson(DBObject jsonMongo) {
    T persistable = null;
    DBObject dbObject = dbCollection.findOne(jsonMongo);
    if (dbObject!=null) {
      persistable = (T) jsonConverter.toBean(dbObject, getPersistableType());
      persistable.setActiviti(activiti);
    }
    return persistable;
  }
}
