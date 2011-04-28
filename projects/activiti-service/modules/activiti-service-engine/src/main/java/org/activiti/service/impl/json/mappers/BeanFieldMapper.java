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
import org.activiti.service.impl.json.FieldMapper;
import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class BeanFieldMapper extends FieldMapper {

  protected JsonConverter jsonConverter;
  
  public BeanFieldMapper(ClassMapper classMapper, Field field) {
    super(classMapper, field);
    this.jsonConverter = classMapper.getJsonConverter();
  }

  public void toJson(DBObject json, Object bean) {
    try {
      Object beanFieldValue = field.get(bean);
      if (beanFieldValue!=null) {
        DBObject beanFieldJson = (DBObject) jsonConverter.toJson(beanFieldValue);
        Class< ? > beanClass = bean.getClass();
        if (!field.getType().equals(beanClass)) {
          beanFieldJson.put("class", beanFieldValue.getClass().getName());
        }
        json.put(field.getName(), beanFieldJson);
      }
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public void toBean(DBObject json, Object bean, Object parentBean) {
    try {
      DBObject beanFieldJson = (DBObject) json.get(field.getName());
      if (beanFieldJson!=null) {
        Object beanFieldValue = jsonConverter.instantiate(beanFieldJson, field.getType());
        jsonConverter.setJsonInBean(beanFieldJson, beanFieldValue);
        field.set(bean, beanFieldValue);
      }
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
}
