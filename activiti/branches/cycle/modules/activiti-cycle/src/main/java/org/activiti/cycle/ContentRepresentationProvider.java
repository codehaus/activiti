package org.activiti.cycle;

import org.activiti.cycle.impl.RepositoryRegistry;

/**
 * A {@link ContentRepresentationProvider} is responsible to create
 * {@link ContentRepresentationDefinition} objects for certain {@link RepositoryArtifact}
 * s. It is registered via the {@link RepositoryRegistry} and new providers can
 * be added on the fly.
 * 
 * @author bernd.ruecker
 */
public abstract class ContentRepresentationProvider {

  private String contentRepresentationName;
  private String contentRepresentationType;
  
  public ContentRepresentationProvider(String contentRepresentationName, String contentRepresentationType) {
    this.contentRepresentationName = contentRepresentationName;
    this.contentRepresentationType = contentRepresentationType;
  }
  
  /**
   * creates the {@link ContentRepresentationDefinition} object for the given artifact
   */
  public ContentRepresentationDefinition createContentRepresentationDefinition(RepositoryArtifact artifact) {
    ContentRepresentationDefinition contentRepresentation = new ContentRepresentationDefinition();
    contentRepresentation.setArtifact(artifact);
    contentRepresentation.setName(contentRepresentationName);
    contentRepresentation.setType(contentRepresentationType);
    return contentRepresentation;
  }
  
  public Content createContent(RepositoryArtifact artifact) {
    Content c = new Content();
    c.setContentRepresentationDefinition(createContentRepresentationDefinition(artifact));
    c.setValue(getContent(artifact));
    return c;
  }  
  
  public byte[] toBytes(String result) {
    return result.getBytes();
  }  
  
  /**
   * TODO: Introduce Provider using Streams instead of byte[]
   */
  public abstract byte[] getContent(RepositoryArtifact artifact);

  /**
   * key for name in properties for GUI
   * 
   * TODO: I18n
   */
  public String getContentRepresentationName() {
    return contentRepresentationName;
  }

  public String getContentRepresentationType() {
    return contentRepresentationType;
  }
}
