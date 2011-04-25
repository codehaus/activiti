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

import java.util.HashMap;
import java.util.Map;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.persistence.ClassMapper;
import org.activiti.service.impl.persistence.JsonPrettyPrinter;
import org.activiti.service.impl.persistence.Manager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


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
  public <T> T instantiate(DBObject dbObject, Class<T> baseType, Activiti activiti) {
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
  
  public void setJsonInBean(DBObject dbObject, Object bean) {
    getClassMapper(bean.getClass()).setJsonInBean(bean, dbObject);
  }

  public DBObject getJsonFromBean(Object bean) {
    return getClassMapper(bean.getClass()).getJsonFromBean(bean);
  }
  
  public void getJsonFromBean(DBObject json, Object bean) {
    getClassMapper(bean.getClass()).getJsonFromBean(json, bean);
  }
  
  public void setJsonTextInBean(String jsonText, Object bean) {
    DBObject dbObject = (DBObject) JSON.parse(jsonText);
    setJsonInBean(dbObject, bean);
  }

  public String getJsonTextFromBean(Object bean) {
    return JSON.serialize(getJsonFromBean(bean));
  }

  public String getJsonTextPrettyPrintFromBean(Object bean) {
    return JsonPrettyPrinter.toJsonPrettyPrint(getJsonFromBean(bean));
  }

  public Activiti getActiviti() {
    return activiti;
  }

  public JsonConverter setActiviti(Activiti activiti) {
    this.activiti = activiti;
    return this;
  }
}
