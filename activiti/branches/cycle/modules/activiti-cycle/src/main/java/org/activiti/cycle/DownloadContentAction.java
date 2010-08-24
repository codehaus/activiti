package org.activiti.cycle;

/**
 * Base class for download content fromthe webapp
 * 
 * @author ruecker
 */
public abstract class DownloadContentAction extends ArtifactAction {

  private final ContentRepresentationDefinition definiton;

  /**
   * TODO: Think about best way to hand in
   * {@link ContentRepresentationDefinition} link (maybe just the name?)
   */
  public DownloadContentAction(RepositoryArtifact artifact, ContentRepresentationDefinition definiton) {
    super(artifact);
    this.definiton = definiton;
  }

  
  public ContentRepresentationDefinition getDefiniton() {
    return definiton;
  }

  
}
