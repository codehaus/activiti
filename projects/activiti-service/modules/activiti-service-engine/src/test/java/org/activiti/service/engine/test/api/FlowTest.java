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

import org.activiti.service.api.ExecutableFlowDefinitions;
import org.activiti.service.api.ModellingFlowDefinitions;
import org.activiti.service.api.model.process.activity.WaitState;
import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.FlowDefinition;
import org.activiti.service.api.process.definition.Transition;
import org.activiti.service.api.process.instance.FlowInstance;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class FlowTest extends ActivitiTestCase {
  
  private static Logger log = Logger.getLogger(FlowTest.class.getName());

  public void testFlow() {
    FlowDefinition modellingFlowDefinition = new FlowDefinition()
      .setId("My first process")
      .addActivity(new ActivityDefinition()
        .setId("start")
        .setActivityType(new WaitState())
        .addTransition(new Transition().setId("t1").setDestination("end")))
      .addActivity(new ActivityDefinition()
        .setId("end")
        .setActivityType(new WaitState()));
    
    ModellingFlowDefinitions modellingFlowDefinitions = activiti.getManager(ModellingFlowDefinitions.class);
    modellingFlowDefinitions.insert(modellingFlowDefinition);
    
    modellingFlowDefinition = modellingFlowDefinitions.findOneByOid(modellingFlowDefinition.getOid());

    assertEquals("My first process", modellingFlowDefinition.getId());

    ExecutableFlowDefinitions executableProcesses = activiti.getManager(ExecutableFlowDefinitions.class);
    
    FlowDefinition executableFlowDefinition = modellingFlowDefinition.buildExecutableProcess();
    executableProcesses.insert(executableFlowDefinition);

    executableFlowDefinition = executableProcesses.findOneByOid(executableFlowDefinition.getOid());
    
    log.fine(activiti.getJsonConverter().getJsonTextPrettyPrintFromBean(executableFlowDefinition));

    FlowInstance flowInstance = executableFlowDefinition.createNewFlowInstance();
    flowInstance
      .getTrigger("start")
      .set("var1", "value1")
      .set("var2", "value2")
      .fire();

    log.fine(activiti.getJsonConverter().getJsonTextPrettyPrintFromBean(flowInstance));

//    
//    Flows flows = activiti.getManager(Flows.class);
//    flows.insert(flow);
//    
//    log.fine("process "+JsonPrettyPrinter.toJsonPrettyPrint(modellingProcess.getJson()));
  }
}
