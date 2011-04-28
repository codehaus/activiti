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

package org.activiti.service.engine.test.json;

import java.util.Map;
import java.util.logging.Logger;

import org.activiti.service.api.cases.Case;
import org.activiti.service.api.identity.Group;
import org.activiti.service.api.identity.Registration;
import org.activiti.service.api.identity.User;
import org.activiti.service.api.identity.UserLink;
import org.activiti.service.api.model.process.activity.WaitState;
import org.activiti.service.api.process.definition.ActivityDefinition;
import org.activiti.service.api.process.definition.FlowDefinition;
import org.activiti.service.api.process.definition.Transition;
import org.activiti.service.api.process.instance.FlowInstance;
import org.activiti.service.engine.test.ActivitiTestCase;
import org.activiti.service.impl.json.JsonConverter;


/**
 * @author Tom Baeyens
 */
public class JsonTest extends ActivitiTestCase {
  
  private static Logger log = Logger.getLogger(JsonTest.class.getName());
  
  public void testFlowJson() {
    FlowDefinition flowDefinition = new FlowDefinition()
      .setId("My first process")
      .addActivity(new ActivityDefinition()
        .setId("start")
        .setActivityType(new WaitState())
        .addTransition(new Transition().setId("t1").setDestination("end")))
      .addActivity(new ActivityDefinition()
        .setId("end")
        .setActivityType(new WaitState()));

    JsonConverter jsonConverter = activiti.getJsonConverter();
    Object json = jsonConverter.toJson(flowDefinition);
    flowDefinition = (FlowDefinition) jsonConverter.toBean(json, FlowDefinition.class);
    
    assertEquals("My first process", flowDefinition.getId());
    ActivityDefinition startActivityDefinition = flowDefinition.getActivityDefinitions().get("start");
    assertEquals(WaitState.class, startActivityDefinition.getActivityType().getClass());
    Transition transition = startActivityDefinition.getTransitions().get(0);
    assertEquals("t1", transition.getId());
    assertEquals("end", transition.getDestination());
    assertEquals(1, startActivityDefinition.getTransitions().size());
    
    ActivityDefinition endActivityDefinition = flowDefinition.getActivityDefinitions().get("end");
    assertEquals(WaitState.class, endActivityDefinition.getActivityType().getClass());
    assertEquals(0, endActivityDefinition.getTransitions().size());
    
    FlowInstance flowInstance = flowDefinition.createNewFlowInstance();

    json = jsonConverter.toJson(flowInstance);
    flowInstance = (FlowInstance) jsonConverter.toBean(json, FlowInstance.class);
    flowInstance.setFlowDefinition(flowDefinition);
    
    assertEquals("start", flowInstance.getActivityInstances().get(0).getActivityDefinition().getId());

    flowInstance
      .getTrigger("start")
      .set("var1", "value1")
      .set("var2", "value2")
      .fire();

    assertEquals("end", flowInstance.getActivityInstances().get(0).getActivityDefinition().getId());

    log.fine(activiti.getJsonConverter().getJsonTextPrettyPrintFromBean(flowInstance));
  }

  public void testUserJson() {
    User user = new User()
      .setId("johndoe")
      .setPassword("secret")
      .addGroupId("winners")
      .addGroupId("loosers");
    
    JsonConverter jsonConverter = activiti.getJsonConverter();
    Object json = jsonConverter.toJson(user);
    user = (User) jsonConverter.toBean(json, User.class);
    
    assertEquals("johndoe", user.getId());
    assertEquals("secret", user.getPassword());
    assertEquals("[winners, loosers]", user.getGroupIds().toString());
  }
  
  public void testGroupJson() {
    Group group = new Group()
      .setId("winners")
      .setName("Winners");
    
    JsonConverter jsonConverter = activiti.getJsonConverter();
    Object json = jsonConverter.toJson(group);
    group = (Group) jsonConverter.toBean(json, Group.class);

    assertEquals("winners", group.getId());
    assertEquals("Winners", group.getName());
  }

  public void testCaseJson() {
    Case caze = new Case()
      .setName("save the planet")
      .setDescription("make sure our kids have a good time on it")
      .setOwner("me")
      .setAssignee("greenpeace")
      .addUserLink("kermit", "native green individual")
      .addUserLink("algore", "promotor");
    
    JsonConverter jsonConverter = activiti.getJsonConverter();
    Object json = jsonConverter.toJson(caze);
    caze = (Case) jsonConverter.toBean(json, Case.class);

    assertEquals("save the planet", caze.getName());
    assertEquals("make sure our kids have a good time on it", caze.getDescription());
    assertEquals("me", caze.getOwner());
    assertEquals("greenpeace", caze.getAssignee());
    
    Map<String, UserLink> userLinks = caze.getUsers();
    assertEquals("native green individual", userLinks.get("kermit").getRole());
    assertEquals("promotor", userLinks.get("algore").getRole());
  }
  
  public void testRegistrationJson() {
    Registration registration = new Registration();
    registration.setUserId("johndoe");
    registration.setEmail("johndoe@johndoe.kom");
    registration.setPassword("secret");
    registration.setClientIp("johndoeslaptop");
    registration.setUrl("/item?id=928374");

    JsonConverter jsonConverter = activiti.getJsonConverter();
    Object json = jsonConverter.toJson(registration);
    registration = (Registration) jsonConverter.toBean(json, Registration.class);

    assertEquals("johndoe", registration.getUserId());
    assertEquals("johndoe@johndoe.kom", registration.getEmail());
    assertEquals("secret", registration.getPassword());
    assertEquals("johndoeslaptop", registration.getClientIp());
    assertEquals("/item?id=928374", registration.getUrl());
  }
}
