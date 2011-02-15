package org.activiti.cycle.impl.db.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.cycle.RepositoryNodeMetadata;
import org.activiti.cycle.impl.RepositoryNodeMetadataImpl;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;
import org.activiti.engine.impl.db.PersistentObject;

/**
 * Represents a {@link VirtualRepositoryFolder}.
 * 
 * @author daniel.meyer@camunda.com
 */
public class VirtualRepositoryFolderEntity implements VirtualRepositoryFolder, Serializable, PersistentObject {

  private static final long serialVersionUID = 1L;

  private String id;

  private String label;

  private String connectorId;

  private String referencedNodeId;

  private String processSolutionId;

  private String type;

  public String getGlobalUniqueId() {
    return processSolutionId + ":" + id;
  }

  public RepositoryNodeMetadata getMetadata() {
    return new RepositoryNodeMetadataImpl() {

      public String getName() {
        return label;
      }
    };
  }

  public String getProcessSolutionId() {
    return processSolutionId;
  }

  public String getType() {
    return type;
  }

  public String getConnectorId() {
    return connectorId;
  }

  public String getNodeId() {
    return id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setConnectorId(String connectorId) {
    this.connectorId = connectorId;
  }

  public void setProcessSolutionId(String processSolutionId) {
    this.processSolutionId = processSolutionId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getReferencedNodeId() {
    return referencedNodeId;
  }

  public void setReferencedNodeId(String referencedNodeId) {
    this.referencedNodeId = referencedNodeId;
  }

  public Object getPersistentState() {
    Map<String, Object> persistentState = new HashMap<String, Object>();
    persistentState.put("id", id);
    persistentState.put("label", label);
    persistentState.put("connectorId", connectorId);
    persistentState.put("processSolutionId", processSolutionId);
    persistentState.put("type", type);
    persistentState.put("referencedNodeId", referencedNodeId);
    return persistentState;
  }

}
