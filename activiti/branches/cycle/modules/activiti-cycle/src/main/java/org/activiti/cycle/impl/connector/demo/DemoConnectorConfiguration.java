package org.activiti.cycle.impl.connector.demo;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.RepositoryConnectorConfiguration;

public class DemoConnectorConfiguration extends RepositoryConnectorConfiguration {

  @Override
  public RepositoryConnector createConnector() {
    return new DemoConnector();
  }

}
