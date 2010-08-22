package org.activiti.cycle.impl.connector.demo.provider;

import java.util.Map;

import org.activiti.cycle.ContentRepresentationProvider;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.impl.connector.demo.DemoConnector;

public class DemoProvider extends ContentRepresentationProvider {

  public DemoProvider(String name, String type) {
    super(name, type);
  }
  
  @Override
  public byte[] getContent(RepositoryArtifact artifact) {
    Map<String, byte[]> map = DemoConnector.content.get(artifact.getId());
    if (map != null) {
      return map.get(getContentRepresentationName());
    }
    throw new RepositoryException("Couldn't find content representation '" + getContentRepresentationName() + "' for artifact " + artifact.getId());
  }
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [" + getContentRepresentationName() + "]";
  }

}
