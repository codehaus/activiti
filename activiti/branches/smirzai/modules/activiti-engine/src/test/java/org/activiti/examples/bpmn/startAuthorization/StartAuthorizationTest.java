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
	  
			List<ProcessDefinition> processDefinitions = repositoryService
					.createProcessDefinitionQuery().orderByProcessDefinitionName()
					.asc().orderByProcessDefinitionName().asc()
					.orderByProcessDefinitionCategory().asc().list();
	
			ProcessDefinition processDefinition = processDefinitions.get(0);
			assertEquals("process1", processDefinition.getKey());
			assertEquals("Process1", processDefinition.getName());
			assertTrue(processDefinition.getId().startsWith("process1:1"));
			assertEquals("Examples", processDefinition.getCategory());
	
			processDefinition = processDefinitions.get(1);
			assertEquals("process2", processDefinition.getKey());
			assertEquals("Process2", processDefinition.getName());
			assertTrue(processDefinition.getId().startsWith("process2:1"));
			assertEquals("Examples", processDefinition.getCategory());
	
			processDefinition = processDefinitions.get(2);
			assertEquals("process3", processDefinition.getKey());
			assertEquals("Process3", processDefinition.getName());
			assertTrue(processDefinition.getId().startsWith("process3:1"));
			assertEquals("Examples", processDefinition.getCategory());
	
			processDefinitions = repositoryService.createProcessDefinitionQuery()
					.orderByProcessDefinitionName().asc()
					.orderByProcessDefinitionVersion().asc()
					.orderByProcessDefinitionCategory().asc()
					.startableByUser("user1").list();
			
			assertEquals(2, processDefinitions.size());
	
			
			processDefinitions = repositoryService.createProcessDefinitionQuery()
					.orderByProcessDefinitionName().asc()
					.orderByProcessDefinitionVersion().asc()
					.orderByProcessDefinitionCategory().asc()
					.startableByUser("user2").list();
			
			assertEquals(1, processDefinitions.size());
	
			processDefinitions = repositoryService.createProcessDefinitionQuery()
					.orderByProcessDefinitionName().asc()
					.orderByProcessDefinitionVersion().asc()
					.orderByProcessDefinitionCategory().asc()
					.startableByUser("user3").list();
			
			assertEquals(0, processDefinitions.size());
	  } finally {
		tearDownUsersAndGroups();
	  }
	}

}
