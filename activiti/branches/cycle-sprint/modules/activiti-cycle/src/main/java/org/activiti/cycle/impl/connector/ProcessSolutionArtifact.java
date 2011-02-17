package org.activiti.cycle.impl.connector;

import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryArtifactType;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;

public class ProcessSolutionArtifact extends ProcessSolutionRepositoryNode implements RepositoryArtifact {

  public ProcessSolutionArtifact(String connectorId, String nodeId, VirtualRepositoryFolder virtualFolder, ProcessSolution processSolution,
          RepositoryArtifact wrappedArtifact) {
    super(connectorId, nodeId, virtualFolder, processSolution, wrappedArtifact);
  }

  public RepositoryArtifactType getArtifactType() {
    if (wrappedNode != null) {
      return ((RepositoryArtifact) wrappedNode).getArtifactType();
    } else {
      // TODO: return processSolutionHomeArtifactType
      return null;
    }
  }
  
  public RepositoryArtifact getWrappedNode() {
    return (RepositoryArtifact) super.getWrappedNode();
  }

}
