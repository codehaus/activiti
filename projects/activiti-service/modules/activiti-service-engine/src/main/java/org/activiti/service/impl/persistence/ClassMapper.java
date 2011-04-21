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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.service.api.ActivitiException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class ClassMapper {
  
  private static Logger log = Logger.getLogger(ClassMapper.class.getName());
  
  Class<?> type;
  List<FieldMapper> fieldMappers = new ArrayList<FieldMapper>();
  
  public ClassMapper(Class<?> type) {
    this.type = type;
  }

  public void init() {
    try {
      Class<?> scannedClass = type;
      while (scannedClass!=null) {
        for (Field field: scannedClass.getDeclaredFields()) {
          FieldMapper fieldMapper = null;
          field.setAccessible(true);
          
          if ("type".equals(field.getName())) {
            throw new ActivitiException("type is a reserved json key and field name: "+field);
          }
          
          if (Modifier.isStatic(field.getModifiers())) { 
            // ignore static fields
            fieldMapper = null;
            
          } else if (Persistable.class.isAssignableFrom(scannedClass) && field.getName().equals("oid")) {
            fieldMapper = new OidFieldMapper();
            
          } else if (String.class.isAssignableFrom(field.getType())) {
            fieldMapper = new StringFieldMapper(field);

          } else if (List.class.isAssignableFrom(field.getType())) {
            PersistentList persistentList = field.getAnnotation(PersistentList.class);
            if (persistentList==null) {
              throw new ActivitiException("list "+field+" doesn't have "+PersistentList.class);
            }
            Class<?> elementType = persistentList.type();
            if (String.class.isAssignableFrom(elementType)) {
              fieldMapper = new CollectionStringFieldMapper(field);
            } else {
              fieldMapper = new CollectionBeanFieldMapper(field, elementType);
            }
            
          } else if (Map.class.isAssignableFrom(field.getType())) {
            PersistentMap persistentMap = field.getAnnotation(PersistentMap.class);
            if (persistentMap==null) {
              throw new ActivitiException("map "+field+" doesn't have "+PersistentMap.class);
            }
            Class<?> elementType = persistentMap.type();
            fieldMapper = new CollectionBeanFieldMapper(field, elementType, persistentMap.key());

          } else {
            throw new ActivitiException("unsupported field type "+field);
          }
          
          if (fieldMapper!=null) {
            fieldMappers.add(fieldMapper);
          }
        }
        
        scannedClass = scannedClass.getSuperclass();
      }
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public void setJsonInBean(Object bean, DBObject json) {
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("setting "+fieldMapper.field);
      fieldMapper.set(json, bean);
    }
  }

  public DBObject getJsonFromBean(Object bean) {
    BasicDBObject json = new BasicDBObject();
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("getting "+fieldMapper.field);
      fieldMapper.get(json, bean);
    }
    return json;
  }
}
