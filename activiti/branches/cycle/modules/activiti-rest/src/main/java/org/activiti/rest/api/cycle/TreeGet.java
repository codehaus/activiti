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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Nils Preusker
 */
public class TreeGet extends ActivitiWebScript {

  

  @Override
  protected void executeWebScript(WebScriptRequest req, Status status, Cache cache, Map<String, Object> model) {

    // TODO: ServletUtil.getSession(true) currently returns null, 
    // check whether this is normal and we first need to create 
    // a session or whether there is a problem with the retrieval 
    // of sessions.
    // HttpSession session = ServletUtil.getSession(true);

    String cuid = getCurrentUserId(req);

    // RepositoryConnector conn = (RepositoryConnector)
    // session.getAttribute("conn");
    
    Map<String, Object> mySession = TmpSessionHandler.getSessionByUserId(cuid);
        
    RepositoryConnector conn = TmpSessionHandler.getRepositoryConnector(req, mySession);

    String id = getString(req, "id");
    List<RepositoryNode> subtree = conn.getChildNodes(id == null ? "/" : id);

    List<RepositoryArtifact> files = new ArrayList<RepositoryArtifact>();
    List<RepositoryFolder> folders = new ArrayList<RepositoryFolder>();

    for (RepositoryNode node : subtree) {
      if (node.getClass().isAssignableFrom(RepositoryArtifact.class)) {
        files.add((RepositoryArtifact) node);
      } else if (node.getClass().isAssignableFrom(RepositoryFolder.class)) {
        folders.add((RepositoryFolder) node);
      }
    }

    model.put("files", files);
    model.put("folders", folders);
  }
}
