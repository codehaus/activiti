package org.activiti.cycle.impl.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.cycle.Content;
import org.activiti.cycle.ContentRepresentation;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.RepositoryNodeCollection;
import org.activiti.cycle.RepositoryNodeNotFoundException;
import org.activiti.cycle.impl.RepositoryNodeCollectionImpl;
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
      return new VirtualRepositoryFolderImpl(getId(), id, null, ps, null);
    }

    VirtualRepositoryFolder virtualFolder = processSolutionService.getVirtualRepositoryFolderById(virtualFolderId);
    String relativePath = id.replace(processSolutionId + "/" + virtualFolderId, "");
    if (relativePath.length() == 0) {
      // id == processsolution/virtualFolderId
      return new VirtualRepositoryFolderImpl(getId(), id, virtualFolder, ps, null);
    }

    // id == processsolution/virtualFolderId/...
    try {
      RepositoryFolder folder = repositoryService.getRepositoryFolder(virtualFolder.getConnectorId(), virtualFolder.getReferencedNodeId() + "/" + relativePath);
      return new VirtualRepositoryFolderImpl(getId(), id, virtualFolder, ps, folder);
    } catch (Exception e) {
      RepositoryArtifact artifact = repositoryService.getRepositoryArtifact(virtualFolder.getConnectorId(), virtualFolder.getReferencedNodeId() + "/"
              + relativePath);
      return new VirtualRepositoryArtifactImpl(getId(), id, virtualFolder, ps, artifact);
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
    VirtualRepositoryFolderImpl folderImpl = (VirtualRepositoryFolderImpl) getRepositoryNode(id);
    VirtualRepositoryFolder virtualFolder = folderImpl.getVirtualRepositoryFolder();
    RepositoryFolder wrappedFolder = (RepositoryFolder) folderImpl.getWrappedNode();
    RepositoryNodeCollection childNodes = null;
    if (wrappedFolder != null) {
      // get child nodes of wrapped folder
      childNodes = repositoryService.getChildren(wrappedFolder.getConnectorId(), wrappedFolder.getNodeId());
    }
    if (virtualFolder != null) {
      // get child nodes of virtual folder
      childNodes = repositoryService.getChildren(virtualFolder.getConnectorId(), virtualFolder.getReferencedNodeId());
    }
    if (childNodes != null) {
      for (RepositoryNode childNode : childNodes.asList()) {
        String childNodeId = childNode.getNodeId();
        childNodeId = childNodeId.replace(folderImpl.getVirtualRepositoryFolder().getReferencedNodeId(), "");
        childNodeId = processSolutionId + "/" + folderImpl.getVirtualRepositoryFolder().getId() + "/" + childNodeId;
        if (childNode instanceof RepositoryArtifact) {
          resultList.add(new VirtualRepositoryArtifactImpl(getId(), childNodeId, folderImpl.getVirtualRepositoryFolder(), folderImpl.getProcessSolution(),
                  (RepositoryArtifact) childNode));
        } else {
          resultList.add(new VirtualRepositoryFolderImpl(getId(), childNodeId, folderImpl.getVirtualRepositoryFolder(), folderImpl.getProcessSolution(),
                  (RepositoryFolder) childNode));
        }
      }
    } else {
      // get children of process solution:
      for (VirtualRepositoryFolder virtualChildfolder : processSolutionService.getFoldersForProcessSolution(processSolutionId)) {
        String childNodeId = processSolutionId + "/" + virtualChildfolder.getId();
        resultList.add(new VirtualRepositoryFolderImpl(getId(), childNodeId, virtualChildfolder, folderImpl.getProcessSolution(), null));
      }
    }
    return new RepositoryNodeCollectionImpl(resultList);
  }
  public RepositoryArtifact createArtifact(String parentFolderId, String artifactName, String artifactType, Content artifactContent)
          throws RepositoryNodeNotFoundException {
    return null;
  }

  public RepositoryArtifact createArtifactFromContentRepresentation(String parentFolderId, String artifactName, String artifactType,
          String contentRepresentationName, Content artifactContent) throws RepositoryNodeNotFoundException {
    return null;
  }

  public RepositoryArtifact createEmptyArtifact(String parentFolderId, String artifactName, String artifactType) throws RepositoryNodeNotFoundException {
    return null;
  }

  public RepositoryFolder createFolder(String parentFolderId, String name) throws RepositoryNodeNotFoundException {
    return null;
  }

  public Content getContent(String artifactId) throws RepositoryNodeNotFoundException {
    return null;
  }

  public void updateContent(String artifactId, Content content) throws RepositoryNodeNotFoundException {
  }

  public void updateContent(String artifactId, String contentRepresentationName, Content content) throws RepositoryNodeNotFoundException {
  }

  public void deleteArtifact(String artifactId) throws RepositoryNodeNotFoundException {
  }

  public void deleteFolder(String folderId) throws RepositoryNodeNotFoundException {
  }

  public void executeParameterizedAction(String artifactId, String actionId, Map<String, Object> parameters) throws Exception {
  }

  public boolean isLoggedIn() {
    return false;
  }

  public ContentRepresentation getDefaultContentRepresentation(String artifactId) throws RepositoryNodeNotFoundException {
    return null;
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

}
