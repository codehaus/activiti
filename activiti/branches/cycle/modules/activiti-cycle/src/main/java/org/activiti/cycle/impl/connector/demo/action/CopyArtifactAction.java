package org.activiti.cycle.impl.connector.demo.action;

import java.util.Map;

import org.activiti.cycle.ParametrizedFreemakerTemplateAction;
import org.activiti.cycle.impl.connector.demo.DemoConnector;

/**
 * Demo action which just copies the artifact (maybe multiple times) to
 * demonstrate actions
 * 
 * @author ruecker
 */
public class CopyArtifactAction extends ParametrizedFreemakerTemplateAction {

  @Override
  public String getFormResourceName() {
    return "/org/activiti/cycle/impl/connector/demo/action/CopyArtifactAction.html";
  }

  @Override
  public void execute(Map<String, Object> parameters) throws Exception {
    int count = (Integer) getParameter(parameters, "copyCount", true, null, Integer.class);
    String targetName = (String) getParameter(parameters, "targetName", true, null, String.class);

    if (count==1) {
      copyArtifact(targetName);
    }
    for (int i = 0; i < count; i++) {
      copyArtifact(targetName + count);
    }
  }
  
  private void copyArtifact(String targetName) {
    // Skip API usage here, since the content handling doesn't behave like other
    // artifacts
    // copy.getConnector().createNewArtifact(getArtifact().getMetadata().getPath(),
    // copy, getArtifact().loadContent(representationName));

    ((DemoConnector) getArtifact().getConnector()).copyArtifact(getArtifact(), targetName);
  }

  @Override
  public String getLabel() {
    return "Copy";
  }

}
