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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class ClassMapper {
  
  List<FieldMapper> fieldMappers = new ArrayList<FieldMapper>();
  
  public ClassMapper(Class<?> persistableType) {
    try {
      Class<?> persistentClass = getClass();
      while (persistentClass!=null) {
        for (Field field: persistableType.getDeclaredFields()) {
          FieldMapper fieldMapper = null;
          if (!field.getName().equals("oid")) {
            field.setAccessible(true);
            
            if (String.class.isAssignableFrom(field.getType())) {
              fieldMapper = new StringFieldMapper(field);

            } else if (List.class.isAssignableFrom(field.getType())) {
              fieldMapper = new ListFieldMapper(field);
              
            } else {
              throw new ActivitiException("unsupported field type "+field);
            }
          }
          if (fieldMapper!=null) {
            fieldMappers.add(fieldMapper);
          }
        }
        
        persistentClass = persistentClass.getSuperclass();
      }
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public void set(Persistable persistable, DBObject dbObject) {
    ObjectId objectId = (ObjectId) dbObject.get("_id");
    if (objectId != null) {
      persistable.setOid(objectId.toString());
    }

    for (FieldMapper fieldMapper: fieldMappers) {
      fieldMapper.set(dbObject, persistable);
    }
  }

  public DBObject get(Persistable persistable) {
    BasicDBObject dbObject = new BasicDBObject();

    String oid = persistable.getOid();
    if (oid!=null) {
      dbObject.put("_id", new ObjectId(oid));
    }
      
    for (FieldMapper fieldMapper: fieldMappers) {
      fieldMapper.get(dbObject, persistable);
    }
    
    return dbObject;
  }
}
