package org.activiti.cycle.impl.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.cycle.Content;
import org.activiti.cycle.ContentRepresentation;
import org.activiti.cycle.CycleComponentFactory;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.RepositoryNodeCollection;
import org.activiti.cycle.RepositoryNodeNotFoundException;
import org.activiti.cycle.impl.RepositoryNodeCollectionImpl;
import org.activiti.cycle.impl.components.RuntimeConnectorList;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;
import org.activiti.cycle.service.CycleProcessSolutionService;
import org.activiti.cycle.service.CycleRepositoryService;
import org.activiti.cycle.service.CycleServiceFactory;

/**
 * Virtual {@link RepositoryConnector} for {@link ProcessSolution}s
 * 
 * @author daniel.meyer@camunda.com
 */
public class ProcessSolutionConnector implements RepositoryConnector {

  protected CycleProcessSolutionService processSolutionService = CycleServiceFactory.getProcessSolutionService();

  protected CycleRepositoryService repositoryService = CycleServiceFactory.getRepositoryService();

  protected final String processSolutionId;

  public ProcessSolutionConnector(String id) {
    processSolutionId = id;
  }

  public boolean login(String username, String password) {
    // does not require login
    return true;
  }

  public static String getProcessSolutionId(String nodeId) {
    if (nodeId == null) {
      return null;
    }
    if (!nodeId.contains("/")) {
      return nodeId;
    }
    return nodeId.substring(0, nodeId.indexOf("/"));
  }

  public static String getVirtualFolderId(String nodeId) {
    if (nodeId == null) {
      return null;
    }
    String[] parts = nodeId.split("/");
    if (parts.length >= 2) {
      return parts[1];
    }
    return null;
  }

  public RepositoryNode getRepositoryNode(String id) throws RepositoryNodeNotFoundException {
    if ("".equals(id) || id == null) {
      throw new RepositoryNodeNotFoundException(id);
    }

    String processSolutionId = getProcessSolutionId(id);
    if ("/".equals(id)) {
      processSolutionId = this.processSolutionId;
    }

    if (processSolutionId == null) {
      throw new RepositoryNodeNotFoundException(id);
    }
    if (processSolutionId.equals(this.processSolutionId) == false) {
      throw new RepositoryNodeNotFoundException(id);
    }
    // id=id of a processSolution
    ProcessSolution ps = processSolutionService.getProcessSolutionById(processSolutionId);

    String virtualFolderId = getVirtualFolderId(id);
    if (virtualFolderId == null) {
      return new ProcessSolutionFolder(getId(), id, null, ps, null);
    }

    VirtualRepositoryFolder virtualFolder = processSolutionService.getVirtualRepositoryFolderById(virtualFolderId);
    String relativePath = id.replace(processSolutionId + "/" + virtualFolderId, "");
    if (relativePath.length() == 0) {
      // id == processsolution/virtualFolderId
      return new ProcessSolutionFolder(getId(), id, virtualFolder, ps, null);
    }
    relativePath = id.replace(processSolutionId + "/" + virtualFolderId + "/", "");
    // id == processsolution/virtualFolderId/...
    RepositoryConnector connector = CycleComponentFactory.getCycleComponentInstance(RuntimeConnectorList.class, RuntimeConnectorList.class).getConnectorById(
            virtualFolder.getConnectorId());

    try {
      RepositoryFolder folder = connector.getRepositoryFolder(connector.concatenateNodeId(virtualFolder.getReferencedNodeId(), relativePath));
      return new ProcessSolutionFolder(getId(), id, virtualFolder, ps, folder);
    } catch (Exception e) {
      RepositoryArtifact artifact = connector.getRepositoryArtifact(connector.concatenateNodeId(virtualFolder.getReferencedNodeId(), relativePath));
      return new ProcessSolutionArtifact(getId(), id, virtualFolder, ps, artifact);
    }
  }
  public RepositoryArtifact getRepositoryArtifact(String id) throws RepositoryNodeNotFoundException {
    return (RepositoryArtifact) getRepositoryNode(id);
  }

  public Content getRepositoryArtifactPreview(String artifactId) throws RepositoryNodeNotFoundException {
    return null;
  }

