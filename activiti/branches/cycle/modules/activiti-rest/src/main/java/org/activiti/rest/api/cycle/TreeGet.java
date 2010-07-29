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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.activiti.cycle.RepositoryArtifact;
//import org.activiti.cycle.RepositoryConnector;
//import org.activiti.cycle.RepositoryFolder;
//import org.activiti.cycle.RepositoryNode;
//import org.activiti.cycle.impl.RestClientRepositoryConnector;
//import org.activiti.cycle.impl.connector.demo.DemoConnector;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Nils Preusker
 */
public class TreeGet extends ActivitiWebScript {

  // TODO
  // This should be done with the HTTPSession of the web application.
  // This static map is just a temporary HTTPSession replacement until we know
  // how to get the HTTPSession.
  //private static Map<String, RepositoryConnector> session;

  // static {
  //     if (session == null) {
  //       session = new HashMap<String, RepositoryConnector>();
  //     }
  //   }

  @Override
  protected void executeWebScript(WebScriptRequest req, Status status, Cache cache, Map<String, Object> model) {

    // String cuid = getCurrentUserId(req);
    // 
    //     RepositoryConnector conn = null;
    //     for (String key : session.keySet()) {
    //       if (key.equals(cuid)) {
    //         conn = session.get(key);
    //       }
    //     }
    //     if (conn == null) {
    //       String contextPath = req.getContextPath();
    //       // TODO
    //       // Repository name and connector type should be determined based on a
    //       // persistent settings object.
    //       conn = new RestClientRepositoryConnector("demo-repo", contextPath, new DemoConnector());
    //       session.put(cuid, conn);
    //     }
    // 
    //     String id = getString(req, "id");
    // 
    //     List<RepositoryNode> subtree = conn.getChildNodes(id == null ? "/" : id);
    // 
    //     model.put("subtree", subtree);
    // 
    //     for(RepositoryNode node : subtree) {
    //       if(node.getClass().isAssignableFrom(RepositoryArtifact.class)) {
    //         System.out.println(node.getId() + " is a file");
    //       } else if (node.getClass().isAssignableFrom(RepositoryFolder.class)) {
    //         System.out.println(node.getId() + "is a folder");
    //       }
    //     }
    
    // String repoUrl = req.getParameter("repourl");
    // String un = req.getParameter("un");
    // String pw = req.getParameter("pw");
    //    
    // model.put("tree", getRepoService().getChildren(repoUrl));
    // TODO: Create a repository object that can be converted into a
    // json string, which can be used to initialize the treeView component.

  }
}
