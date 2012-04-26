package org.activiti.examples.bpmn.startAuthorization;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.StartAuthorizationException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;

public class StartAuthorizationTest extends PluggableActivitiTestCase {

  IdentityService identityService;

  User userInGroup1;
  User userInGroup2;
  User userInGroup3;

  Group group1;
  Group group2;
  Group group3;

  protected void setUpUsersAndGroups() throws Exception {

    identityService = processEngine.getIdentityService();

    // create users
    userInGroup1 = identityService.newUser("userInGroup1");
    identityService.saveUser(userInGroup1);

    userInGroup2 = identityService.newUser("userInGroup2");
    identityService.saveUser(userInGroup2);

    userInGroup3 = identityService.newUser("userInGroup3");
    identityService.saveUser(userInGroup3);

    // create groups
    group1 = identityService.newGroup("group1");
    identityService.saveGroup(group1);

    group2 = identityService.newGroup("group2");
    identityService.saveGroup(group2);

    group3 = identityService.newGroup("group3");
    identityService.saveGroup(group3);

    // relate users to groups
    identityService.createMembership(userInGroup1.getId(), group1.getId());
    identityService.createMembership(userInGroup2.getId(), group2.getId());
    identityService.createMembership(userInGroup3.getId(), group3.getId());
  }

  protected void tearDownUsersAndGroups() throws Exception {
    identityService.deleteMembership(userInGroup1.getId(), group1.getId());
    identityService.deleteMembership(userInGroup2.getId(), group2.getId());
    identityService.deleteMembership(userInGroup3.getId(), group3.getId());

    identityService.deleteGroup(group1.getId());
    identityService.deleteGroup(group2.getId());
    identityService.deleteGroup(group3.getId());

    identityService.deleteUser(userInGroup1.getId());
    identityService.deleteUser(userInGroup2.getId());
    identityService.deleteUser(userInGroup3.getId());

  }

  @Deployment
  public void testPotentialStarter() throws Exception {
    // first check an unauthorized user. An exception is expected

    setUpUsersAndGroups();

    try {
    
	    // an undefined user should not be able to start process
	    identityService.setAuthenticatedUserId("unauthorizedUser");
	    try {
	      runtimeService.startProcessInstanceByKey("potentialStarter");
	
	    } catch (StartAuthorizationException ae) {
	
	    } catch (Exception e) {
	      fail("Wrong exception caught. StartAuthorizationException expected, " + e.getClass().getName() + " caught.");
	    }
	
	    // check with an authorized user in activiti:candidateStarterUsers attibute
	    identityService.setAuthenticatedUserId("user1");
	    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
	
	    // make sure more comma seperation for users in activiti:candidateStarterUsers work properly
	    identityService.setAuthenticatedUserId("user2");
	    processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
	    
	    // check with an authorized user defined in activiti:potentialStarter in user() format
	    identityService.setAuthenticatedUserId("user3");
	    processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
	
	    // check the user in a group defined in activiti:candidateStarterGroups
	    identityService.setAuthenticatedUserId("userInGroup1");
	    processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
	
	    // check the user in a group defined in activiti:potentialStarter as default target
	    identityService.setAuthenticatedUserId("userInGroup2");
	    processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
	
	    // check the user in a group defined in activiti:potentialStarter in group() format
	    identityService.setAuthenticatedUserId("userInGroup3");
	    processInstance = runtimeService.startProcessInstanceByKey("potentialStarter");
	    assertProcessEnded(processInstance.getId());
	    assertTrue(processInstance.isEnded());
    } finally {

    tearDownUsersAndGroups();
    }

  }

  /*
   * if there is no security definition, then user authorization check is not
   * done. This ensures backward compatibility
   */
  @Deployment
  public void testPotentialStarterNoDefinition() throws Exception {
    identityService = processEngine.getIdentityService();

    identityService.setAuthenticatedUserId("someOneFromMars");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("potentialStarterNoDefinition");
    assertProcessEnded(processInstance.getId());
    assertTrue(processInstance.isEnded());
  }
  
  // this test checks the list without user constraint
  @Deployment
	public void testProcessDefinitionList() throws Exception {
	  
    setUpUsersAndGroups();
    try {

      // do not mention user, all processes should be selected
      List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc()
              .orderByProcessDefinitionName().asc().orderByProcessDefinitionCategory().asc().list();

      assertEquals(4, processDefinitions.size());

      assertEquals("process1", processDefinitions.get(0).getKey());
      assertEquals("process2", processDefinitions.get(1).getKey());
      assertEquals("process3", processDefinitions.get(2).getKey());
      assertEquals("process4", processDefinitions.get(3).getKey());

      // check user1, process3 has "user1" as only authorized starter, and
      // process2 has two authorized starters, of which one is "user1"
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
              .orderByProcessDefinitionCategory().asc().startableByUser("user1").list();

      assertEquals(2, processDefinitions.size());
      assertEquals("process2", processDefinitions.get(0).getKey());
      assertEquals("process3", processDefinitions.get(1).getKey());

      // "user2" can only start process2
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
              .orderByProcessDefinitionCategory().asc().startableByUser("user2").list();

      assertEquals(1, processDefinitions.size());
      assertEquals("process2", processDefinitions.get(0).getKey());

      // no process could be started with "user4"
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
              .orderByProcessDefinitionCategory().asc().startableByUser("user4").list();

      assertEquals(0, processDefinitions.size());

      // "userInGroup3" is in "group3" and can start only process4 via group authorization
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
              .orderByProcessDefinitionCategory().asc().startableByUser("userInGroup3").list();

      assertEquals(1, processDefinitions.size());
      assertEquals("process4", processDefinitions.get(0).getKey());

      // "userInGroup2" can start process4, via both user and group authorizations
      // but we have to be sure that process4 appears only once
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionName().asc().orderByProcessDefinitionVersion().asc()
              .orderByProcessDefinitionCategory().asc().startableByUser("userInGroup2").list();

      assertEquals(1, processDefinitions.size());
      assertEquals("process4", processDefinitions.get(0).getKey());

    } finally {
      tearDownUsersAndGroups();
    }
  }

}
