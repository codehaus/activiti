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

import java.util.HashMap;
import java.util.Map;

import org.activiti.service.api.Activiti;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;


/**
 * @author Tom Baeyens
 */
public class Persistable {
  
  static Map<Class<?>, ClassMapper> classMappers = new HashMap<Class<?>, ClassMapper>(); 
  
  protected Activiti activiti;
  protected String oid;
  
  static ClassMapper getClassMapper(Class<?> type) {
    ClassMapper classMapper = classMappers.get(type); 
    if (classMapper==null) {
      classMapper = new ClassMapper(type);
      classMappers.put(type, classMapper);
      classMapper.init();
    }
    return classMapper;
  }

  public void setJson(DBObject dbObject) {
    getClassMapper().setJsonInBean(this, dbObject);
  }

  public DBObject getJson() {
    return getClassMapper().getJsonFromBean(this);
  }
  
  protected ClassMapper getClassMapper() {
    return getClassMapper(getClass());
  }
  
  public void setJsonText(String jsonText) {
    DBObject dbObject = (DBObject) JSON.parse(jsonText);
    setJson(dbObject);
  }

  public String getJsonText() {
    return JSON.serialize(getJson());
  }

  public String getJsonTextPrettyPrint() {
    return JsonPrettyPrinter.toJsonPrettyPrint(getJson());
  }

  public String toString() {
    return getJsonText();
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Activiti getActiviti() {
    return activiti;
  }

  public void setActiviti(Activiti activiti) {
    this.activiti = activiti;
  }
}
