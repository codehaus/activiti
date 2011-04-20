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
import org.activiti.service.api.ActivitiException;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Manager <T extends Persistable> {

  protected Activiti activiti;
  protected Class<T> persistableType;
  protected DBCollection dbCollection;
  
  public Manager(Activiti activiti, Class<T> persistableType, DBCollection dbCollection) {
    this.activiti = activiti;
    this.persistableType = persistableType;
    this.dbCollection = dbCollection;
    // initialize this class
    Persistable.classMappers.put(persistableType, new ClassMapper(persistableType));
  }

  public String insert(T persistable) {
    DBObject jsonMongo = persistable.getJson();
    dbCollection.insert(jsonMongo);
    String oid = jsonMongo.get("_id").toString();
    persistable.setOid(oid);
    return oid;
  }
  
  public void delete(T persistable) {
    DBObject jsonMongo = persistable.getJson();
    dbCollection.remove(jsonMongo);
  }

  public void update(T persistable) {
    DBObject query = new BasicDBObject("_id", new ObjectId(persistable.getOid()));
    DBObject jsonMongo = persistable.getJson();
    dbCollection.update(query, jsonMongo);
  }

  public List<T> findAllByExample(T example) {
    List<T> persistables = new ArrayList<T>();
    DBObject jsonMongo = example.getJson();
    DBCursor dbCursor = dbCollection.find(jsonMongo);
    while (dbCursor.hasNext()) {
      try {
        DBObject dbObject = dbCursor.next();
        T persistable = persistableType.newInstance();
        persistable.setJson(dbObject);
        persistables.add(persistable);
      } catch (Exception e) {
        throw new ActivitiException("persistence reflection problem", e);
      }
    }
    return persistables;
  }
  
  public T findOneByExample(T example) {
    DBObject jsonMongo = example.getJson();
    return findOneJsonByExampleJson(jsonMongo);
  }

  public T findOneByOid(String oid) {
    DBObject jsonMongo = new BasicDBObject();
    jsonMongo.put("_id", new ObjectId(oid));
    return findOneJsonByExampleJson(jsonMongo);
  }

  private T findOneJsonByExampleJson(DBObject jsonMongo) {
    T persistable = null;
    DBObject dbObject = dbCollection.findOne(jsonMongo);
    if (dbObject!=null) {
      try {
        persistable = persistableType.newInstance();
        persistable.setJson(dbObject);
      } catch (Exception e) {
        throw new ActivitiException("persistence reflection problem", e);
      }
    }
    return persistable;
  }
}
