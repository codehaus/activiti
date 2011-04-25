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

package org.activiti.service.api.identity;

import org.activiti.service.impl.persistence.AbstractPersistable;


/**
 * @author Tom Baeyens
 */
public class Registration extends AbstractPersistable {

  protected String userId;
  protected String password;
  protected String email;
  protected String url;
  protected String clientIp;

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
