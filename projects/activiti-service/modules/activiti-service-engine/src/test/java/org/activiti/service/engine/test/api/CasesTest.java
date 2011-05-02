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

import java.util.Map;

import org.activiti.service.api.Cases;
import org.activiti.service.api.cases.Case;
import org.activiti.service.api.identity.UserLink;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class CasesTest extends ActivitiTestCase {
  
  public void testCasePeopleLinks() {
    Case caze = new Case()
      .setName("run")
      .setDescription("burn calories")
      .addUserLink("kermit", UserLink.ROLE_ASSIGNEE);

    Cases cases = activiti.getManager(Cases.class);

    cases.insert(caze);
    caze = cases.findOneByOid(caze.getOid());
  
    caze.addUserLink("johndoe", "interested");
    caze.addUserLink("joesmoe", "interested");
  
    cases.update(caze);
    caze = cases.findOneByOid(caze.getOid());
    
    assertEquals("run", caze.getName());
    assertEquals("burn calories", caze.getDescription());
    assertEquals("kermit", caze.getAssignee());
    assertNull(caze.getOwner());

    Map<String, UserLink> users = caze.getUsers();
    assertEquals("interested", users.get("johndoe").getRole());
    assertEquals("interested", users.get("joesmoe").getRole());
    assertEquals(UserLink.ROLE_ASSIGNEE, users.get("kermit").getRole());
  }
}
