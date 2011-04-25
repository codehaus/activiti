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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class CollectionFieldMapper extends FieldMapper {
  
  protected String key;

  public CollectionFieldMapper(ClassMapper classMapper, Field field) {
    super(classMapper, field);
  }

  public CollectionFieldMapper(ClassMapper classMapper, Field field, String key) {
    super(classMapper, field);
    this.key = key;
  }

  public abstract Object convertElement(Object element);
  public abstract Object convertJsonElement(Object jsonElement);

  @SuppressWarnings("unchecked")
  public void get(DBObject dbObject, Object bean) {
    Object collection = getValueFromField(bean);
    if (collection!=null) {
      BasicDBList jsonList = new BasicDBList();
      Collection elements = (key!=null ? ((Map<String,Object>) collection).values() : (List<Object>) collection);
      if (!elements.isEmpty()) {
        for (Object element: elements) {
          if (element!=null) {
            Object jsonElement = convertElement(element);
            jsonList.add(jsonElement);
          }
        }
        dbObject.put(field.getName(), jsonList);
      }
    }
  }

  public void set(DBObject dbObject, Object bean) {
    BasicDBList jsonList = (BasicDBList) dbObject.get(field.getName());
    Object collection = null;
    if (jsonList!=null) {
      if (key!=null) {
        Map<String,Object> map = new HashMap<String, Object>();
        collection = map;
        for (Object jsonElement: jsonList) {
          String keyValue = (String) ((DBObject)jsonElement).get(key);
          Object element = convertJsonElement(jsonElement);
          map.put(keyValue, element);
        }
        
      } else {
        List<Object> list = new ArrayList<Object>();
        collection = list;
        for (Object jsonElement: jsonList) {
          Object element = convertJsonElement(jsonElement);
          list.add(element);
        }
      }
    }
    setValueInField(bean, collection);
  }
}
