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
import org.activiti.service.api.model.process.activity.AutomaticActivityType;
import org.activiti.service.api.model.process.activity.EndActivityType;
import org.activiti.service.api.model.process.activity.StartActivityType;
import org.activiti.service.api.model.process.activity.WaitStateActivityType;
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
    // First users will author a new process in the model repository.
    // Both model repository and deployed processes use the FlowDefinition as the entity.
    // So let's create a flow definition for modelling...
    FlowDefinition modellingFlowDefinition = new FlowDefinition()
      .setId("My first process")
      .addActivity(new ActivityDefinition()
        .setId("start")
        .setActivityType(new StartActivityType())
        .addTransition(new Transition().setTo("auto1")))
      .addActivity(new ActivityDefinition()
        .setId("auto1")
        .setActivityType(new AutomaticActivityType())
        .addTransition(new Transition().setTo("wait")))
      .addActivity(new ActivityDefinition()
        .setId("wait")
        .setActivityType(new WaitStateActivityType())
        .addTransition(new Transition().setTo("auto2")))
      .addActivity(new ActivityDefinition()
        .setId("auto2")
        .setActivityType(new AutomaticActivityType())
        .addTransition(new Transition().setTo("end")))
      .addActivity(new ActivityDefinition()
        .setId("end")
        .setActivityType(new EndActivityType()));
    
    // Storing the process in the model repository
    // The user now can get a coffee and continue modelling later on
    ModellingFlowDefinitions modellingFlowDefinitions = activiti.getManager(ModellingFlowDefinitions.class);
    modellingFlowDefinitions.insert(modellingFlowDefinition);
    
    // The user now returned from coffee break and wants to deploy the process
    // The flowDefinition is fetched from the model repository
    modellingFlowDefinition = modellingFlowDefinitions.findOneByOid(modellingFlowDefinition.getOid());
    // An executable flow definition is build from the model flow definition
    // At the moment only the start activity is initialized at this point
    FlowDefinition executableFlowDefinition = modellingFlowDefinition.buildExecutableProcess();
    // And then the executable flow definition is stored in the collection for executable flow definitions
    ExecutableFlowDefinitions executableProcesses = activiti.getManager(ExecutableFlowDefinitions.class);
    executableProcesses.insert(executableFlowDefinition);

    // Now, the user can start flow instances for the executable process
    // First we get the flow definition from the repository
    executableFlowDefinition = executableProcesses.findOneByOid(executableFlowDefinition.getOid());

    // A start is a wait state.  So after creation of the new flow instance, it is positioned 
    // in the start activity and a trigger is created in that activity instance.
    // That way the nested data structures can be initialized before the initial data (from e.g. the start flow form) 
    // is submitted in the start activity instance
    FlowInstance flowInstance = executableFlowDefinition.createNewFlowInstance();

    log.fine("let's look at the initial flow instance data structure...");
    log.fine(activiti.getJsonConverter().toJsonTextPretty(flowInstance));

    // First we fetch the trigger, then set data in the trigger and then fire it  
    flowInstance
      .getTrigger("start")
      .set("var1", "value1")
      .set("var2", "value2")
      .fire();

    log.fine("now let's look at the flow instance data structure when it is in the middle wait state...");
    log.fine(activiti.getJsonConverter().toJsonTextPretty(flowInstance));

    flowInstance
      .getTrigger("wait")
      .set("var1", "updated")
      .fire();

    log.fine("and here when the flow instance is finished...");
    log.fine(activiti.getJsonConverter().toJsonTextPretty(flowInstance));

//    
//    Flows flows = activiti.getManager(Flows.class);
//    flows.insert(flow);
//    
//    log.fine("process "+JsonPrettyPrinter.toJsonPrettyPrint(modellingProcess.getJson()));
  }
}
