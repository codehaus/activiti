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
import org.activiti.service.impl.json.FieldMapper;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class BooleanFieldMapper extends FieldMapper {

  public BooleanFieldMapper(Field field) {
    super(null, field);
  }

  public void toJson(DBObject dbObject, Object bean) {
    try {
      Boolean fieldValue = (Boolean) field.get(bean);
      if (fieldValue!=null) {
        dbObject.put(field.getName(), fieldValue.toString());
      }
    } catch (Exception e) {
      throw new ActivitiException("json reflection problem", e);
    }
  }

  public void toBean(DBObject dbObject, Object bean, Object parentBean) {
    String jsonValue = (String) dbObject.get(field.getName());
    if (jsonValue!=null) {
      Boolean fieldValue = Boolean.parseBoolean(jsonValue);
      try {
        field.set(bean, fieldValue);
      } catch (Exception e) {
        throw new ActivitiException("json reflection problem", e);
      }
    }
  }
}
