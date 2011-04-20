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

import com.mongodb.DBObject;
import com.mongodb.util.JSON;


/**
 * @author Tom Baeyens
 */
public class Persistable {
  
  static Map<Class<?>, ClassMapper> classMappers = new HashMap<Class<?>, ClassMapper>(); 
  
  protected String oid;

  public void setJson(DBObject dbObject) {
    classMappers
      .get(getClass())
      .setJsonInBean(this, dbObject);
  }
  
  public DBObject getJson() {
    return classMappers
      .get(getClass())
      .getJsonFromBean(this);
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
}
