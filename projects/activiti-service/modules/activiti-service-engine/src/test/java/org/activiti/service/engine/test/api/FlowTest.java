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

import org.activiti.service.api.ModellingFlowDefinitions;
import org.activiti.service.api.model.process.activity.EndActivityType;
import org.activiti.service.api.model.process.activity.StartActivityType;
import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.FlowDefinition;
import org.activiti.service.api.process.definition.Transition;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class FlowTest extends ActivitiTestCase {
  
  private static Logger log = Logger.getLogger(FlowTest.class.getName());

  public void testProcess() {
    FlowDefinition modellingFlowDefinition = new FlowDefinition()
      .setId("My first process")
      .addActivity(new ActivityDefinition()
        .setId("start")
        .setType(new StartActivityType())
        .addTransition(new Transition().setId("t1").setDestination("end")))
      .addActivity(new ActivityDefinition()
        .setId("end")
        .setType(new EndActivityType()));
    
    ModellingFlowDefinitions modellingFlowDefinitions = activiti.getManager(ModellingFlowDefinitions.class);
    modellingFlowDefinitions.insert(modellingFlowDefinition);
//    
//    modellingProcess = (Process) modellingProcesses.findOneByOid(modellingProcess.getOid());
//
//    assertEquals("My first process", modellingProcess.getName());
//
//    ExecutableProcesses executableProcesses = activiti.getManager(ExecutableProcesses.class);
//    
//    Process executableProcess = modellingProcess.buildExecutableProcess();
//    executableProcesses.insert(executableProcess);
//
//    executableProcess = (Process) executableProcesses.findOneByOid(executableProcess.getOid());
//
//    Flow flow = executableProcess.createNewFlow();
//    flow
//      .getTrigger("start")
//      .set("var1", "value1")
//      .set("var2", "value2")
//      .fire();
//    
//    Flows flows = activiti.getManager(Flows.class);
//    flows.insert(flow);
//    
//    log.fine("process "+JsonPrettyPrinter.toJsonPrettyPrint(modellingProcess.getJson()));
  }
}
