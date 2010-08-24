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
package org.activiti.cycle.impl.connector.signavio;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.conf.PasswordEnabledRepositoryConnectorConfiguration;

/**
 * Object used to configure signavio connector. Candidate for Entity to save
 * config later on.
 * 
 * The Signavio URL has a trailing "/", the URL-SUFFIXES TODO: CHECK
 * 
 * @author bernd.ruecker@camunda.com
 */
public class SignavioConnectorConfiguration extends PasswordEnabledRepositoryConnectorConfiguration {

  // TODO: use it or not?
  private String folderRootUrl;

  private String signavioBaseUrl;
  //

  public static String SIGNAVIO_BACKEND_URL_SUFFIX = "p/";

  public static String REGISTRATION_URL_SUFFIX = "register/";
  public static String LOGIN_URL_SUFFIX = "login/";
  public static String EDITOR_URL_SUFFIX = "editor/";
  public static String EXPLORER_URL_SUFFIX = "explorer/";
  public static String MODEL_URL_SUFFIX = "model";
  public static String DIRECTORY_URL_SUFFIX = "directory";
  public static String MASHUP_URL_SUFFIX = "mashup/";

  public static String BPMN_20_EXPORT_SERVLET = "bpmn2_0serialization";
  
  public SignavioConnectorConfiguration() {
    signavioBaseUrl = "http://127.0.0.1:8080/";
  }

  public SignavioConnectorConfiguration(String signavioUrl) {
    signavioBaseUrl = signavioUrl;
  }
  
  public SignavioConnectorConfiguration(String name, String signavioBaseUrl, String folderRootUrl, String password, String user) {
    setName(name);
    this.signavioBaseUrl = signavioBaseUrl;
    this.folderRootUrl = folderRootUrl;
    setPassword(password);
    setUser(user);
  }  

  public SignavioConnectorConfiguration(String name, String signavioBaseUrl) {
    setName(name);
    this.signavioBaseUrl = signavioBaseUrl;    
  }  

  public String getSignavioUrl() {
    return signavioBaseUrl;
  }

  public void setSignavioUrl(String path) {
    if (path != null && !path.endsWith("/")) {
      path = path + "/";
    }
    this.signavioBaseUrl = path;
  }

  public String getSignavioBackendUrl() {
    return getSignavioUrl() + SIGNAVIO_BACKEND_URL_SUFFIX;
  }

  public String getDirectoryIdFromUrl(String href) {
    return retrieveIdFromUrl(href, "/" + DIRECTORY_URL_SUFFIX);
  }

  public String getModelIdFromUrl(String href) {
    return retrieveIdFromUrl(href, "/" + MODEL_URL_SUFFIX);
  }

  /**
   * get the part of the URL identifying the real ID needed to be stored in the
   * API object to be able to identify the object later on
   */
  private String retrieveIdFromUrl(String href, String baseUrl) {
    // TODO: Check implementation!
    return href.replaceAll(baseUrl, "");
  }
  
  public String getModelUrl(String id) {
    if (id.startsWith("/")) {
      // this is how it should be now
      return getSignavioBackendUrl() + MODEL_URL_SUFFIX + id;
    } else {
      // this is how it was in ancient times
      return getSignavioBackendUrl() + MODEL_URL_SUFFIX + "/" + id;
    }
  }

  public String getDirectoryUrl(String id) {
    if (id.startsWith("/")) {
      // this is how it should be now
      return getSignavioBackendUrl() + DIRECTORY_URL_SUFFIX + id;
    } else {
      // this is how it was in ancient times
      return getSignavioBackendUrl() + DIRECTORY_URL_SUFFIX + "/" + id;
    }
  }

  public String getRegistrationUrl() {
    return getSignavioBackendUrl() + REGISTRATION_URL_SUFFIX;
  }

  public String getLoginUrl() {
    return getSignavioBackendUrl() + LOGIN_URL_SUFFIX;
  }

  public String getEditorUrl() {
    return getSignavioBackendUrl() + EDITOR_URL_SUFFIX;
  }

  public String getExplorerUrl() {
    return getSignavioBackendUrl() + EXPLORER_URL_SUFFIX;
  }
  
  /**
   * TODO: Rename?
   */
  public String getModelRootUrl() {
    return getSignavioBackendUrl() + MODEL_URL_SUFFIX + "/";
  }

  public String getDirectoryRootUrl() {
    return getSignavioBackendUrl() + DIRECTORY_URL_SUFFIX + "/";
  }

  public String getMashupUrl() {
    return getSignavioBackendUrl() + MASHUP_URL_SUFFIX;
  }

  public String getBpmn20XmlExportServletUrl() {
    return getSignavioUrl() + EDITOR_URL_SUFFIX + BPMN_20_EXPORT_SERVLET;
  }

  @Override
  public RepositoryConnector createConnector() {
    return new SignavioConnector(this);
  }
}
