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
package org.activiti.examples.processdefinitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;

/**
 * @author Tom Baeyens
 */
public class ProcessDefinitionsTest extends PluggableActivitiTestCase {

  private static final String NAMESPACE = "xmlns='http://www.omg.org/spec/BPMN/20100524/MODEL'";

  private static final String TARGET_NAMESPACE = "targetNamespace='http://activiti.org/BPMN20'";

  public void testGetProcessDefinitions() {
    List<String> deploymentIds = new ArrayList<String>();
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='IDR' name='Insurance Damage Report 1' />" + "</definitions>")));
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='IDR' name='Insurance Damage Report 2' />" + "</definitions>")));
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='IDR' name='Insurance Damage Report 3' />" + "</definitions>")));
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='EN' name='Expense Note 1' />" + "</definitions>")));
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='EN' name='Expense Note 2' />" + "</definitions>")));

    List<ProcessDefinition> processDefinitions = repositoryService
      .createProcessDefinitionQuery()
      .orderByProcessDefinitionKey().asc()
      .orderByProcessDefinitionVersion().desc()
      .list();

    assertNotNull(processDefinitions);

    assertEquals(5, processDefinitions.size());

    ProcessDefinition processDefinition = processDefinitions.get(0);
    assertEquals("EN", processDefinition.getKey());
    assertEquals("Expense Note 2", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("EN:2"));
    assertEquals(2, processDefinition.getVersion());

    processDefinition = processDefinitions.get(1);
    assertEquals("EN", processDefinition.getKey());
    assertEquals("Expense Note 1", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("EN:1"));
    assertEquals(1, processDefinition.getVersion());

    processDefinition = processDefinitions.get(2);
    assertEquals("IDR", processDefinition.getKey());
    assertEquals("Insurance Damage Report 3", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("IDR:3"));
    assertEquals(3, processDefinition.getVersion());

    processDefinition = processDefinitions.get(3);
    assertEquals("IDR", processDefinition.getKey());
    assertEquals("Insurance Damage Report 2", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("IDR:2"));
    assertEquals(2, processDefinition.getVersion());

    processDefinition = processDefinitions.get(4);
    assertEquals("IDR", processDefinition.getKey());
    assertEquals("Insurance Damage Report 1", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("IDR:1"));
    assertEquals(1, processDefinition.getVersion());

    deleteDeployments(deploymentIds);
  }

  public void testDeployIdenticalProcessDefinitions() {
    List<String> deploymentIds = new ArrayList<String>();
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='IDR' name='Insurance Damage Report' />" + "</definitions>")));
    deploymentIds.add(deployProcessString(("<definitions " + NAMESPACE + " " + TARGET_NAMESPACE + ">" + "  <process id='IDR' name='Insurance Damage Report' />" + "</definitions>")));

    List<ProcessDefinition> processDefinitions = repositoryService
      .createProcessDefinitionQuery()
      .orderByProcessDefinitionKey().asc()
      .orderByProcessDefinitionVersion().desc()
      .list();

    assertNotNull(processDefinitions);
    assertEquals(2, processDefinitions.size());

    ProcessDefinition processDefinition = processDefinitions.get(0);
    assertEquals("IDR", processDefinition.getKey());
    assertEquals("Insurance Damage Report", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("IDR:2"));
    assertEquals(2, processDefinition.getVersion());

    processDefinition = processDefinitions.get(1);
    assertEquals("IDR", processDefinition.getKey());
    assertEquals("Insurance Damage Report", processDefinition.getName());
    assertTrue(processDefinition.getId().startsWith("IDR:1"));
    assertEquals(1, processDefinition.getVersion());
    
    deleteDeployments(deploymentIds);
  }
  
  private String deployProcessString(String processString) {
    String resourceName = "xmlString." + BpmnDeployer.BPMN_RESOURCE_SUFFIX;
    return repositoryService.createDeployment().addString(resourceName, processString).deploy().getId();
  }
  
  private void deleteDeployments(Collection<String> deploymentIds) {
    for (String deploymentId : deploymentIds) {
      repositoryService.deleteDeployment(deploymentId);
    }
  }
}
