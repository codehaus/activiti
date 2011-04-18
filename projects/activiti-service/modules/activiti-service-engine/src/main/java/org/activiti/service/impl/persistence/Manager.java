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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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

  Class<T> persistableType;
  DBCollection dbCollection;
  
  public Manager(Class<T> persistableType, DBCollection dbCollection) {
    this.persistableType = persistableType;
    this.dbCollection = dbCollection;
  }

  public String insert(T persistable) {
    DBObject jsonMongo = persistable.toJsonMongo();
    dbCollection.insert(jsonMongo);
    String oid = jsonMongo.get("_id").toString();
    persistable.setOid(oid);
    return oid;
  }
  
  public void delete(T persistable) {
    DBObject jsonMongo = persistable.toJsonMongo();
    dbCollection.remove(jsonMongo);
  }

  public void update(T persistable) {
    DBObject query = new BasicDBObject("_id", new ObjectId(persistable.getOid()));
    DBObject jsonMongo = persistable.toJsonMongo();
    dbCollection.update(query, jsonMongo);
  }

  public List<T> findByExample(T example) {
    List<T> persistables = new ArrayList<T>();
    DBObject jsonMongo = example.toJsonMongo();
    DBCursor dbCursor = dbCollection.find(jsonMongo);
    while (dbCursor.hasNext()) {
      try {
        DBObject dbObject = dbCursor.next();
        Constructor<T> constructor = persistableType.getDeclaredConstructor(DBObject.class);
        T persistable = constructor.newInstance(new Object[]{dbObject});
        persistables.add(persistable);
      } catch (Exception e) {
        throw new ActivitiException("persistence reflection problem", e);
      }
    }
    return persistables;
  }
}
