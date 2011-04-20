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

import org.activiti.service.api.model.Deployment;
import org.activiti.service.api.model.Deployments;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class DeploymentsTest extends ActivitiTestCase {

  public void testDeployments() {
    Deployments deployments = activiti.getManager(Deployments.class);
    
    Deployment deployment = new Deployment()
      .setName("hi.bar");
    
    deployments.insert(deployment);
    
    deployment.setName("by.jar");
    deployments.update(deployment);

    Deployment example = new Deployment()
      .setName("by.jar");
  
    List<Deployment> fetched = deployments.findAllByExample(example);
    assertEquals(1, fetched.size());
    
    deployments.delete(fetched.get(0));
  }
}
