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

import java.util.ArrayList;
import java.util.List;

import org.activiti.service.api.Activiti;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class Registrations {

  protected Activiti activiti;
  protected DBCollection registrations;
  
  public Registrations(Activiti activiti, DBCollection registrations) {
    this.activiti = activiti;
    this.registrations = registrations;
  }
  
  public void register(Registration registration) {
    DBObject taskJson = registration.toJson();
    registrations.insert(taskJson);
    ObjectId objectId = (ObjectId) taskJson.get("_id");
    String registrationId = objectId.toString();
    registration.setOid(registrationId);

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
    DBObject query = new BasicDBObject();
    query.put("_id", new ObjectId(registrationId));
    DBObject dbObject = registrations.findOne(query);
    // if registration does not exist return null.
    if (dbObject==null) {
      return null;
    }
    Registration registration = new Registration(dbObject);
    
    // create user from registration
    User user = new User();
    user.setId(registration.getUserId());
    user.setPassword(registration.getPassword());
    activiti.getUsers().insertUser(user);
    
    // delete registration
    registrations.remove(dbObject);
    
    return registration;
  }

  public List<Registration> findRegistrationsByExample(Registration query) {
    List<Registration> registrations = new ArrayList<Registration>(); 
    DBCursor dbCursor = this.registrations.find(query.toJson());
    while (dbCursor.hasNext()) {
      registrations.add(new Registration(dbCursor.next()));
    }
    return registrations;
  }
}
