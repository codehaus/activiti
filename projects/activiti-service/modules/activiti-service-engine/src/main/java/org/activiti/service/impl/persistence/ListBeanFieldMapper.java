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
public class ListBeanFieldMapper extends ListFieldMapper {

  protected Class<?> elementType;
  protected ClassMapper classMapper;
  
  public ListBeanFieldMapper(Field field, Class<?> elementType) {
    super(field);
    this.classMapper = new ClassMapper(elementType);
    this.elementType = elementType;
  }

  public Object convertElement(Object element) {
    return classMapper.getJsonFromBean(element);
  }

  public Object convertJsonElement(Object jsonElement) {
    try {
      Object bean = elementType.newInstance(); 
      classMapper.setJsonInBean(bean, (DBObject) jsonElement);
      return bean;
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
}
