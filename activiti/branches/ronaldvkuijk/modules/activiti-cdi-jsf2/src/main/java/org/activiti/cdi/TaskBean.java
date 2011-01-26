package org.activiti.cdi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

@Named
@SessionScoped
public class TaskBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3682263872435608682L;

	@Inject
	Activiti activiti;

	@Inject
	//@LoggedIn
	User currentUser;

	private List<Task> myTasks = null;
	private Map<String, List<Task>> unAssignedTasks = null;

	private int selectedRowId;

	private Task selectedTask;
	private String selectedGroupId = "";
	private ArrayList<String> groupList;

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		refresh();
	}

	public List<Task> getMyTasks() {
		return myTasks;
	}

	public int getMyTasksCount() {
		return myTasks.size();
	}

	public int getTotalUnassignedTasks() {
		int total = 0;
		for (String group : this.unAssignedTasks.keySet()) {
			total = total + this.unAssignedTasks.get(group).size();
		}
		return total;
	}

	public ArrayList<String> getGroupList() {
		return this.groupList;
	}

	public List<Task> getUnassignedTasksPerGroup(String group) {
		return this.unAssignedTasks.get(group);
	}

	public void setSelectedTask(Task task) {
		this.selectedTask = task;
	}
	
	public Task getSelectedTask() {
		return this.selectedTask;
	}

	public void setSelectedGroupId(String selectedGroupId) {
//		JSFUtility.info("Group selected", "Group with ID " + selectedGroupId
//				+ " is selected for display");
		this.selectedGroupId = selectedGroupId;
	}

	public String getSelectedGroupId() {
		return selectedGroupId;
	}

	public void setSelectedRowId(int selectedRowId) {
//		JSFUtility.info("Row selected", "Row with number " + selectedRowId
//				+ " is selected");
		this.selectedRowId = selectedRowId;
	}

	public int getSelectedRowId() {
		return selectedRowId;
	}

	public void refresh() {
		this.myTasks = activiti.getTaskService().createTaskQuery()
				.taskAssignee(currentUser.getId()).list();
		this.unAssignedTasks = getUnassignedTasks();
	}

	private Map<String, List<Task>> getUnassignedTasks() {
		Map<String, List<Task>> taskOverview = new HashMap<String, List<Task>>();

		List<Group> groups = activiti.getIdentityService().createGroupQuery()
				.groupMember(currentUser.getId()).orderByGroupName().asc()
				.list();
		this.groupList = new ArrayList<String>();
		for (Group group : groups) {
			taskOverview.put(group.getId(),
					activiti.getTaskService().createTaskQuery()
							.taskUnnassigned()
							.taskCandidateGroup(group.getId()).list());
			groupList.add(group.getId());
		}
		return taskOverview;
	}
	 
	public void assign() throws ActivitiTaskException {
		// TODO (re)assigning still needs to be implemented in the core.
	}
	
	public void claim() throws ActivitiTaskException {
		
		ProcessInstance pi = 
			activiti.getRuntimeService().createProcessInstanceQuery().processInstanceId(selectedTask.getProcessInstanceId()).singleResult();
		pi.getBusinessKey();
		
		// Add a check for 'admin' and / or manager roles...
		if (selectedTask.getAssignee()!= null && !"".equals(selectedTask.getAssignee()) ) {
			throw new ActivitiTaskException("The task is already assigned");
		}
		
		activiti.getTaskService().claim(selectedTask.getId(),
				currentUser.getId());

		// Do not 'null the task here, so a redirect to the form is possible

		// Hmmm... this can be to quick... the previous ones should be committed
		// and this done separately.
		refresh();

	}
	
	public void complete() throws ActivitiTaskException {
		// Add a check for 'admin' and / or manager roles...
		
		if (!currentUser.getId().equals(selectedTask.getAssignee())) {
			throw new ActivitiTaskException("You are not the assignee of the task");
		}
		
		activiti.getTaskService().complete(selectedTask.getId());
		selectedTask = null;
		refresh();

	}
}