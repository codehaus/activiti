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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.activiti.service.api.ActivitiException;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Persistable {
  
  String oid;

  public Persistable() {
  }
  
  @SuppressWarnings("unchecked")
  public Persistable(DBObject dbObject) {
    try {
      for (Field field: getClass().getDeclaredFields()) {
        Object value = null;
        field.setAccessible(true);
        if (field.getName().equals("oid")) {
          ObjectId objectId = (ObjectId) dbObject.get("_id");
          if (objectId != null) {
            value = objectId.toString();
          }
        } else {

          if (String.class.isAssignableFrom(field.getType())) {
            value = dbObject.get(field.getName());

          } else if (List.class.isAssignableFrom(field.getType())) {
            BasicDBList dbList = (BasicDBList) dbObject.get(field.getName());
            if (dbList!=null) {
              value = new ArrayList<String>((List)dbList);
            }
            
          } else {
            throw new ActivitiException("unsupported field type "+field.getType().getName());
          }
        }
        if (value!=null) {
          field.set(this, value);
        }
      }
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
  
  @SuppressWarnings("unchecked")
  public DBObject toJsonMongo() {
    BasicDBObject dbObject = new BasicDBObject();
    try {
      Class<?> persistentClass = getClass();
      while (persistentClass!=null) {
        for (Field field: getClass().getDeclaredFields()) {
          field.setAccessible(true);
          Object value = field.get(this);
          if (value!=null) {
            if (field.getName().equals("oid")) {
              dbObject.put("_id", new ObjectId((String)value));
            } else {
              if (String.class.isAssignableFrom(field.getType())) {
                dbObject.put(field.getName(), value);

              } else if (List.class.isAssignableFrom(field.getType())) {
                BasicDBList dbList = (BasicDBList) new BasicDBList();
                for (String element: (List<String>)value) {
                  if (element instanceof String) {
                    dbList.add(element);
                  }
                }
                dbObject.put(field.getName(), dbList);
                
              } else {
                throw new ActivitiException("unsupported field type "+field.getType().getName());
              }
            }
          }
        }

        persistentClass = persistentClass.getSuperclass();
      }
      
      return dbObject;
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }
}
