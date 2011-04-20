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

package org.activiti.service.api.model;

import org.activiti.service.impl.persistence.Persistable;
import org.activiti.service.impl.util.json.JSONObject;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Registration extends Persistable {

  protected String userId;
  protected String password;
  protected String email;
  protected String url;
  protected String clientIp;

  public Registration() {
    super();
  }

  public Registration(DBObject dbObject) {
    super(dbObject);
  }

  public Registration(String jsonText) {
    JSONObject jsonObject = new JSONObject(jsonText);
    if (jsonObject.has("_id")) {
      JSONObject objectId = (JSONObject) jsonObject.get("_id");
      oid = objectId.get("$oid").toString();
    }
    userId = (String) jsonObject.get("userId");
    password = (String) jsonObject.get("password");
    email = (String) jsonObject.get("email");
    url = (String) jsonObject.get("url");
    clientIp = (String) jsonObject.get("clientIp");
  }

  public String getUserId() {
    return userId;
  }
  
  public Registration setUserId(String userId) {
    this.userId = userId;
    return this;
  }
  
  public String getPassword() {
    return password;
  }
  
  public Registration setPassword(String password) {
    this.password = password;
    return this;
  }
  
  public String getEmail() {
    return email;
  }
  
  public Registration setEmail(String email) {
    this.email = email;
    return this;
  }
  
  public String getUrl() {
    return url;
  }
  
  public Registration setUrl(String url) {
    this.url = url;
    return this;
  }
  
  public String getClientIp() {
    return clientIp;
  }

  public Registration setClientIp(String clientIp) {
    this.clientIp = clientIp;
    return this;
  }
}
