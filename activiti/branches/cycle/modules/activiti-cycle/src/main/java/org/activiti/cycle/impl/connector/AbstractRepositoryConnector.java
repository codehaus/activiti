package org.activiti.cycle.impl.connector;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.conf.RepositoryConnectorConfiguration;


public abstract class AbstractRepositoryConnector<T extends RepositoryConnectorConfiguration> implements RepositoryConnector {

  private T configuration;

  public AbstractRepositoryConnector(T configuration) {
    this.configuration = configuration;
  }

  public T getConfiguration() {
    return configuration;
  }

}
