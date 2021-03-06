package org.activiti.cycle.impl.connector.signavio.action;

import java.util.Map;

import org.activiti.cycle.Content;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryArtifactLink;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.ParameterizedHtmlFormTemplateAction;
import org.activiti.cycle.impl.db.entity.RepositoryArtifactLinkEntity;
import org.activiti.cycle.service.CycleRepositoryService;
import org.activiti.cycle.service.CycleServiceFactory;

/**
 * This action copies any artifact to another location. Extend it to specify the
 * used content representation id
 * 
 * TODO: Move to a folder for generic base classes
 * 
 * @author bernd.ruecker@camunda.com
 */
public abstract class AbstractCopyBaseAction extends ParameterizedHtmlFormTemplateAction {

  private static final long serialVersionUID = 1L;

  public static final String PARAM_TARGET_FOLDER = "targetFolderId";
  public static final String PARAM_TARGET_CONNECTOR = "targetConnectorId";
  public static final String PARAM_TARGET_NAME = "targetName";
  public static final String PARAM_COMMENT_NAME = "comment";
  public static final String CREATE_LINK_NAME = "createLink";

  public AbstractCopyBaseAction(String actionId) {
    super(actionId);
  }

  public void execute(RepositoryConnector connector, RepositoryArtifact artifact, Map<String, Object> parameters) throws Exception {
    String targetFolderId = (String) getParameter(parameters, PARAM_TARGET_FOLDER, true, null, String.class);
    String targetName = (String) getParameter(parameters, PARAM_TARGET_NAME, false, artifact.getMetadata().getName(), String.class);
    String comment = (String) getParameter(parameters, PARAM_COMMENT_NAME, false, null, String.class);
    RepositoryConnector targetConnector = (RepositoryConnector) getParameter(parameters, PARAM_TARGET_CONNECTOR, true, null, RepositoryConnector.class);

    boolean createLink = (Boolean) getParameter(parameters, CREATE_LINK_NAME, true, Boolean.TRUE, Boolean.class);

    String contentAsString = connector.getContent(artifact.getNodeId(), getContentRepresentationIdToUse()).asString();
    Content content = new Content();
    content.setValue(contentAsString);

    // no transaction required: atomic
    RepositoryArtifact targetArtifact = targetConnector.createArtifact(targetFolderId, targetName, artifact.getArtifactType().getId(), content);

    if (createLink) {
      RepositoryArtifactLink link = new RepositoryArtifactLinkEntity();
      link.setSourceArtifact(artifact);
      link.setTargetArtifact(targetArtifact);
      link.setComment(comment);
      link.setLinkType(RepositoryArtifactLinkEntity.TYPE_COPY);
      CycleRepositoryService repositoryService = CycleServiceFactory.getRepositoryService();
      repositoryService.addArtifactLink(link);
    }
  }

  public abstract String getContentRepresentationIdToUse();

  @Override
  public String getFormResourceName() {
    return getDefaultFormName();
  }

}
