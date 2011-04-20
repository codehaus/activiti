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
import org.activiti.service.impl.util.json.JSONObject;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class FieldMapper {

  protected Field field;
  
  public FieldMapper(Field field) {
    this.field = field;
  }

  public abstract void set(DBObject dbObject, Persistable persistable);
  public abstract void get(DBObject dbObject, Persistable persistable);

  public abstract void set(JSONObject jsonObject, Persistable persistable);
  public abstract void get(JSONObject jsonObject, Persistable persistable);

  public Object getValueFromField(Persistable persistable) {
    try {
      return field.get(persistable);
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }

  public void setValueInField(Persistable persistable, Object value) {
    try {
      field.set(persistable, value);
    } catch (Exception e) {
      throw new ActivitiException("persistence reflection problem", e);
    }
  }
}
