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

package org.activiti.rest.test;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.model.Task;
import org.activiti.service.api.model.User;
import org.activiti.service.impl.util.LogUtil;
import org.activiti.service.rest.RestServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author Tom Baeyens
 */
public class RestTestCase extends TestCase {
  
  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath();
  }
  
  private static Logger log = Logger.getLogger(RestTestCase.class.getName());
  
  static Activiti activiti = null;
  static int serverPort = 8765;
  static String baseContextUrl = "http://localhost:"+serverPort+"/rest";
  static Server server = null;
  static TestMailService testMailService = new TestMailService();
  static Throwable servletException = null;

  protected String username = "kermit";
  protected String password = "kermit";
  
  public void setUp() throws Exception {
    super.setUp();
    
    if (server==null) {
      RestServlet restServlet = new TestRestServlet();
      server = new Server(serverPort);
      ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
      servletContextHandler.addServlet(new ServletHolder(restServlet), "/rest/*");
      server.start();
      
      activiti = restServlet.getActiviti();
      activiti
        .getActivitiConfiguration()
        .setMailService(testMailService);
      
      User defaultUser = new User()
        .setId(username)
        .setPassword(password);
      
      createUser(defaultUser);
    }
    
    servletException = null;
    testMailService.getMails().clear();
  }
  
  protected void tearDown() throws Exception {
    log.info("=== cleaning db at teardown =================================");
    activiti.clean(true);
    
    super.tearDown();
  }

  protected void createUser(User user) {
    activiti.getUsers().insertUser(user);
  }

  protected void deleteUser(String userId) {
    activiti.getUsers().deleteUser(userId);
  }

  protected void createTask(Task task) {
    activiti.getTasks().createTask(task);
  }

  protected void deleteTask(String taskId) {
    activiti.getTasks().deleteTask(taskId);
  }

  
  protected void setUser(String username, String password) {
    setUser(username, password, false);
  }
  
  protected void setUser(String username, String password, boolean create) {
    if (create) {
      User user = new User()
        .setId(username)
        .setPassword(password);
      createUser(user);
    }
    this.username = username;
    this.password = password;
  }
  
  public RestRequest createRestRequest(String url) {
    return new RestRequest(baseContextUrl, url).authenticate(username, password);
  }
  
  public RestRequest createRestRequestWithoutAuthentication(String url) {
    return new RestRequest(baseContextUrl, url);
  }
  
//  protected String get(String contextPath) {
//    HttpURLConnection urlConnection = null;
//    try {
//      String authHeaderValue = username+":"+password;
//      String encodedAuthHeaderValue = new String(Base64.encodeBase64(authHeaderValue.getBytes()));
//      urlConnection = (HttpURLConnection) new URL(baseContextUrl+contextPath).openConnection();
//      urlConnection.addRequestProperty("Auth", "Basic "+encodedAuthHeaderValue);
//      InputStream stream = urlConnection.getInputStream();
//      return new String(IoUtil.readInputStream(stream, "test servlet response"));
//    } catch (Exception e) {
//      
//      int responseCode = -1;
//      String responseMessage = null;
//      try {
//        responseCode = urlConnection.getResponseCode();
//        responseMessage = urlConnection.getResponseMessage();
//      } catch (IOException e2) {
//        e.printStackTrace();
//      }
//      
//      throw new RuntimeException("exception getting "+contextPath+" ("+responseCode+") "+responseMessage, e);
//    }
//  }
}
