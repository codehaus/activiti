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

package org.activiti.service.impl.json.mappers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.activiti.service.impl.json.ClassMapper;
import org.activiti.service.impl.json.FieldMapper;
import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class BeanMapFieldMapper extends FieldMapper {

  public BeanMapFieldMapper(ClassMapper classMapper, Field field) {
    super(classMapper, field);
  }

  @SuppressWarnings("unchecked")
  public void toJson(DBObject dbObject, Object bean)  throws Exception {
    Map<String,Object> mapBeanFieldValue = (Map<String, Object>) field.get(bean);
    if (mapBeanFieldValue!=null && !mapBeanFieldValue.isEmpty()) {
      DBObject jsonMap = new BasicDBObject();
      for (String elementKey: mapBeanFieldValue.keySet()) {
        Object elementValue = mapBeanFieldValue.get(elementKey);
        Object jsonElementValue = classMapper.getJsonConverter().toJson(elementValue);
        jsonMap.put(elementKey, jsonElementValue);
      }
      dbObject.put(field.getName(), jsonMap);
    }
  }

  public void toBean(DBObject json, Object bean, Object parentBean) throws Exception {
    Map<String, Object> beanFieldMap = new HashMap<String, Object>();
    DBObject jsonMap = (DBObject) json.get(field.getName());
    if (jsonMap!=null) {
      for (String key: jsonMap.keySet()) {
        Object jsonEntryValue = jsonMap.get(key);
        JsonConverter jsonConverter = classMapper.getJsonConverter();
        Object beanEntryValue = jsonConverter.toBean(jsonEntryValue, Object.class, bean);
        beanFieldMap.put(key, beanEntryValue);
      }
      field.set(bean, beanFieldMap);
    }
  }
}
