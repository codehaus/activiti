package org.activiti.cycle.impl.connector.signavio.action;

import java.util.List;
import java.util.Map;

import org.activiti.cycle.ContentRepresentation;
import org.activiti.cycle.ContentRepresentationType;
import org.activiti.cycle.ParametrizedFreemakerTemplateAction;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.impl.RepositoryRegistry;
import org.activiti.cycle.impl.connector.signavio.SignavioConnector;
import org.activiti.cycle.impl.connector.signavio.provider.Bpmn20Provider;
import org.activiti.cycle.impl.connector.signavio.provider.JsonProvider;

/**
 * This action creates a technical BPMN 2.0 XML for the process engines. It
 * copies the XML from Signavio to a given {@link RepositoryFolder}.
 * 
 * By doing that, registered plugins / transformations are executed. The link
 * between the two {@link RepositoryArtifact}s is remembered (TODO).
 * 
 * @author bernd.ruecker@camunda.com
 */
public class CreateTechnicalBpmnXmlAction extends ParametrizedFreemakerTemplateAction {

  public static final String PARAM_TARGET_FOLDER = "targetFolder";

  /**
   * Where do we get the transformations from? How are they registered?
   * 
   * How can we extend that project specific?
   */
  private List<Object> registeredTransformations;

  @Override
  public String getFreemarkerTemplateUrl() {
    return "todo.ftl";
  }  

  @Override
  public void execute(Map<String, Object> parameter) throws Exception {
    RepositoryFolder targetFolder = (RepositoryFolder) parameter.get(PARAM_TARGET_FOLDER);
    
    String sourceJson = getBpmn20Json();
    String transformedJson = applyJsonTranfsormations(sourceJson);
    String bpmnXml = transformToBpmn20(transformedJson);
    createTargetArtifact(targetFolder, getProcessName() + ".bpmn.xml", bpmnXml, SignavioConnector.BPMN_2_0_XML);    
  }

  protected String getBpmn20Json() {
    return getArtifact().getContentRepresentation(JsonProvider.NAME).getContentAsString();
  }

  private String applyJsonTranfsormations(String sourceJson) {
    // TODO: How to register JSON-Transformations
    return sourceJson;
  }

  private String transformToBpmn20(String transformedJson) {
    // TODO: How to do that?
    // getSignavioConnector();
    
    // for the moment just use the untransformed version
    return getArtifact().getContentRepresentation(Bpmn20Provider.NAME).getContentAsString();
  }

  public void createTargetArtifact(RepositoryFolder targetFolder, String artifactId, String bpmnXml, String artifactTypeIdentifier) {
    RepositoryArtifact targetArtifact = new RepositoryArtifact(targetFolder.getConnector());
    // TODO: Check naming policy
    targetArtifact.setId(artifactId);
    // TODO: Check artifac types / registry
    targetArtifact.setArtifactType(RepositoryRegistry.getArtifactTypeByIdentifier(artifactTypeIdentifier));

    ContentRepresentation contentRepresentation = new ContentRepresentation();
    contentRepresentation.setArtifact(targetArtifact);
    contentRepresentation.setContent(bpmnXml);
    contentRepresentation.setType(ContentRepresentationType.XML);

    targetFolder.getConnector().createNewArtifact(targetFolder.getId(), targetArtifact, contentRepresentation);
  }
  
  public String getProcessName() {
    return getArtifact().getMetadata().getName();
  }

  public SignavioConnector getSignavioConnector() {
    return (SignavioConnector) getArtifact().getConnector();
  }
  
  @Override
  public String getLabel() {
    return "create technical model";
  }

}
