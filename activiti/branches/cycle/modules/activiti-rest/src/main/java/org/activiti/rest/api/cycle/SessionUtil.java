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

import javax.servlet.http.HttpSession;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.RestClientRepositoryConnector;
import org.activiti.cycle.impl.connector.demo.DemoConnector;

public class SessionUtil {
  
  public static RepositoryConnector getRepositoryConnector(String currentUserId, HttpSession session) {
    String key = currentUserId + "_connector";
    RepositoryConnector conn = (RepositoryConnector)session.getAttribute(key);
    if (conn == null) {
      String contextPath = session.getServletContext().getContextPath();
      // TODO
      // Repository name and connector type should be determined based on a
      // persistent settings object.
      conn = new RestClientRepositoryConnector("demo-repo", contextPath, new DemoConnector());
      session.setAttribute(key, conn);
    }
    return conn;
  }
  
}
