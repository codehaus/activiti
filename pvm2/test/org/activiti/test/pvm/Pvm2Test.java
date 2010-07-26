package org.activiti.test.pvm;
import junit.framework.TestCase;

import org.activiti.impl.pvm.process.ProcessDefinition;
import org.activiti.impl.pvm.process.ProcessDefinitionBuilder;
import org.activiti.impl.pvm.runtime.ActivityInstance;
import org.activiti.impl.pvm.runtime.ProcessInstance;
import org.activiti.test.pvm.activities.Automatic;
import org.activiti.test.pvm.activities.WaitState;


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

/**
 * @author Tom Baeyens
 */
public class Pvm2Test extends TestCase {

  public void testOne() {
    ProcessDefinition processDefinition = new ProcessDefinitionBuilder()
      .startActivity("start")
        .initial()
        .behaviour(new Automatic())
        .transition("one")
      .endActivity()
      .startActivity("one")
        .behaviour(new WaitState())
        .transition("two")
      .endActivity()
      .startActivity("two")
        .behaviour(new Automatic())
        .transition("three")
      .endActivity()
      .startActivity("three")
        .behaviour(new WaitState())
      .endActivity()
    .buildProcessDefinition();
    
    ProcessInstance processInstance = processDefinition.createProcessInstance();
    processInstance.start();
    
    ActivityInstance activityInstance = processInstance.findActivityInstance("one");
    assertNotNull(activityInstance);
    
    activityInstance.signal(null, null);

    activityInstance = processInstance.findActivityInstance("three");
    assertNotNull(activityInstance);
  }
}
