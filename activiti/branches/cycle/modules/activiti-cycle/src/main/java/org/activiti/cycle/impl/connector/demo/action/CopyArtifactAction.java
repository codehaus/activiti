package org.activiti.cycle.impl.connector.demo.action;

import java.util.Map;

import org.activiti.cycle.ParametrizedFreemakerTemplateAction;
import org.activiti.cycle.RepositoryArtifact;
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
      copyArtifact(targetName + i);
    }
  }
  
  private void copyArtifact(String targetName) {    
    String path = getArtifact().getId().substring(0, getArtifact().getId().lastIndexOf("/"));
    RepositoryArtifact copy = DemoConnector.clone(getArtifact());
    if (targetName.endsWith("/")) {
      copy.setId(path + targetName);
    } else {
      copy.setId(path + "/" + targetName);
    }
    copy.getMetadata().setName(targetName);
    copy.overwriteConnector(getOriginalArtifactConnector());
    
    String representatioName = getArtifact().getContentRepresentationProviders().iterator().next().getContentRepresentationName();
    getArtifact().getConnector().createNewArtifact(path, copy, getArtifact().getConnector().getContent(getArtifact().getId(), representatioName));
  }

  @Override
  public String getLabel() {
    return "Copy";
  }

}
