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

import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class BeanFieldMapper extends FieldMapper {

  protected Class<?> elementType;
  protected JsonConverter jsonConverter;
  
  public BeanFieldMapper(ClassMapper classMapper, Field field, Class<?> elementType) {
    super(classMapper, field);
    this.jsonConverter = classMapper.getJsonConverter();
    this.elementType = elementType;
  }

  @Override
  public void get(DBObject json, Object bean) {
    jsonConverter.getJsonFromBean(json, bean);
    if (!elementType.equals(bean.getClass())) {
      json.put("class", bean.getClass().getName());
    }
  }

  @Override
  public void set(DBObject json, Object bean) {
    jsonConverter.setJsonInBean(json, bean);
  }

}
