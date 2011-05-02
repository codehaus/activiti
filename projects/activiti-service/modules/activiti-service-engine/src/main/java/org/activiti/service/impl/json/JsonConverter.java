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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.persistence.Manager;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class JsonConverter {

  protected Map<Class<?>, ClassMapper> classMappersCache = new HashMap<Class<?>, ClassMapper>();
  protected Activiti activiti;
  
  public JsonConverter(Activiti activiti) {
    this.activiti = activiti;
  }

  protected ClassMapper getClassMapper(Class<?> type) {
    ClassMapper classMapper = classMappersCache.get(type); 
    if (classMapper==null) {
      classMapper = new ClassMapper(this, type);
      classMappersCache.put(type, classMapper);
      classMapper.init();
    }
    return classMapper;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T instantiate(DBObject dbObject, Class<T> baseType) {
    try {
      Class<T> instanceType = baseType;
      String type = (String) dbObject.get("class");
      if (type!=null) {
        instanceType = (Class<T>) Class.forName(type, true, Manager.class.getClassLoader());
      }
      T object = instanceType.newInstance();
      
      return object;
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
  
  public Object toBean(Object json, Class<?> beanType) {
    return toBean(json, beanType, null);
  }
  public Object toBean(Object json, Class<?> beanType, Object parentBean) {
    if ( (json instanceof String) || (json instanceof Boolean) ) {
      return json;

    } else if (json instanceof BasicDBObject) {
      try {
        BasicDBObject jsonObject = (BasicDBObject) json;
        String beanTypeName = (String) jsonObject.get("class");
        if (beanTypeName!=null) {
          beanType = Class.forName(beanTypeName);
        }
        ClassMapper classMapper = getClassMapper(beanType);
        return classMapper.toBean(jsonObject, parentBean);
      } catch (Exception e) {
        throw new ActivitiException("json reflection problem", e);
      }

    } else if (json instanceof BasicDBList) {
      List<Object> beans = new ArrayList<Object>();
      BasicDBList jsonList = (BasicDBList) json;
      for (Object jsonElement: jsonList) {
        Object beanElement = toBean(jsonElement, beanType, parentBean);
        beans.add(beanElement);
      }
      return beans;
    } 
    
    throw new ActivitiException("supported json type "+json.getClass().getName());
  }
  
  @SuppressWarnings("unchecked")
  public Object toJson(Object bean) {
    try {
      if (bean instanceof String || bean instanceof Boolean) {
        return bean;
        
      } else if (Map.class.isAssignableFrom(bean.getClass())) {
        Map<String,Object> beanMap = (Map<String, Object>) bean;
        if (beanMap!=null && !beanMap.isEmpty()) {
          BasicDBObject jsonMap = new BasicDBObject();
          for (String elementKey: beanMap.keySet()) {
            Object elementValue = beanMap.get(elementKey);
            Object jsonElementValue = toJson(elementValue);
            jsonMap.put(elementKey, jsonElementValue);
          }
          return jsonMap;
        }
        
      } else if (Collection.class.isAssignableFrom(bean.getClass())) {
        BasicDBList jsonList = new BasicDBList();
        Collection beanCollection = (Collection)bean;
        for (Object beanCollectionElement: beanCollection) {
          Object jsonCollectionElement = toJson(beanCollectionElement);
          jsonList.add(jsonCollectionElement);
        }
        return jsonList;
        
      }
      Class<?> beanType = bean.getClass();
      return getClassMapper(beanType).toJson(bean);
    } catch (Exception e) {
      throw new ActivitiException("problem getting json from bean "+bean, e);
    }
  }

  public String toJsonText(Object bean) {
    // what is the difference between json.toString() and JSON.serialize(json); ?
    return toJson(bean).toString();
  }

  public String toJsonTextPretty(Object bean) {
    return JsonPrettyPrinter.toJsonPrettyPrint((DBObject) toJson(bean));
  }
  
  public Activiti getActiviti() {
    return activiti;
  }

  public JsonConverter setActiviti(Activiti activiti) {
    this.activiti = activiti;
    return this;
  }
}
