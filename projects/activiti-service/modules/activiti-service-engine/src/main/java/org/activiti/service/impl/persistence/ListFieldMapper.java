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
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class ListFieldMapper extends FieldMapper {

  public ListFieldMapper(Field field) {
    super(field);
  }

  public abstract Object convertElement(Object element);
  public abstract Object convertJsonElement(Object jsonElement);

  @SuppressWarnings("unchecked")
  public void get(DBObject dbObject, Object bean) {
    List<Object> list = (List<Object>) getValueFromField(bean);
    if (list!=null && !list.isEmpty()) {
      BasicDBList jsonList = new BasicDBList();
      for (Object element: (List<Object>) list) {
        if (element!=null) {
          Object jsonElement = convertElement(element);
          jsonList.add(jsonElement);
        }
      }
      dbObject.put(field.getName(), jsonList);
    }
  }

  public void set(DBObject dbObject, Object bean) {
    BasicDBList jsonList = (BasicDBList) dbObject.get(field.getName());
    if (jsonList!=null) {
      List<Object> list = new ArrayList<Object>();
      for (Object jsonElement: jsonList) {
        Object element = convertJsonElement(jsonElement);
        list.add(element);
      }
      
      setValueInField(bean, list);
    }
  }
}
