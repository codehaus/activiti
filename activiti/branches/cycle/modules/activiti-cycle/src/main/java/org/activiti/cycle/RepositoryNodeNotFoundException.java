package org.activiti.cycle;

/**
 * Exception to indicate a requested node was not found
 * 
 * @author ruecker
 */
public class RepositoryNodeNotFoundException extends RepositoryException {

  private static final long serialVersionUID = 1L;
  
  public static String createMessage(String repositoryName, Class< ? extends RepositoryNode> artifactType, String artifactId) {
    return artifactType.getSimpleName() + " with id '" + artifactId + "' not found in repository '" + repositoryName + "'";
  }

  public RepositoryNodeNotFoundException(String repositoryName, Class< ? extends RepositoryNode> artifactType, String artifactId) {
    super(createMessage(repositoryName, artifactType, artifactId));
  }

  public RepositoryNodeNotFoundException(String repositoryName, Class< ? extends RepositoryNode> artifactType, String artifactId, Throwable cause) {
    super(createMessage(repositoryName, artifactType, artifactId), cause);
  }
  //
  // public RepositoryNodeNotFoundException(String msg) {
  // super(msg);
  // }
  //
  // public RepositoryNodeNotFoundException(String msg, Throwable cause) {
  // super(msg, cause);
  // }

}
