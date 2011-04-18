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

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.activiti.service.api.ActivitiException;


/**
 * @author Tom Baeyens
 */
public class Mail {

  protected MailService mailService;
  protected String from;
  protected String to;
  protected String subject;
  protected String body;
  
  public Mail(MailService mailService) {
    this.mailService = mailService;
  }

  public void send() {
    try {
      // create a new Session object
      Session session = Session.getInstance(mailService.getMailProperties());

      // create a new MimeMessage object (using the Session created above)
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
      message.setSubject(subject);
      message.setContent(body, "text/plain");

      Transport.send(message);
      
    } catch (Exception e) {
      throw new ActivitiException("sending mail failed", e);
    }
  }
  
  public String getFrom() {
    return from;
  }
  
  public Mail setFrom(String from) {
    this.from = from;
    return this;
  }
  
  public String getTo() {
    return to;
  }
  
  public Mail setTo(String to) {
    this.to = to;
    return this;
  }
  
  public String getSubject() {
    return subject;
  }
  
  public Mail setSubject(String subject) {
    this.subject = subject;
    return this;
  }
  
  public String getBody() {
    return body;
  }
  
  public Mail setBody(String body) {
    this.body = body;
    return this;
  }
}
