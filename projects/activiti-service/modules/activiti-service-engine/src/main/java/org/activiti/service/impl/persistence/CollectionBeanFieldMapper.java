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

import org.activiti.service.api.ActivitiException;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class CollectionBeanFieldMapper extends CollectionFieldMapper {

  protected Class<?> elementType;
  protected ClassMapper classMapper;
  
  public CollectionBeanFieldMapper(Field field, Class<?> elementType) {
    this(field, elementType, null);
  }

  public CollectionBeanFieldMapper(Field field, Class<?> elementType, String key) {
    super(field, key);
    this.classMapper = Persistable.getClassMapper(elementType);
    this.elementType = elementType;
  }

  public Object convertElement(Object element) {
    DBObject jsonMongo = classMapper.getJsonFromBean(element);
    if (!elementType.equals(element.getClass())) {
      jsonMongo.put("type", element.getClass().getName());
    }
    return jsonMongo;
  }

  public Object convertJsonElement(Object jsonElement) {
    try {
      DBObject jsonMongo = (DBObject) jsonElement;
      Object bean = Manager.instantiate(jsonMongo, elementType, null); 
      classMapper.setJsonInBean(bean, jsonMongo);
      return bean;
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
}
