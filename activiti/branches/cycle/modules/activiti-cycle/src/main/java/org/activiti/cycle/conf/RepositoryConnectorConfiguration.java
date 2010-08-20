package org.activiti.cycle.conf;

/**
 * 
 * @author christian.lipphardt
 */
public abstract class RepositoryConnectorConfiguration {

  protected String basePath;

  public String getUser() {
    return null;
  }

  public void setUser(String user) {
  }

  public String getPassword() {
    return null;
  }

  public void setPassword(String password) {
  }

  public abstract String getBasePath();

  public abstract void setBasePath(String basePath);

  @Override
  public String toString() {
    return "RepositoryConnectorConfiguration '" + this.getClass().getSimpleName() + "' [getBasePath()=" + getBasePath() + ", getPassword()=" + getPassword()
            + ", getUser()=" + getUser() + "]";
  }
}
