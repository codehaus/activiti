package org.activiti.cycle.impl.connector;

import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.RepositoryNodeMetadata;
import org.activiti.cycle.impl.RepositoryNodeMetadataImpl;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;

/**
 * A {@link RepositoryFolder}-implementation for virtual folders
 * 
 * @author Daniel Meyer
 */
public class VirtualRepositoryNodeImpl implements RepositoryNode {

  protected VirtualRepositoryFolder virtualRepositoryFolder;

  protected ProcessSolution processSolution;

  protected RepositoryNode wrappedNode;

  protected String connectorId;

  protected String nodeId;

  public VirtualRepositoryNodeImpl(String connectorId, String nodeId, VirtualRepositoryFolder virtualFolder, ProcessSolution processSolution,
          RepositoryNode wrappedNode) {
    this.virtualRepositoryFolder = virtualFolder;
    this.connectorId = connectorId;
    this.nodeId = nodeId;
    this.processSolution = processSolution;
    this.wrappedNode = wrappedNode;
  }

  public String getConnectorId() {
    return connectorId;
  }

  public String getNodeId() {
    return nodeId;
  }

  public String getGlobalUniqueId() {
    return nodeId;
  }

  public RepositoryNodeMetadata getMetadata() {
    return new RepositoryNodeMetadataImpl() {

      public String getName() {
        if (wrappedNode != null) {
          return wrappedNode.getMetadata().getName();
        } else if (virtualRepositoryFolder != null) {
          return virtualRepositoryFolder.getLabel();
        }
        return processSolution.getLabel();
      }
    };
  }

  public VirtualRepositoryFolder getVirtualRepositoryFolder() {
    return virtualRepositoryFolder;
  }

  public void setVirtualRepositoryFolder(VirtualRepositoryFolder virtualRepositoryFolder) {
    this.virtualRepositoryFolder = virtualRepositoryFolder;
  }

  public ProcessSolution getProcessSolution() {
    return processSolution;
  }

  public void setProcessSolution(ProcessSolution processSolution) {
    this.processSolution = processSolution;
  }

  public RepositoryNode getWrappedNode() {
    return wrappedNode;
  }

  public void setWrappedNode(RepositoryNode wrappedNode) {
    this.wrappedNode = wrappedNode;
  }

  public void setConnectorId(String connectorId) {
    this.connectorId = connectorId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

}
