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

package org.activiti.service.engine.test.api;

import java.util.List;
import java.util.logging.Logger;

import org.activiti.service.api.model.Case;
import org.activiti.service.api.model.Cases;
import org.activiti.service.api.model.nested.PersonLink;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class CasesTest extends ActivitiTestCase {
  
  private static Logger log = Logger.getLogger(CasesTest.class.getName());

  public void testCasePeopleLinks() {
    Case caze = new Case()
      .setName("run")
      .setAssignee("kermit");
    
    List<PersonLink> personLinks = caze.getPersonLinks();
    personLinks.add(new PersonLink().setUserId("johndoe").setRole("interested"));
    personLinks.add(new PersonLink().setUserId("joesmoe").setRole("interested"));
    
    activiti
      .getManager(Cases.class)
      .insert(caze);
    
    caze = activiti
      .getManager(Cases.class)
      .findOneByOid(caze.getOid());
    
    log.fine(caze.getJsonTextPrettyPrint());
  }
}
