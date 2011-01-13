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

package org.activiti.engine.test.bpmn.exec;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.test.Deployment;


/**
 * @author Ronald van Kuijk
 */
public class ExecServiceTaskTest extends PluggableActivitiTestCase {
  
  @Deployment
  public void testSimpleOSCommand() throws Exception {
    
    // All is fixed in the process definition
    runtimeService.startProcessInstanceByKey("simpleOSCommand");
    
    // How to assert it has worked? Return value?
  }
  
  @Deployment
  public void testExpressionsOSCommand() throws Exception {
    
    String command = "echo Hello Universe";
    String path = "/";
    String params = "Hmmm needed";
    
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("a_command", command);
    vars.put("justAPath", path);
    vars.put("params", params);
    
    runtimeService.startProcessInstanceByKey("expressionsOSCommand", vars);
    
    // How to assert it has worked? Return value?
  }

}
