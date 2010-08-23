package org.activiti.cycle.impl.connector.fs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.cycle.Content;
import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.impl.connector.AbstractRepositoryConnector;

public class FileSystemConnector extends AbstractRepositoryConnector<FileSystemConnectorConfiguration> {

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

  public FileSystemConnector(FileSystemConnectorConfiguration conf) {
    super(conf);
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
        path = getConfiguration().getBasePath();
        children = new File(path).listFiles();
      } else {
        // Use base path!
        children = getFileFromId(parentId).listFiles();
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
    try {
      return getArtifactInfo(getFileFromId(id));
    } catch (IOException ex) {
      throw new RepositoryException("Error while getting artifact details for artifact id '" + id + "'", ex);
    }
  }

  private File getFileFromId(String id) {
    return new File(getConfiguration().getBasePath() + id);
  }

  public Content getContent(String nodeId, String representationName) {
    RepositoryArtifact artifact = getArtifactDetails(nodeId);
    return artifact.loadContent(representationName);
  }

  public void deleteArtifact(String artifactId) {
    File fileToDelete = getFileFromId(artifactId);
    if (deleteFile(fileToDelete)) {
      return;
    }

    throw new RepositoryException("Unable to delete file " + fileToDelete);
  }

  public void createNewSubFolder(String parentFolderId, RepositoryFolder subFolder) {
    File newSubFolder = new File(getFileFromId(parentFolderId), subFolder.getId());
    if (!newSubFolder.mkdir()) {
      throw new RepositoryException("Unable to create subfolder " + subFolder.getId() + " in parentfolder " + parentFolderId);
    }
  }

  public void deleteSubFolder(String subFolderId) {
    File subFolderToDelete = getFileFromId(subFolderId);
    if (deleteFile(subFolderToDelete)) {
      return;
    }

    throw new RepositoryException("Unable to delete folder " + subFolderToDelete);
  }

  public void commitPendingChanges(String comment) {
    // do nothing
  }

  // delete file or directory, even non-empty ones.
  private boolean deleteFile(File path) {
    if (path.exists() && path.isAbsolute()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteFile(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return path.delete();
  }

  private RepositoryArtifact getArtifactInfo(File file) throws IOException {
    RepositoryArtifact artifact = new RepositoryArtifact(this);

    artifact.setId(getLocalPath(file.getCanonicalPath()));
    artifact.getMetadata().setName(file.getName());
    artifact.getMetadata().setPath(getConfiguration().getBasePath() + artifact.getId());
    artifact.getMetadata().setLastChanged(new Date(file.lastModified()));

    return artifact;
  }

  private RepositoryFolder getFolderInfo(File file) throws IOException {
    RepositoryFolder folder = new RepositoryFolder(this);

    folder.setId(getLocalPath(file.getCanonicalPath()));
    folder.getMetadata().setName(file.getName());
    folder.getMetadata().setPath(getConfiguration().getBasePath() + folder.getId());
    folder.getMetadata().setLastChanged(new Date(file.lastModified()));

    return folder;
  }

  public void createNewArtifact(String containingFolderId, RepositoryArtifact artifact, Content artifactContent) {
    File newFile = new File(getFileFromId(containingFolderId), artifact.getId());
    BufferedOutputStream bos = null;

    try {
      if (newFile.createNewFile()) {
        bos = new BufferedOutputStream(new FileOutputStream(newFile));
        bos.write(artifactContent.asByteArray());
      }
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to create file " + artifact + " in folder " + containingFolderId);
    } finally {
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          throw new RepositoryException("Unable to create file " + artifact + " in folder " + containingFolderId);
        }
      }
    }
  }

  public void modifyArtifact(RepositoryArtifact artifact, ContentRepresentationDefinition artifactContent) {
    throw new UnsupportedOperationException("FileSystemConnector does not support modifying files!");
  }

  private String getLocalPath(String path) {
    if (path.startsWith(getConfiguration().getBasePath())) {
      path = path.replace(getConfiguration().getBasePath(), "");
      return path;
    }
    throw new RepositoryException("Unable to determine local path! ('" + path + "')");
  }
}
