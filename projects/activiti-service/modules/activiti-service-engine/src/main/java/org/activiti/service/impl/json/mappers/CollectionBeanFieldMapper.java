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

import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.json.ClassMapper;
import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class CollectionBeanFieldMapper extends CollectionFieldMapper {

  protected Class<?> elementType;
  protected JsonConverter jsonConverter;
  
  public CollectionBeanFieldMapper(ClassMapper classMapper, Field field, Class<?> elementType) {
    this(classMapper, field, elementType, null);
  }

  public CollectionBeanFieldMapper(ClassMapper classMapper, Field field, Class<?> elementType, String key) {
    super(classMapper, field, key);
    this.jsonConverter = classMapper.getJsonConverter();
    this.elementType = elementType;
  }

  public Object convertElement(Object beanElement) {
    BasicDBObject jsonElement = (BasicDBObject) jsonConverter.toJson(beanElement);
    if (!elementType.equals(beanElement.getClass())) {
      jsonElement.put("class", beanElement.getClass().getName());
    }
    return jsonElement;
  }

  public Object convertJsonElement(Object jsonElement, Object parentBean) {
    try {
      DBObject jsonMongo = (DBObject) jsonElement;
      Object bean = jsonConverter.instantiate(jsonMongo, elementType); 
      jsonConverter.setJsonInBean(jsonMongo, bean, parentBean);
      return bean;
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
}
