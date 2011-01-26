package org.activiti.cdi;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.activiti.cdi.jsf.JSFUtility;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;

/*
 * <f:metadata>
 *   <f:event type="preRenderView" listener="#{management.initConversation}" />
 * </f:metadata>
 * 
 * 
 */
@Named("management")
@SessionScoped
public class ManagementBean implements Serializable {

	@Inject
	Activiti activiti;

	@Inject
	//@LoggedIn
	User currentUser;
	
	private String businessKey;
	private String status;
	private String jobExecutorStatus;

	private int piFirstResult = 0;
	private int pageSize = 100;


	@PostConstruct
	private void init() {
		if (activiti == null) {
			this.status = "Activiti not running correctly";
		} else if (activiti.getManagementService() == null) {
			this.status = "Management service not active";
		} else {
			this.status = "Running and deployed";
			this.jobExecutorStatus = "Running";
		}
	}

	public String getStatus() {
		return this.status;
	}

	public void deploy() {
		
		activiti.getIdentityService().setAuthenticatedUserId(currentUser.getId());
		DeploymentBuilder db = activiti.getRepositoryService()
				.createDeployment().name("POC");
		db.addClasspathResource("diagrams/poc.activiti.bpmn20.xml");
		Deployment d = db.deploy();
		JSFUtility.info("Deployed", "Deployed the process with id " + d.getId()
				+ " and name " + d.getName());
	}

	public String getJobExecutorStatus() {
		return this.jobExecutorStatus;
	}

	public void signalService(String id) {
		try {
			activiti.getRuntimeService().signal(id);
		} catch (Exception e) {
			JSFUtility.error("Error signalling execution", e.getMessage());
		}
	}

	public void executeJobNow(String id) {
		try {
			activiti.getManagementService().executeJob(id);
			JSFUtility.info("Job " + id + " executed");
		} catch (Exception e) {
			JSFUtility.error("Error executing Job", e.getMessage());
		}
	}

	public List<Job> getJobs() {
		List<Job> jl = activiti.getManagementService().createJobQuery()
				.listPage(piFirstResult, pageSize);
		if (jl.size() == 1 && jl.get(0) == null)
			jl.remove(0);
		return jl;
	}

	public List<Execution> getRunningExecutions() {
		List<Execution> el = activiti.getRuntimeService()
				.createExecutionQuery().listPage(piFirstResult, pageSize);
		if (el.size() == 1 && el.get(0) == null)
			el.remove(0);
		return el;
	}


	public void toggle() {	
		if (this.jobExecutorStatus.equals("Running")) {
			this.jobExecutorStatus = "Paused";
			activiti.pauseJobExecutor();
			JSFUtility.info("JobExecutor paused");
		} else if (this.jobExecutorStatus.equals("Paused")) {
			this.jobExecutorStatus = "Running";
			JSFUtility.info("JobExecutor resumed");
		} else {
			JSFUtility.error("Error jobexecutor", "Unknown status: "
					+ jobExecutorStatus);
		}
	}
}