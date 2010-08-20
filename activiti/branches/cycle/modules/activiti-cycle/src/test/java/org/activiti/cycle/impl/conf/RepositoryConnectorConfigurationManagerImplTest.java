package org.activiti.cycle.impl.conf;

import java.io.File;
import java.util.List;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.impl.connector.demo.DemoConnectorConfiguration;
import org.activiti.cycle.impl.connector.fs.FileSystemConnectorConfiguration;
import org.activiti.cycle.impl.connector.signavio.SignavioConnectorConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO: Remove sysouts
 * 
 * @author christian.lipphardt
 */
public class RepositoryConnectorConfigurationManagerImplTest {

  private RepositoryConnectorConfigurationManager repoConfManager;

  @Before
  public void setUp() throws Exception {
    repoConfManager = new SimpleXstreamRepositoryConnectorConfigurationManager();
  }

  @After
  public void tearDown() throws Exception {
    repoConfManager = null;
  }

  @Test
  public void testPersistRepositoryConfiguration() {
    SignavioConnectorConfiguration sigConf = new SignavioConnectorConfiguration();
    repoConfManager.persistRepositoryConfiguration(sigConf);

    DemoConnectorConfiguration demoConf = new DemoConnectorConfiguration();
    repoConfManager.persistRepositoryConfiguration(demoConf);

    FileSystemConnectorConfiguration fileConf = new FileSystemConnectorConfiguration();
    repoConfManager.persistRepositoryConfiguration(fileConf);
  }

  @Test
  public void testCreateRepositoryConfiguration() {
    // repoConfManager.registerRepositoryConnector(DemoConnector.class);
    // repoConfManager.registerRepositoryConnector(SignavioConnector.class);
    // repoConfManager.registerRepositoryConnector(FileSystemConnector.class);

    RepositoryConnectorConfiguration config = new SignavioConnectorConfiguration("Local Signavio", "http://localhost:8080", null, "christian.lipphardt", "xxx");
    System.out.println(config);

    repoConfManager.persistRepositoryConfiguration(config);
  }

  @Test
  public void testRepoConfigUsage() {
    // register connectors
    // repoConfManager.registerRepositoryConnector(DemoConnector.class);
    // repoConfManager.registerRepositoryConnector(SignavioConnector.class);
    // repoConfManager.registerRepositoryConnector(FileSystemConnector.class);

    // create configurations
    repoConfManager.persistRepositoryConfiguration(
            new SignavioConnectorConfiguration("Activiti Modeler", "http://localhost:8080/activiti-modeler/", null, "christian.lipphardt", "xxx"));
    repoConfManager.persistRepositoryConfiguration(           
            new FileSystemConnectorConfiguration("Hard Drive", new File("c:")));

    // // create connector instances based on configs
    // List<RepositoryConnector> connectors =
    // repoConfManager.createRepositoryConnectorsFromConfigurations();
    //
    // // use connectors
    // for (RepositoryConnector repositoryConnector : connectors) {
    // List<RepositoryNode> nodes = repositoryConnector.getChildNodes("");
    // System.out.println(repositoryConnector.getClass().getName() + ": " +
    // nodes);
    // }
  }

  @Test
  public void testLoadRepoConfig() {
    // register connectors
    // config for demo connector does not exists, what to do?
    // repoConfManager.registerRepositoryConnector(DemoConnector.class);
    RepositoryConnectorConfigurationManager repoConfManager1 = new SimpleXstreamRepositoryConnectorConfigurationManager();
    // repoConfManager1.registerRepositoryConnector(SignavioConnector.class);
    // repoConfManager1.registerRepositoryConnector(FileSystemConnector.class);

    // get configs from filesystem
    List< ? extends RepositoryConnectorConfiguration> configs = repoConfManager1.findAllRepositoryConfigurations();
    for (RepositoryConnectorConfiguration config : configs) {
      System.out.println(config);
      RepositoryConnector connector = config.createConnector();
      List<RepositoryNode> nodes = connector.getChildNodes("");
      System.out.println(connector.getClass().getName() + ": " + nodes);
    }
  }
}
