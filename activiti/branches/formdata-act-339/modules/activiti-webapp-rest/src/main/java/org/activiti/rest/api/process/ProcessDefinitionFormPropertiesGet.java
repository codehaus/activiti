package org.activiti.rest.api.process;

import java.util.Map;

import org.activiti.rest.util.ActivitiRequest;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

/**
 * Returns properties for a process definitions start form.
 *
 * @author Erik Winlof
 */
public class ProcessDefinitionFormPropertiesGet extends ActivitiWebScript {

  /**
   * Returns details about a process definition.
   *
   * @param req The webscripts request
   * @param status The webscripts status
   * @param cache The webscript cache
   * @param model The webscripts template model
   */
  @Override
  protected void executeWebScript(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {
    String processDefinitionId = req.getMandatoryPathParameter("processDefinitionId");
    model.put("formProperties", getFormService().getStartFormData(processDefinitionId).getFormProperties());
  }

}
