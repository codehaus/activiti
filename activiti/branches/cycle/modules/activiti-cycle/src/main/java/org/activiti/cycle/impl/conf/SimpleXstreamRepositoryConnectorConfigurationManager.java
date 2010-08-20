package org.activiti.cycle.impl.conf;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.cycle.RepositoryException;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author christian.lipphardt
 */
public class SimpleXstreamRepositoryConnectorConfigurationManager implements RepositoryConnectorConfigurationManager {

  // TODO: Set a config dir for xstream?
  private static final String CONFIG_DIR = "";
  private static final String FILE_EXT = ".xml";

  // private List<Class< ? extends RepositoryConnector>>
  // registeredRepositoryConnnectors = new ArrayList<Class< ? extends
  // RepositoryConnector>>();
  // private List<RepositoryConnectorConfiguration> repoConnectorConfigurations
  // = findAllRepositoryConfigurations();
  private List<RepositoryConnectorConfiguration> repoConnectorConfigurations = new ArrayList<RepositoryConnectorConfiguration>();

  public SimpleXstreamRepositoryConnectorConfigurationManager() {
  }

  // public RepositoryConnectorConfiguration
  // createRepositoryConfiguration(Class< ? extends RepositoryConnector>
  // repositoryConnector, String user,
  // String password, String basePath) {
  // try {
  // if (registeredRepositoryConnnectors.contains(repositoryConnector)) {
  // RepositoryConnectorConfiguration config = null;
  // // FIXME: Better way for instance creation
  // config = (RepositoryConnectorConfiguration)
  // Class.forName(repositoryConnector.getCanonicalName() +
  // "Configuration").newInstance();
  // config.setUser(user);
  // config.setPassword(password);
  // config.setSignavioUrl(basePath);
  // repoConnectorConfigurations.add(config);
  // return config;
  // }
  // throw new RepositoryException("RepositoryConnector '" +
  // repositoryConnector.getClass().getName() + "' is not registered!");
  // } catch (Exception e) {
  // throw new RepositoryException("Unable to create configuration for " +
  // repositoryConnector.getClass().getName(), e);
  // }
  // }
  //
  // public void registerRepositoryConnector(Class< ? extends
  // RepositoryConnector> repositoryConnector) {
  // if (repositoryConnector != null) {
  // registeredRepositoryConnnectors.add(repositoryConnector);
  // }
  // }
  //
  // public List<Class< ? extends RepositoryConnector>>
  // getRegisteredRepositoryConnectors() {
  // return registeredRepositoryConnnectors;
  // }

  public void persistRepositoryConfiguration(RepositoryConnectorConfiguration config) {
    String configFileName = config.getName() + FILE_EXT;
    try {
      XStream xStream = new XStream();
      xStream.toXML(config, new FileWriter(configFileName));
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to persist RepositoryConnectorConfiguration '" + configFileName + "'", ioe);
    }
  }

  public List<RepositoryConnectorConfiguration> findAllRepositoryConfigurations() {
    // TODO: Implement retrieving all files
    return new ArrayList<RepositoryConnectorConfiguration>();
  }

  public RepositoryConnectorConfiguration getRepositoryConfiguration(String name) {
    String configFileName = name + FILE_EXT;
    try {
      XStream xStream = new XStream();
      return (RepositoryConnectorConfiguration) xStream.fromXML(new FileReader(configFileName));
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to load RepositoryConnectorConfiguration '" + configFileName + "'", ioe);
    }
  }

  public void removeRepositoryConfiguration(String id) {
    // TODO: delete from FS
  }

  // public void persistAllRepositoryConfigurations() {
  // for (RepositoryConnectorConfiguration config : repoConnectorConfigurations)
  // {
  // persistRepositoryConfiguration(config);
  // }
  // }

  // public RepositoryConnector
  // createRepositoryConnectorFromConfiguration(RepositoryConnectorConfiguration
  // repositoryConfig) {
  // try {
  // Class connectorClass =
  // Class.forName(repositoryConfig.getClass().getName().replace("Configuration",
  // ""));
  // return (RepositoryConnector)
  // connectorClass.getConstructor(repositoryConfig.getClass()).newInstance(repositoryConfig);
  // } catch (Exception e) {
  // throw new RepositoryException("Unable to create repository connector!", e);
  // }
  // }
  //
  // public List<RepositoryConnector>
  // createRepositoryConnectorsFromConfigurations() {
  // List<RepositoryConnector> connectors = new
  // ArrayList<RepositoryConnector>();
  //
  // for (RepositoryConnectorConfiguration config : repoConnectorConfigurations)
  // {
  // connectors.add(createRepositoryConnectorFromConfiguration(config));
  // }
  //
  // return connectors;
  // }
  //
  // public List<RepositoryConnectorConfiguration>
  // findAllRepositoryConfigurations() {
  // List<RepositoryConnectorConfiguration> configs = new
  // ArrayList<RepositoryConnectorConfiguration>();
  //
  // for (Class< ? extends RepositoryConnector> connector :
  // registeredRepositoryConnnectors) {
  // String clazzName = connector.getName() + "Configuration";
  // try {
  // RepositoryConnectorConfiguration config =
  // (RepositoryConnectorConfiguration) Class.forName(clazzName).newInstance();
  // configs.add(findRepositoryConfiguration(config.getClass()));
  // } catch (Exception e) {
  // throw new RepositoryException("Unable to find class '" + clazzName + "'",
  // e);
  // }
  // }
  //
  // return configs;
  // }
  //
  // public RepositoryConnectorConfiguration findRepositoryConfiguration(Class<
  // ? extends RepositoryConnectorConfiguration> config) {
  // String configFileName = config.getSimpleName() + FILE_EXT;
  // try {
  // XStream xStream = new XStream();
  // RepositoryConnectorConfiguration loadedConfig =
  // (RepositoryConnectorConfiguration) xStream.fromXML(new
  // FileReader(configFileName));
  // return loadedConfig;
  // } catch (FileNotFoundException fnfe) {
  // throw new
  // RepositoryException("Unable to find RepositoryConnectorConfiguration '" +
  // configFileName + "'", fnfe);
  // }
  // }

}
