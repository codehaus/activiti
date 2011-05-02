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

package org.activiti.service.engine.test.rest;

import org.activiti.service.api.Registrations;
import org.activiti.service.api.Users;
import org.activiti.service.api.identity.Registration;
import org.activiti.service.api.identity.User;
import org.activiti.service.engine.test.RestTestCase;
import org.activiti.service.impl.mail.Mail;


/**
 * @author Tom Baeyens
 */
public class RegistrationRestTest extends RestTestCase {
  
  public void testRegistration() {
    // ensure registrations manager is initialized.  it is used in the registration.getJson below
    activiti.getManager(Registrations.class);
    
    Registration registration = new Registration();
    registration.setUserId("johndoe");
    registration.setEmail("johndoe@johndoe.kom");
    registration.setPassword("secret");
    registration.setClientIp("johndoeslaptop");
    registration.setUrl("/item?id=928374");
    
    String registrationJsonText = activiti.getJsonConverter().toJsonText(registration);
    
    String registrationResponseContent = createRestRequest("/register")
      .post("registration="+registrationJsonText)
      .getContent();
    
    assertEquals("registration email sent to johndoe@johndoe.kom", registrationResponseContent.trim());
    
    Mail mail = testMailService.getMails().get(0);
    assertEquals("johndoe@johndoe.kom", mail.getTo());
    assertEquals("Confirm your Activiti registration", mail.getSubject());
    
    registration = activiti
      .getManager(Registrations.class)
      .findByExample(new Registration().setEmail("johndoe@johndoe.kom"))
      .get(0);
    
    // the next assert ensures that the registration link is in the mail body
    assertTrue(mail.getBody().indexOf("/confirm-registration?id="+registration.getOid())!=-1);
  
    Registration confirmedRegistration = activiti
      .getManager(Registrations.class)
      .confirmRegistration(registration.getOid());
    assertEquals(registration.getClientIp(), confirmedRegistration.getClientIp());
    assertEquals(registration.getUrl(), confirmedRegistration.getUrl());
    
    User johnDoe = activiti.getManager(Users.class).findUserById("johndoe");
    assertNotNull(johnDoe);
  }
}
