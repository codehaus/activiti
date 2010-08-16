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

/**
 * Object used to configure signavio connector. Candidate for Entity to save
 * config later on.
 * 
 * ALL url's have a trailing "/"!
 * 
 * @author bernd.ruecker@camunda.com
 */
public class SignavioConnectorConfiguration {

  /**
   * default URL
   */
  private String signavioUrl = "http://127.0.0.1:8080/";

  // TODO?
  private String name;

  // use it or not?
  private String folderRootUrl;
  //

  public static String SIGNAVIO_BACKEND_URL_SUFFIX = "p/";

  public static String REGISTRATION_URL_SUFFIX = "register/";
  public static String LOGIN_URL_SUFFIX = "login/";
  public static String EDITOR_URL_SUFFIX = "editor/";
  public static String EXPLORER_URL_SUFFIX = "explorer/";
  public static String MODEL_URL_SUFFIX = "model/";
  public static String DIRECTORY_URL_SUFFIX = "directory/";
  public static String MASHUP_URL_SUFFIX = "mashup/";

  public static String BPMN_20_EXPORT_SERVLET = "bpmn2_0serialization";

  public SignavioConnectorConfiguration() {
  }

  public SignavioConnectorConfiguration(String signavioUrl) {
    setSignavioUrl(signavioUrl);
  }

  public String getSignavioUrl() {
    return signavioUrl;
  }

  public String getSignavioBackendUrl() {
    return getSignavioUrl() + SIGNAVIO_BACKEND_URL_SUFFIX;
  }

  public void setSignavioUrl(String signavioUrl) {
    if (signavioUrl != null && !signavioUrl.endsWith("/")) {
      signavioUrl = signavioUrl + "/";
    }
    this.signavioUrl = signavioUrl;
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

  public String getModelUrl() {
    return getSignavioBackendUrl() + MODEL_URL_SUFFIX;
  }

  public String getDirectoryUrl() {
    return getSignavioBackendUrl() + DIRECTORY_URL_SUFFIX;
  }

  public String getMashupUrl() {
    return getSignavioBackendUrl() + MASHUP_URL_SUFFIX;
  }

  public String getBpmn20XmlExportServletUrl() {
    return getSignavioUrl() + EDITOR_URL_SUFFIX + BPMN_20_EXPORT_SERVLET;
  }
}