  public RepositoryFolder getRepositoryFolder(String id) throws RepositoryNodeNotFoundException {
    return (RepositoryFolder) getRepositoryNode(id);
  }

  public RepositoryNodeCollection getChildren(String id) throws RepositoryNodeNotFoundException {
    List<RepositoryNode> resultList = new ArrayList<RepositoryNode>();
    ProcessSolutionFolder processSolutionFolder = (ProcessSolutionFolder) getRepositoryNode(id);
    VirtualRepositoryFolder virtualFolder = processSolutionFolder.getVirtualRepositoryFolder();
    RepositoryFolder wrappedFolder = (RepositoryFolder) processSolutionFolder.getWrappedNode();
    RepositoryNodeCollection childNodes = null;
    if (wrappedFolder != null) {
      // get child nodes of wrapped folder
      childNodes = repositoryService.getChildren(wrappedFolder.getConnectorId(), wrappedFolder.getNodeId());
    } else if (virtualFolder != null) {
      // get child nodes of virtual folder
      childNodes = repositoryService.getChildren(virtualFolder.getConnectorId(), virtualFolder.getReferencedNodeId());
    }
    if (childNodes != null) {
      for (RepositoryNode childNode : childNodes.asList()) {
        String childNodeId = childNode.getNodeId();
        childNodeId = childNodeId.replace(processSolutionFolder.getVirtualRepositoryFolder().getReferencedNodeId(), "");
        childNodeId = processSolutionId + "/" + processSolutionFolder.getVirtualRepositoryFolder().getId() + "/" + childNodeId;
        if (childNode instanceof RepositoryArtifact) {
          resultList.add(new ProcessSolutionArtifact(getId(), childNodeId, processSolutionFolder.getVirtualRepositoryFolder(), processSolutionFolder
                  .getProcessSolution(), (RepositoryArtifact) childNode));
        } else {
          resultList.add(new ProcessSolutionFolder(getId(), childNodeId, processSolutionFolder.getVirtualRepositoryFolder(), processSolutionFolder
                  .getProcessSolution(), (RepositoryFolder) childNode));
        }
      }
    } else {
      // get children of process solution:
      for (VirtualRepositoryFolder virtualChildfolder : processSolutionService.getFoldersForProcessSolution(processSolutionId)) {
        String childNodeId = processSolutionId + "/" + virtualChildfolder.getId();
        resultList.add(new ProcessSolutionFolder(getId(), childNodeId, virtualChildfolder, processSolutionFolder.getProcessSolution(), null));
      }
    }
    return new RepositoryNodeCollectionImpl(resultList);
  }

  public RepositoryArtifact createArtifact(String parentFolderId, String artifactName, String artifactType, Content artifactContent)
          throws RepositoryNodeNotFoundException {
    ProcessSolutionFolder folder = (ProcessSolutionFolder) getRepositoryNode(parentFolderId);
    if (folder.getVirtualRepositoryFolder() == null) {
      throw new RepositoryException("Cannot create artifact in the top-level folder. ");
    }
    RepositoryArtifact newArtifact = null;
    if (folder.getWrappedNode() == null) {
      newArtifact = repositoryService.createArtifact(folder.getVirtualRepositoryFolder().getConnectorId(), folder.getVirtualRepositoryFolder()
              .getReferencedNodeId(), artifactName, artifactType, artifactContent);
    } else {
      newArtifact = repositoryService.createArtifact(folder.getWrappedNode().getConnectorId(), folder.getWrappedNode().getNodeId(), artifactName, artifactType,
              artifactContent);
    }
    String relativePath = newArtifact.getNodeId().replace(folder.getVirtualRepositoryFolder().getReferencedNodeId(), "");
    String virtualPath = folder.getNodeId() + "/" + relativePath;
    return new ProcessSolutionArtifact(getId(), virtualPath, folder.getVirtualRepositoryFolder(), folder.getProcessSolution(), newArtifact);
  }

  public RepositoryArtifact createArtifactFromContentRepresentation(String parentFolderId, String artifactName, String artifactType,
          String contentRepresentationName, Content artifactContent) throws RepositoryNodeNotFoundException {
    return createArtifact(parentFolderId, artifactName, artifactType, artifactContent);
  }

