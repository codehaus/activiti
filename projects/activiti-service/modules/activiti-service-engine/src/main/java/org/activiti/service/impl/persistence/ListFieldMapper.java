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

import org.activiti.service.impl.util.json.JSONArray;
import org.activiti.service.impl.util.json.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class ListFieldMapper extends FieldMapper {

  public ListFieldMapper(Field field) {
    super(field);
  }

  @SuppressWarnings("unchecked")
  public void get(DBObject dbObject, Persistable persistable) {
    List<String> value = (List<String>) getValueFromField(persistable);
    if (value!=null) {
      BasicDBList dbList = new BasicDBList();
      for (String element: (List<String>)value) {
        if (element instanceof String) {
          dbList.add(element);
        }
      }
      dbObject.put(field.getName(), dbList);
    }
  }

  @SuppressWarnings("unchecked")
  public void set(DBObject dbObject, Persistable persistable) {
    BasicDBList dbList = (BasicDBList) dbObject.get(field.getName());
    if (dbList!=null) {
      Object value = new ArrayList<String>((List)dbList);
      setValueInField(persistable, value);
    }
  }

  @SuppressWarnings("unchecked")
  public void get(JSONObject jsonObject, Persistable persistable) {
    List<String> value = (List<String>) getValueFromField(persistable);
    if (value!=null) {
      JSONArray jsonArray = new JSONArray();
      for (String element: (List<String>) value) {
        if (element instanceof String) {
          jsonArray.put(element);
        }
      }
      jsonObject.put(field.getName(), jsonArray);
    }
  }

  public void set(JSONObject jsonObject, Persistable persistable) {
    JSONArray jsonArray = (JSONArray) jsonObject.get(field.getName());
    if (jsonArray!=null) {
      List<String> value = new ArrayList<String>();
      for (int i=0; i<jsonArray.length(); i++) {
        value.add((String)jsonArray.get(i));
      }
      setValueInField(persistable, value);
    }
  }
}
