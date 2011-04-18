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

package org.activiti.service.impl.rest.handler;

import org.activiti.service.api.model.Registration;
import org.activiti.service.impl.rest.impl.HttpServletMethod;
import org.activiti.service.impl.rest.impl.JsonParameter;
import org.activiti.service.impl.rest.impl.Parameter;
import org.activiti.service.impl.rest.impl.RestHandler;


/**
 * @author Tom Baeyens
 */
public class RegisterHandler extends RestHandler {
  
  public boolean requiresAuthentication() {
    return false;
  }

  public HttpServletMethod getMethod() {
    return HttpServletMethod.POST;
  }

  public String getUrlPattern() {
    return "/register";
  }
  
  protected Parameter<Registration> registrationParameter = new JsonParameter<Registration>(Registration.class)
    .setName("registration")
    .setDescription("json registration");
  
  public void handle(RestRequestContext restRequestContext) {
    Registration registration = registrationParameter.get(restRequestContext);
    restRequestContext
      .getActiviti()
      .getRegistrations()
      .register(registration);
    
    restRequestContext.sendResponsePlainText("registration email sent to "+registration.getEmail());
  }
}
