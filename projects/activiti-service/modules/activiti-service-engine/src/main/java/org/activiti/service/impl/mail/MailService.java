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

package org.activiti.service.impl.mail;

import java.util.Properties;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.identity.Registration;


/**
 * @author Tom Baeyens
 */
public class MailService {

  Properties mailProperties = new Properties();
  
  public MailService() {
    this.mailProperties.put("mail.smtp.host", "localhost");
  }

  public Mail newMail() {
    return new Mail(this);
  }

  public Properties getMailProperties() {
    return mailProperties;
  }
  
  public void setMailProperties(Properties mailProperties) {
    this.mailProperties = mailProperties;
  }

  public void sendRegistrationConfirmationMail(Activiti activiti, Registration registration) {
    String activitiHost = activiti.getActivitiConfiguration().getActivitiHost();
    int activitiPort = activiti.getActivitiConfiguration().getActivitiPort();
    String registrationId = registration.getOid();
    String confirmationLink = "http://"+activitiHost+(activitiPort!=80?":"+activitiPort:"")+"/confirm-registration?id="+registrationId;
    
    newMail()
      .setFrom("registrations@activiti.com")
      .setTo(registration.getEmail())
      .setSubject("Confirm your Activiti registration")
      .setBody( "Hi, \n\n" +
                "Please click this link to confirm your registration: \n\n" +
                confirmationLink+" \n\n" +
                "Cheers," +
                "Activiti.com")
      .send();
  }
}
