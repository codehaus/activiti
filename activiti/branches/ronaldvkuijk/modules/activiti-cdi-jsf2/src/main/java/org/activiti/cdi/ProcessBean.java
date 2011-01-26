package org.activiti.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import org.activiti.cdi.jsf.JSFUtility;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.ibatis.exceptions.PersistenceException;

@Named
@SessionScoped
public class ProcessBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6571210790561113141L;

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Inject
	Activiti activiti;

	@Inject
	User currentUser;
	
	@Inject
	TaskBean taskBean;
	
	private String businessKey;
	private String selectedProcessDefinition = "";
	private List<ProcessDefinition> deployedProcesses; 
	
	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		refresh();
	}
	
	private void refresh() {
		this.deployedProcesses = activiti.getRepositoryService().createProcessDefinitionQuery().latestVersion().list();
	}

	public void deploy() {
		
		activiti.getIdentityService().setAuthenticatedUserId(currentUser.getId());
		DeploymentBuilder db = activiti.getRepositoryService()
				.createDeployment().name("POC");
		db.addClasspathResource("diagrams/poc.activiti.bpmn20.xml");
		Deployment d = db.deploy();
		refresh();
		JSFUtility.info("Deployed", "Deployed the process with id " + d.getId()
				+ " and name " + d.getName());
	}

	public void start(String processName) {
		log.fine("businessKey: >" + businessKey + "<");
		if (businessKey != null && !"".equals(businessKey)) {
			try {
				
				Map<String, Object> variables = new HashMap<String, Object>();
				
				// TODO Check how to 'outject' the processInstance object
				ProcessInstance pi = activiti.getRuntimeService()
						.startProcessInstanceByKey(processName, businessKey, variables);

				taskBean.refresh();
			} catch (PersistenceException pe) {

				// do not use instanceof so the class is not needed in all situations
				if (pe.getCause() != null && pe.getCause().getClass().getName().equals("MySQLIntegrityConstraintViolationException")) {
					JSFUtility.error("Duplicate businesskey",
							"Businesskey already in use, choose another one");
				} else {
					JSFUtility.error("Activiti exception",
							pe.getLocalizedMessage());
				}
			} catch (ActivitiException ae) {
				JSFUtility
						.error("Activiti exception", ae.getLocalizedMessage());
			}

		} else {
			JSFUtility.error("BusinessKey is empty",
					"The businessKey may not be empty");
		}
	}

	public String getBusinessKey() {
		return this.businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public void setSelectedProcessDefinition(String selectedProcessDefinition) {
		this.selectedProcessDefinition = selectedProcessDefinition;
	}

	public String getSelectedProcessDefinition() {
		return this.selectedProcessDefinition;
	}
	
	public List<ProcessDefinition> getDeployedProcesses() {
		return this.deployedProcesses;
	}
}