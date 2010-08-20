package org.activiti.cycle.impl.connector.demo;

import org.activiti.cycle.conf.RepositoryConnectorConfiguration;

public class DemoConnectorConfiguration extends RepositoryConnectorConfiguration {

  public DemoConnectorConfiguration() {
    basePath = "/";
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    if (basePath != null && basePath.length() > 0) {
      this.basePath = basePath;
    }
  }

}
