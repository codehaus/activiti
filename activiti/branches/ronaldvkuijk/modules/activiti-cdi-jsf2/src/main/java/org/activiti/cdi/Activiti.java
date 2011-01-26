package org.activiti.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.jobexecutor.JobExecutor;

@Singleton
@Startup
@ApplicationScoped
public class Activiti {

	private static final long serialVersionUID = 1L;

	private ProcessEngine processEngine;
	private RuntimeService runtimeService;
	private ManagementService managementService;
	private JobExecutor jobExecutor;
	private TaskService taskService;
	private IdentityService identityService;
	private RepositoryService repositoryService;
 
	public Activiti() {
	}

	@PostConstruct
	public void init() {
		System.out.println("Init: " + this);
		//ProcessEngines.init();
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResourceDefault();
		processEngineConfiguration
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
		processEngineConfiguration.setJdbcMaxCheckoutTime(1000);
		this.processEngine = processEngineConfiguration.buildProcessEngine();
		this.runtimeService = processEngine.getRuntimeService();
		this.managementService = processEngine.getManagementService();
		this.identityService = processEngine.getIdentityService();
		this.repositoryService = processEngine.getRepositoryService();
		this.taskService = processEngine.getTaskService();
		this.jobExecutor = ((ProcessEngineConfigurationImpl) processEngineConfiguration)
				.getJobExecutor();

		User user = identityService.createUserQuery()
				.userId("ronald").singleResult();

		if (user == null) {
			user = identityService.newUser("ronald");
		}
		user.setFirstName("Ronald");
		user.setEmail("rvkuijk@intercommit.nl");
		user.setLastName("Kuijk, van");
		user.setPassword("ronald");
		identityService.saveUser(user);

		Group group = identityService.createGroupQuery()
				.groupId("R&D").singleResult();
		if (group == null) {
			group = identityService.newGroup("R&D");
		}
		group.setType("R&D Type");
		group.setName("R&D Name");
		identityService.saveGroup(group);

		if (identityService.createUserQuery().userId("ronald")
				.memberOfGroup("R&D").count() != 1) {
			identityService.createMembership("ronald", "R&D");
		}

	}

	@PreDestroy
	public void cleanup() {
		ProcessEngines.destroy();
	}

	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public RuntimeService getRuntimeService() {
		return runtimeService;
	}

	public ManagementService getManagementService() {
		return managementService;
	}

	public void pauseJobExecutor() {
		jobExecutor.pause();
	}

	public void resumeJobExecutor() {
		jobExecutor.resume();
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public TaskService getTaskService() {
		return this.taskService;
	}

	public void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}

	public IdentityService getIdentityService() {
		return identityService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}
}