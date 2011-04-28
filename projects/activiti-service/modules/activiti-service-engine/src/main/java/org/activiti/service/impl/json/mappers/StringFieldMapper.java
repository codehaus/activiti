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

import org.activiti.service.impl.json.ClassMapper;
import org.activiti.service.impl.json.FieldMapper;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class StringFieldMapper extends FieldMapper {

  public StringFieldMapper(ClassMapper classMapper, Field field) {
    super(classMapper, field);
  }

  public void toJson(DBObject dbObject, Object persistable) throws Exception {
    String value = (String) field.get(persistable);
    if (value!=null) {
      dbObject.put(field.getName(), value);
    }
  }

  public void toBean(DBObject dbObject, Object persistable, Object parentBean) throws Exception {
    String value = (String) dbObject.get(field.getName());
    if (value!=null) {
      field.set(persistable, value);
    }
  }
}
