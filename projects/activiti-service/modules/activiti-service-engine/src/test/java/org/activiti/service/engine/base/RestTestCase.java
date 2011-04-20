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

package org.activiti.service.engine.base;

import org.activiti.service.api.Activiti;
import org.activiti.service.rest.RestServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author Tom Baeyens
 */
public class RestTestCase extends ActivitiTestCase {
  
  static int serverPort = 8765;
  static String baseContextUrl = "http://localhost:"+serverPort+"/rest";
  static Server server = null;
  static Throwable servletException = null;
  
  protected Activiti buildActiviti() throws Exception {
    RestServlet restServlet = new TestRestServlet();
    server = new Server(serverPort);
    ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
    servletContextHandler.addServlet(new ServletHolder(restServlet), "/rest/*");
    server.start();
    
    return restServlet.getActiviti();
  }

  public RestRequest createRestRequest(String url) {
    return new RestRequest(baseContextUrl, url).authenticate(username, password);
  }
  
  public RestRequest createRestRequestWithoutAuthentication(String url) {
    return new RestRequest(baseContextUrl, url);
  }
}
