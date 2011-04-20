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

import org.activiti.service.impl.persistence.Manager;


/**
 * @author Tom Baeyens
 */
public class Registrations extends Manager<Registration>{

  public void register(Registration registration) {
    insert(registration);

    // TODO check if there are already existing registrations for the given email and handle appropriate
    
    // send email with confirmation code (== registrationId)
    activiti
      .getActivitiConfiguration()
      .getMailService()
      .sendRegistrationConfirmationMail(activiti, registration);
  }
  
  /** with the returned registration, ui can check if the 
   * confirmation comes from the same clientId and in that case perform
   * autologin.  The ui can also automatically forward to the 
   * original requested url (registration.getUrl()) if it is not null. */
  public Registration confirmRegistration(String registrationId) {
    // lookup registration
    Registration registration = findOneByOid(registrationId);
    // if registration does not exist return null.
    if (registration==null) {
      return null;
    }
    
    // create user from registration
    User user = new User();
    user.setId(registration.getUserId());
    user.setPassword(registration.getPassword());
    activiti.getManager(Users.class).insert(user);
    
    // delete registration
    delete(registration);
    
    return registration;
  }
}
