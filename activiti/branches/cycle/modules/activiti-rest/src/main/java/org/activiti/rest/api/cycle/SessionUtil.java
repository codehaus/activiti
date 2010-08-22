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
package org.activiti.rest.api.cycle;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.conf.ConfigurationContainer;
import org.activiti.cycle.impl.conf.CycleConfigurationService;
import org.activiti.cycle.impl.conf.SimpleXstreamRepositoryConnectorConfigurationManager;
import org.activiti.cycle.impl.connector.demo.DemoConnectorConfiguration;
import org.activiti.cycle.impl.connector.fs.FileSystemConnectorConfiguration;
import org.activiti.cycle.impl.connector.signavio.SignavioConnectorConfiguration;
import org.activiti.cycle.impl.connector.view.CustomizedViewConfiguration;

public class SessionUtil {

  /**
   * TODO: Use an OS independent default
   */
  private static final File fsBaseDir = new File("c:");

  /**
   * Not yet used, so can be null for the moment (but we maybe better should use
   * it?)
   */
  private static final File configBaseDir = null;

  public static RepositoryConnector getRepositoryConnector(String currentUserId, HttpSession session) {
    String key = currentUserId + "_connector";
    RepositoryConnector connector = (RepositoryConnector) session.getAttribute(key);
    if (connector == null) {
      String contextPath = session.getServletContext().getContextPath();

      ConfigurationContainer configuration = loadUserConfiguration(currentUserId);
      connector = new CustomizedViewConfiguration(contextPath, configuration).createConnector();
      
      session.setAttribute(key, connector);      
    }
    return connector;
  }

  /**
   * loads the configuration for this user. If no configuration exists, a demo
   * config is created and save to file (this xml can be easily modified later
   * on to play around with it).
   * 
   * This is a temporary solution until real persistence for configs is in place
   * 
   * TODO: This should be rewritten as soon as we have real persistence and
   * stuff
   */
  public static ConfigurationContainer loadUserConfiguration(String currentUserId) {
    CycleConfigurationService cycleConfigurationService = new SimpleXstreamRepositoryConnectorConfigurationManager(configBaseDir);

    ConfigurationContainer configuration = cycleConfigurationService.getConfiguration(currentUserId);
    if (configuration == null) {
      configuration = createDefaultDemoConfiguration();
      cycleConfigurationService.saveConfiguration(configuration);
    }
    return configuration;
  }

  public static ConfigurationContainer createDefaultDemoConfiguration() {
    ConfigurationContainer configuration = new ConfigurationContainer("Activiti Demo Setup");
    configuration.addRepositoryConnectorConfiguration(new DemoConnectorConfiguration("demo"));
    configuration.addRepositoryConnectorConfiguration(new SignavioConnectorConfiguration("signavio", "http://localhost:8080/activiti-modeler/"));
    configuration.addRepositoryConnectorConfiguration(new FileSystemConnectorConfiguration("files", fsBaseDir));
    return configuration;
  }
  
}