  public RepositoryArtifact createEmptyArtifact(String parentFolderId, String artifactName, String artifactType) throws RepositoryNodeNotFoundException {
    throw new RuntimeException("Not implemented");
  }

  public RepositoryFolder createFolder(String parentFolderId, String name) throws RepositoryNodeNotFoundException {
    ProcessSolutionFolder folder = (ProcessSolutionFolder) getRepositoryNode(parentFolderId);
    if (folder.getVirtualRepositoryFolder() == null) {
      throw new RepositoryException("Cannot create artifact in the top-level folder. ");
    }
    if (folder.getWrappedNode() == null) {
      return repositoryService.createFolder(folder.getVirtualRepositoryFolder().getConnectorId(), folder.getVirtualRepositoryFolder().getReferencedNodeId(),
              name);
    }
    return repositoryService.createFolder(folder.getWrappedNode().getConnectorId(), folder.getWrappedNode().getNodeId(), name);
  }

  public Content getContent(String artifactId) throws RepositoryNodeNotFoundException {
    ProcessSolutionArtifact artifact = (ProcessSolutionArtifact) getRepositoryNode(artifactId);
    return repositoryService.getContent(artifact.wrappedNode.getConnectorId(), artifact.wrappedNode.getNodeId());
  }

  public void updateContent(String artifactId, Content content) throws RepositoryNodeNotFoundException {
    ProcessSolutionArtifact artifact = (ProcessSolutionArtifact) getRepositoryNode(artifactId);
    repositoryService.updateContent(artifact.wrappedNode.getConnectorId(), artifact.wrappedNode.getNodeId(), content);
  }

  public void updateContent(String artifactId, String contentRepresentationName, Content content) throws RepositoryNodeNotFoundException {
    updateContent(artifactId, content);
  }

  public void deleteArtifact(String artifactId) throws RepositoryNodeNotFoundException {
    ProcessSolutionArtifact artifact = (ProcessSolutionArtifact) getRepositoryNode(artifactId);
    repositoryService.deleteArtifact(artifact.wrappedNode.getConnectorId(), artifact.wrappedNode.getNodeId());
  }

  public void deleteFolder(String folderId) throws RepositoryNodeNotFoundException {
    ProcessSolutionFolder folder = (ProcessSolutionFolder) getRepositoryNode(folderId);
    repositoryService.deleteFolder(folder.wrappedNode.getConnectorId(), folder.wrappedNode.getNodeId());
  }

  public void executeParameterizedAction(String artifactId, String actionId, Map<String, Object> parameters) throws Exception {
    ProcessSolutionArtifact artifact = (ProcessSolutionArtifact) getRepositoryNode(artifactId);
    repositoryService.executeParameterizedAction(artifact.wrappedNode.getConnectorId(), artifact.wrappedNode.getNodeId(), actionId, parameters);
  }

  public boolean isLoggedIn() {
    return false;
  }

  public ContentRepresentation getDefaultContentRepresentation(String artifactId) throws RepositoryNodeNotFoundException {
    ProcessSolutionArtifact artifact = (ProcessSolutionArtifact) getRepositoryNode(artifactId);
    RuntimeConnectorList connectorList = CycleComponentFactory.getCycleComponentInstance(RuntimeConnectorList.class, RuntimeConnectorList.class);
    RepositoryConnector connector = connectorList.getConnectorById(artifact.connectorId);
    return connector.getDefaultContentRepresentation(artifact.wrappedNode.getNodeId());
  }

  public void startConfiguration() {
  }

  public void addConfiguration(Map<String, Object> configurationValues) {
  }

  public void addConfigurationEntry(String key, Object value) {
  }

  public void configurationFinished() {
  }

  public String[] getConfigurationKeys() {
    return null;
  }

  public void setId(String connectorId) {
  }

  public String getId() {
    return "ps-" + processSolutionId;
  }

  public String getName() {
    try {
      return processSolutionService.getProcessSolutionById(processSolutionId).getLabel();
    } catch (Exception e) {
      return "Deleted processSolution - " + processSolutionId;
    }
  }

  public void setName(String name) {
  }

  public String concatenateNodeId(String prefix, String suffix) {
    return null;
  }

}
