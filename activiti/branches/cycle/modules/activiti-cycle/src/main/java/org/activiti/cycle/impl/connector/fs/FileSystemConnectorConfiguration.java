/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cycle.impl.connector.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.activiti.cycle.RepositoryException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Object used to configure FS connector. Candidate for Entity to save config
 * later on.
 * 
 * @author bernd.ruecker@camunda.com
 */
public class FileSystemConnectorConfiguration {

  private static final String DEFAULT_ROOT = "c:";
  /**
   * default URL
   */
  private File rootPath = new File(DEFAULT_ROOT);

  // TODO?
  private String name;

  public FileSystemConnectorConfiguration() {
  }

  public FileSystemConnectorConfiguration(String rootPath) {
    this.rootPath = new File(rootPath);
  }

  public String getRootPath() {
    try {
      return rootPath.getCanonicalPath();
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to get rootPath!", ioe);
    }
  }

  public void setRootPath(String rootPath) {
    if (rootPath != null && !rootPath.endsWith("/")) {
      rootPath = rootPath + "/";
    }
    this.rootPath = new File(rootPath);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void readConfiguration() {
    XStream xstream = new XStream(new DomDriver());
    try {
      xstream.fromXML(new FileReader(this.getClass().getSimpleName() + ".xml"));
    } catch (FileNotFoundException fnfe) {
      throw new RepositoryException("Unable to find configuration file '" + this.getClass().getSimpleName() + ".xml'", fnfe);
    }
  }

  public void writeConfiguration() {
    try {
      XStream xstream = new XStream(new DomDriver());
      xstream.aliasType(this.getClass().getSimpleName(), this.getClass());
      xstream.toXML(this, new FileWriter(this.getClass().getSimpleName() + ".xml"));
    } catch (IOException ioe) {
      throw new RepositoryException("Unable to write configuration file '" + this.getClass().getSimpleName() + ".xml'", ioe);
    }
  }

}
