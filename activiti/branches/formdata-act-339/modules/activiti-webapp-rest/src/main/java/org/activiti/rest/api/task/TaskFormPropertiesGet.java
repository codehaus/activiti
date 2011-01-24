package org.activiti.rest.api.task;

import java.util.Map;

import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.task.TaskEntity;
import org.activiti.rest.model.RestTask;
import org.activiti.rest.util.ActivitiRequest;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

/**
 * Returns a task's form properties.
 *
 * @author Erik Winlof
 */
public class TaskFormPropertiesGet extends ActivitiWebScript {


  /**
   * Returns a task's form properties 
   *
   * @param req The webscripts request
   * @param status The webscripts status
   * @param cache The webscript cache
   * @param model The webscripts template model
   */
  @Override
  protected void executeWebScript(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {
    String taskId = req.getMandatoryPathParameter("taskId");
    model.put("formProperties", getFormService().getTaskFormData(taskId).getFormProperties());
  }
}
