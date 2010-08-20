package org.activiti.cycle.impl.connector.fs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.activiti.cycle.Content;
import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.impl.RepositoryConnectorConfiguration;

public class FileSystemConnector implements RepositoryConnector {

  private static Logger log = Logger.getLogger(FileSystemConnector.class.getName());

  static {
    // RepositoryRegistry.registerArtifactType(new ArtifactType(name,
    // typeIdentifier));
    //
    // RepositoryRegistry.registerContentRepresentationProvider(fileTypeIdentifier,
    // provider);
    //
    // RepositoryRegistry.registerArtifactAction(artifactTypeIdentifier,
    // action);
  }

  private FileSystemConnectorConfiguration conf;

  public FileSystemConnector(FileSystemConnectorConfiguration conf) {
    this.conf = conf;
  }

  public boolean login(String username, String password) {
    // login is not need to access local file system, so everything we get is
    // OK!
    return true;
  }

  public List<RepositoryNode> getChildNodes(String parentId) {
    File[] children = null;
    String path = "";

    List<RepositoryNode> childNodes = new ArrayList<RepositoryNode>();

    try {
      if (parentId == null || parentId.length() == 0) {
        // Go to root!
        path = conf.getBasePath();
        children = new File(path).listFiles();
      } else {
        // Use base path!
        path = conf.getBasePath() + parentId;
        children = new File(path).listFiles();
      }

      for (File file : children) {
        if (file.isDirectory()) {
          childNodes.add(getFolderInfo(file));
        } else if (file.isFile()) {
          childNodes.add(getArtifactInfo(file));
        }
      }
    } catch (IOException ioe) {
      throw new RepositoryException("Error while getting childNodes from path '" + path + "'", ioe);
    }

    return childNodes;
  }

  public RepositoryArtifact getArtifactDetails(String id) {
    return null;
  }

  public Content getContent(String nodeId, String representationName) {
    RepositoryArtifact artifact = getArtifactDetails(nodeId);
    return artifact.loadContent(representationName);
  }

  public void createNewFile(String folderId, RepositoryArtifact file) {
    File newFile = new File(folderId, file.getId());
    try {
      if (newFile.createNewFile()) {
        return;
      }

      throw new RepositoryException("Unable to create file " + file + " in folder " + folderId);
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to create file " + file + " in folder " + folderId);
    }
  }

  public void deleteArtifact(String artifactId) {
    File fileToDelete = new File(artifactId);
    if (fileToDelete.isFile()) {
      if (fileToDelete.exists() && fileToDelete.isAbsolute()) {
        if (fileToDelete.delete()) {
          return;
        }
      }
    }

    throw new RepositoryException("Unable to delete file " + artifactId);
  }

  public void createNewSubFolder(String parentFolderId, RepositoryFolder subFolder) {
    File newSubFolder = new File(new File(parentFolderId), subFolder.getId());
    if (!newSubFolder.mkdir()) {
      throw new RepositoryException("Unable to create subfolder " + subFolder.getId() + " in parentfolder " + parentFolderId);
    }
  }

  public void deleteSubFolder(String subFolderId) {
    File subFolderToDelete = new File(subFolderId);
    if (subFolderToDelete.isDirectory()) {
      if (subFolderToDelete.exists() && subFolderToDelete.isAbsolute()) {
        if (subFolderToDelete.delete()) {
          return;
        }
      }
    }

    throw new RepositoryException("Unable to delete folder " + subFolderId);
  }

  public void commitPendingChanges(String comment) {
    // do nothing
  }

  private RepositoryArtifact getArtifactInfo(File file) throws IOException {
    RepositoryArtifact artifact = new RepositoryArtifact(this);

    artifact.setId(getLocalPath(file.getCanonicalPath()));
    artifact.getMetadata().setName(file.getName());
    artifact.getMetadata().setPath(conf.getBasePath() + artifact.getId());
    artifact.getMetadata().setLastChanged(new Date(file.lastModified()));

    return artifact;
  }

  private RepositoryFolder getFolderInfo(File file) throws IOException {
    RepositoryFolder folder = new RepositoryFolder(this);

    folder.setId(getLocalPath(file.getCanonicalPath()));
    folder.getMetadata().setName(file.getName());
    folder.getMetadata().setPath(conf.getBasePath() + folder.getId());
    folder.getMetadata().setLastChanged(new Date(file.lastModified()));

    return folder;
  }

  public void createNewArtifact(String containingFolderId, RepositoryArtifact artifact, Content artifactContent) {
    throw new UnsupportedOperationException("FileSystemConnector does not support creating files!");
  }

  public void modifyArtifact(RepositoryArtifact artifact, ContentRepresentationDefinition artifactContent) {
    throw new UnsupportedOperationException("FileSystemConnector does not support modifying files!");
  }

  private String getLocalPath(String path) {
    if (path.startsWith(conf.getBasePath())) {
      path = path.replace(conf.getBasePath(), "");
      return path;
    }
    throw new RepositoryException("Unable to determine local path! ('" + path + "')");
  }

  public RepositoryConnectorConfiguration getRepositoryConnectorConfiguration() {
    return (RepositoryConnectorConfiguration) conf;
  }

  public void setRepositoryConnectorConfiguration(RepositoryConnectorConfiguration config) {
    this.conf = (FileSystemConnectorConfiguration) config;
  }
}
