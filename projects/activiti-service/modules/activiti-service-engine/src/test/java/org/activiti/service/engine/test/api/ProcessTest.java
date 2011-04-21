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

import java.util.logging.Logger;

import org.activiti.service.api.model.Process;
import org.activiti.service.api.model.Processes;
import org.activiti.service.api.model.process.Activity;
import org.activiti.service.api.model.process.Transition;
import org.activiti.service.engine.test.ActivitiTestCase;
import org.activiti.service.impl.persistence.JsonPrettyPrinter;


/**
 * @author Tom Baeyens
 */
public class ProcessTest extends ActivitiTestCase {
  
  private static Logger log = Logger.getLogger(ProcessTest.class.getName());

  public void testProcess() {
    Process process = new Process()
      .setName("My first process")
      .addActivity(new Activity()
        .setId("a")
        .addTransition(new Transition().setId("t1"))
        .addTransition(new Transition().setId("t2"))
        .addActivity(new Activity()
          .setId("a1")
          .addTransition(new Transition().setId("t3"))
          .addTransition(new Transition().setId("t4")))
        .addActivity(new Activity()
          .setId("a2")))
      .addActivity(new Activity()
        .setId("b"));
    
    Processes processes = activiti.getManager(Processes.class);
    processes.insert(process);
    
    process = (Process) processes.findOneByOid(process.getOid());

    assertEquals("My first process", process.getName());
    
    log.fine("process "+JsonPrettyPrinter.toJsonPrettyPrint(process.getJson()));
  }
}
