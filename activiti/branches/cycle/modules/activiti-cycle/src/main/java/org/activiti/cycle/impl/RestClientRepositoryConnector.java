package org.activiti.cycle.impl;

import java.util.List;

import org.activiti.cycle.Content;
import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;

/**
 * Wrapper for {@link RepositoryConnector} to set client url in objects
 * correctly for REST API
 * 
 * TODO: REWRITE!
 * 
 * @author bernd.ruecker@camunda.com
 */
public class RestClientRepositoryConnector implements RepositoryConnector {

  private final String repositoryName;

  private final String baseUrl;

  private final RepositoryConnector connector;

  public RestClientRepositoryConnector(String repositoryName, String baseUrl, RepositoryConnector connector) {
    this.repositoryName = repositoryName;
    this.baseUrl = baseUrl;
    this.connector = connector;
  }

  // private RepositoryNode adjustClientUrl(RepositoryNode repositoryNode) {
  // repositoryNode.setClientUrl(baseUrl + repositoryName + "/" +
  // repositoryNode.getId());
  //
  // if (repositoryNode instanceof RepositoryArtifact) {
  // Collection<ContentRepresentationDefinition> contentRepresentations =
  // ((RepositoryArtifact)
  // repositoryNode).getContentRepresentationDefinitions();
  // for (ContentRepresentationDefinition contentRepresentation :
  // contentRepresentations) {
  // adjustClientUrl(contentRepresentation);
  // }
  // }
  //
  // return repositoryNode;
  // }
  //
  // private ContentRepresentationDefinition
  // adjustClientUrl(ContentRepresentationDefinition content) {
  // content.setClientUrl(baseUrl + repositoryName + "/" +
  // content.getContentRepresentationDefinition().getArtifact().getId() +
  // "/content/"
  // + content.getContentRepresentationDefinition().getName());
  // return content;
  // }
  //
  // private ContentRepresentationDefinition adjustClientUrl(Content content) {
  // content.setClientUrl(baseUrl + repositoryName + "/" +
  // content.getContentRepresentationDefinition().getArtifact().getId() +
  // "/content/"
  // + content.getContentRepresentationDefinition().getName());
  // return content;
  // }

  public void createNewArtifact(String containingFolderId, RepositoryArtifact artifact, Content artifactContent) {
    connector.createNewArtifact(containingFolderId, artifact, artifactContent);
  }

  public void modifyArtifact(RepositoryArtifact artifact, ContentRepresentationDefinition artifactContent) {
  }

  public void createNewSubFolder(String parentFolderUrl, RepositoryFolder subFolder) {
    connector.createNewSubFolder(parentFolderUrl, subFolder);
  }

  public void deleteArtifact(String artifactUrl) {
    connector.deleteArtifact(artifactUrl);
  }

  public void deleteSubFolder(String subFolderUrl) {
    connector.deleteSubFolder(subFolderUrl);
  }

  public List<RepositoryNode> getChildNodes(String parentUrl) {
    List<RepositoryNode> childNodes = connector.getChildNodes(parentUrl);
    for (RepositoryNode repositoryNode : childNodes) {
      // adjustClientUrl(repositoryNode);
    }
    return childNodes;
  }

  public RepositoryArtifact getArtifactDetails(String id) {
    // return (RepositoryArtifact)
    // adjustClientUrl(connector.getArtifactDetails(id));
    return (RepositoryArtifact) connector.getArtifactDetails(id);
  }

  public boolean login(String username, String password) {
    return connector.login(username, password);
  }

  public Content getContent(String nodeId, String representationName) {
    // return adjustClientUrl(connector.getContent(nodeId, representationName));
    return connector.getContent(nodeId, representationName);
  }

  public void commitPendingChanges(String comment) {
    connector.commitPendingChanges(comment);
  }
}
