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

import java.util.logging.Logger;

import org.activiti.service.api.Cases;
import org.activiti.service.api.cases.Case;
import org.activiti.service.engine.test.RestTestCase;



/**
 * @author Tom Baeyens
 */
public class CasesRestTest extends RestTestCase {
  
  private static Logger log = Logger.getLogger(CasesRestTest.class.getName());

  public void testTaskLists() {
    Case caseOne = new Case()
      .setName("helloworld")
      .setAssignee("kermit");
    activiti.getManager(Cases.class).insert(caseOne);
    
    Case caseTwo = new Case()
      .setName("hiuniverse")
      .setAssignee("kermit");
    activiti.getManager(Cases.class).insert(caseTwo);
    
    Case caseThree= new Case()
      .setName("yowwhatsup")
      .setAssignee("fozzie");
    activiti.getManager(Cases.class).insert(caseThree);

    log.info("cases of default user kermit:");
    createRestRequest("/cases?first=0&max=10").get();
    
    setUser("fozzie", "fozzie", true);

    log.info("cases of user fozzie:");
    createRestRequest("/cases?first=0&max=10").get();
    
    setUser("misspiggy", "misspiggy", true);

    log.info("cases of user misspiggy:");
    createRestRequest("/cases?first=0&max=10").get();

    log.info("case two");
    createRestRequest("/case/"+caseTwo.getOid()).get();
  }
  
  public void getTaskDetails() {
  }
}
