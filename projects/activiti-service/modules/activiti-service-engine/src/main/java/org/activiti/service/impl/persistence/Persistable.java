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


/**
 * @author Tom Baeyens
 */
public class Persistable {
  
  static Map<Class<?>, ClassMapper> classMappers = new HashMap<Class<?>, ClassMapper>(); 
  
  protected String oid;

  public void setJson(DBObject dbObject) {
    classMappers
      .get(getClass())
      .setJson(this, dbObject);
  }
  
  public DBObject getJson() {
    return classMappers
      .get(getClass())
      .getJson(this);
  }
  
  public void setJsonText(String jsonText) {
    classMappers
      .get(getClass())
      .setJsonText(this, jsonText);
  }

  public String getJsonText() {
    return classMappers
      .get(getClass())
      .getJsonText(this);
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }
}
