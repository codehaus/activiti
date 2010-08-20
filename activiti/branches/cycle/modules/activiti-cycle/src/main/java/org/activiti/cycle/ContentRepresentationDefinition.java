package org.activiti.cycle;

import java.io.Serializable;

/**
 * Data structure for link to content, including the URL to the content, the
 * type (see {@link ContentRepresentationType}) and a name (which is shown in
 * the GUI).
 * 
 * The client URL should be normally set by the infrastructure, so a
 * {@link ContentRepresentationProvider} can concentrate on really providing the
 * content itself (as byte array). If that is an expensive operation (maybe slow
 * or big content), then this should only be done if the
 * {@link ContentRepresentationProvider} is asked to create the content as well.
 * 
 * @author bernd.ruecker@camunda.com
 */
public class ContentRepresentationDefinition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private RepositoryArtifact artifact;

	/**
   * type of content as normally indicated by {@link ContentRepresentationType}
   * (e.g. text file, image, ...). Information for the client to render it
   * correctly.
   */
  private String type;
	
  /**
   * Name of this representation, serves as a key to query the correct
   * representation and may be used by the client to show a list of possible
   * {@link ContentRepresentationDefinition}s
   */
	private String name;
	
	private String clientUrl;

  public ContentRepresentationDefinition() {
  }
  
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClientUrl() {
    return clientUrl;
  }

  public void setClientUrl(String clientUrl) {
    this.clientUrl = clientUrl;
  }

  public RepositoryArtifact getArtifact() {
    return artifact;
  }

  public void setArtifact(RepositoryArtifact artifact) {
    this.artifact = artifact;
  }

}
