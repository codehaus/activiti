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

package org.activiti.service.impl.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.json.mappers.BeanFieldMapper;
import org.activiti.service.impl.json.mappers.BeanMapFieldMapper;
import org.activiti.service.impl.json.mappers.BooleanFieldMapper;
import org.activiti.service.impl.json.mappers.CollectionBeanFieldMapper;
import org.activiti.service.impl.json.mappers.CollectionStringFieldMapper;
import org.activiti.service.impl.json.mappers.OidFieldMapper;
import org.activiti.service.impl.json.mappers.ParentFieldMapper;
import org.activiti.service.impl.json.mappers.StringFieldMapper;
import org.activiti.service.impl.persistence.Persistable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class ClassMapper {
  
  private static Logger log = Logger.getLogger(ClassMapper.class.getName());
  
  JsonConverter jsonConverter;
  Class<?> type;
  List<FieldMapper> fieldMappers = new ArrayList<FieldMapper>();
  Method toBeanCompletedMethod = null;
  
  public ClassMapper(JsonConverter jsonConverter, Class<?> type) {
    this.jsonConverter = jsonConverter;
    this.type = type;
  }

  public void init() {
    try {
      Class<?> scannedClass = type;
      while (scannedClass!=null) {
        for (Field field: scannedClass.getDeclaredFields()) {
          FieldMapper fieldMapper = null;
          field.setAccessible(true);
          
          JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
          JsonParent jsonParent = field.getAnnotation(JsonParent.class);
          
          if ( Modifier.isStatic(field.getModifiers()) 
               || Activiti.class.equals(field.getType())
               || jsonIgnore!=null) { 
            // ignore static fields and Activiti typed fields
            fieldMapper = null;
            
          } else if (jsonParent!=null) {
            fieldMapper = new ParentFieldMapper(field);
            
          } else if (boolean.class.equals(field.getType())) {
            fieldMapper = new BooleanFieldMapper(field);

          } else if (Persistable.class.isAssignableFrom(scannedClass) && field.getName().equals("oid")) {
            fieldMapper = new OidFieldMapper(this);
            
          } else if (String.class.isAssignableFrom(field.getType())) {
            fieldMapper = new StringFieldMapper(this, field);

          } else if (List.class.isAssignableFrom(field.getType())) {
            JsonList jsonList = field.getAnnotation(JsonList.class);
            if (jsonList==null) {
              fieldMapper = new CollectionStringFieldMapper(this, field);
            } else {
              Class<?> elementType = jsonList.type();
              fieldMapper = new CollectionBeanFieldMapper(this, field, elementType);
            }
            
          } else if (Map.class.isAssignableFrom(field.getType())) {
            JsonMap jsonMap = field.getAnnotation(JsonMap.class);
            JsonBeanMap jsonBeanMap = field.getAnnotation(JsonBeanMap.class);
            if (jsonBeanMap!=null) {
              fieldMapper = new BeanMapFieldMapper(this, field);
              
            } else if (jsonMap!=null) {
              Class<?> elementType = jsonMap.type();
              fieldMapper = new CollectionBeanFieldMapper(this, field, elementType, jsonMap.key());

            } else {
              throw new ActivitiException("map "+field+" doesn't have annotation "+JsonMap.class.getName()+" or "+JsonBeanMap.class.getName());
            }

          } else {
            fieldMapper = new BeanFieldMapper(this, field);
          }
          
          if (fieldMapper!=null) {
            fieldMappers.add(fieldMapper);
          }
        }
        
        scannedClass = scannedClass.getSuperclass();
      }
      
      for (Method method: type.getDeclaredMethods()) {
        if (method.getName().equals("toBeanCompleted")) {
          toBeanCompletedMethod = method;
          break;
        }
      }
      
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public Object toBean(BasicDBObject json) throws Exception {
    return toBean(json, null);
  }
  
  public Object toBean(BasicDBObject json, Object parentBean) throws Exception {
    Object bean = type.newInstance();
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("setting "+fieldMapper.field);
      fieldMapper.toBean(json, bean, parentBean);
    }
    if (toBeanCompletedMethod!=null) {
      toBeanCompletedMethod.invoke(bean, json, jsonConverter.getActiviti());
    }
    return bean;
  }

  public BasicDBObject toJson(Object bean) throws Exception {
    BasicDBObject json = new BasicDBObject();
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("getting "+fieldMapper.field);
      fieldMapper.toJson(json, bean);
    }
    return json;
  }


  public void setJsonInBean(Object bean, DBObject json, Object parentBean) throws Exception {
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("setting "+fieldMapper.field);
      fieldMapper.toBean(json, bean, parentBean);
    }
  }

  public void getJsonFromBean(DBObject json, Object bean) throws Exception {
    for (FieldMapper fieldMapper: fieldMappers) {
      log.fine("getting "+fieldMapper.field);
      fieldMapper.toJson(json, bean);
    }
  }

  public JsonConverter getJsonConverter() {
    return jsonConverter;
  }
}
