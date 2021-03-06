package org.activiti.cycle.impl.connector.vfs;

import java.util.UUID;

import org.activiti.cycle.Content;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.RepositoryNodeCollection;
import org.activiti.cycle.impl.conf.ConfigurationContainer;
import org.activiti.cycle.impl.plugin.PluginFinder;
import org.activiti.cycle.incubator.connector.vfs.VfsConnector;
import org.activiti.cycle.incubator.connector.vfs.VfsConnectorConfiguration;
import org.activiti.cycle.incubator.connector.vfs.VfsConnectorPluginDefinition;
import org.junit.BeforeClass;
import org.junit.Test;

public class SftpConnectorTest {

  private static ConfigurationContainer userConfiguration;

  private static VfsConnector connector;

  @BeforeClass
  public static void createConnector() {
    userConfiguration = new ConfigurationContainer("daniel");
    userConfiguration.addRepositoryConnectorConfiguration(new VfsConnectorConfiguration("sftp", "localhost", "/home/guest/", "sftp"));
    connector = (VfsConnector) userConfiguration.getConnector("sftp");

    // TODO: Should be done in Bootstrapping
    PluginFinder.checkPluginInitialization();
    connector.login("guest", "guestlogin");
  }

  @Test
  public void testGetChildren() {
    RepositoryNodeCollection nodeCollection = connector.getChildren("");
    for (RepositoryNode node : nodeCollection.asList()) {
      System.out.println(node);
    }
  }

  @Test
  public void testCreateArtifact() {
    RepositoryArtifact artifact = connector.getRepositoryArtifact("build.xml");

    Content content = connector.getContent(artifact.getNodeId(), VfsConnectorPluginDefinition.CONTENT_REPRESENTATION_ID_XML);

    RepositoryArtifact newArtifact = connector.createArtifact("tmp/", UUID.randomUUID() + ".xml", "Text", content);

  }

  @Test
  public void testCreateFolder() {

    RepositoryFolder newFolder = connector.createFolder("tmp/", UUID.randomUUID().toString());

  }

  @Test
  public void testDeleteArtifact() {
    RepositoryArtifact artifact = connector.getRepositoryArtifact("build.xml");

    Content content = connector.getContent(artifact.getNodeId(), VfsConnectorPluginDefinition.CONTENT_REPRESENTATION_ID_XML);

    RepositoryArtifact newArtifact = connector.createArtifact("tmp/", UUID.randomUUID() + ".xml", "Text", content);

    connector.deleteArtifact(newArtifact.getNodeId());

  }

  @Test
  public void testDeleteFolder() {
    connector.deleteFolder("/tmp/test");
  }

}
