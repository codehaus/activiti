package org.activiti.cycle.impl.conf;

import java.util.Properties;

import org.activiti.cycle.RepositoryConnector;

/**
 * 
 * @author christian.lipphardt
 */
public abstract class RepositoryConnectorConfiguration {

  /**
   * name (unique!), used for URL and identification
   * 
   * TODO: Use this default? Hmm....
   */
  private String name = this.getClass().getSimpleName().replace("Configuration", "");

  private String description;
  
  public abstract RepositoryConnector createConnector();

  public Properties getProperties() {
    // TODO: get possiblte attributes per relfection
    return new Properties();
  }

  public void setProperties(Properties properties) {
    // TODO: Change properties via reflection
  }
  
  public String getName() {
    return name;
  }

  
  public void setName(String name) {
    this.name = name;
  }

  
  public String getDescription() {
    return description;
  }

  
  public void setDescription(String description) {
    this.description = description;
  }

  //
  // protected String basePath;
  //
  // public String getUser() {
  // return null;
  // }
  //
  // public void setUser(String user) {
  // }
  //
  // public String getPassword() {
  // return null;
  // }
  //
  // public void setPassword(String password) {
  // }
  //
  // public abstract String getBasePath();
  //
  // public abstract void setBasePath(String basePath);
  //
  // @Override
  // public String toString() {
  // return "RepositoryConnectorConfiguration '" +
  // this.getClass().getSimpleName() + "' [getBasePath()=" + getBasePath() +
  // ", getPassword()=" + getPassword()
  // + ", getUser()=" + getUser() + "]";
  // }
}
