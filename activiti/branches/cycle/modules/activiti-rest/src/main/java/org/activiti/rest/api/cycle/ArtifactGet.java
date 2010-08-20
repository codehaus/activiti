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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.rest.api.cycle.dto.ContentView;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * @author Nils Preusker
 */
public class ArtifactGet extends ActivitiWebScript {

  @Override
  protected void executeWebScript(WebScriptRequest req, Status status, Cache cache, Map<String, Object> model) {
    // Retrieve the artifactId from the request
    String artifactId = getString(req, "artifactId");

    // Retrieve session and repo connector
    String cuid = getCurrentUserId(req);

    HttpSession session = ((WebScriptServletRequest) req).getHttpServletRequest().getSession(true);
    RepositoryConnector conn = SessionUtil.getRepositoryConnector(cuid, session);

    // Retrieve the artifact from the repository
    RepositoryArtifact artifact = conn.getArtifactDetails(artifactId);

    List<ContentView> contentViews = new ArrayList<ContentView>();
    for (ContentRepresentationDefinition representation : artifact.getContentRepresentationDefinitions()) {
      try {
        String value = "";
        if (representation.getType().equals("txt") || representation.getType().equals("xml")) {
          value = conn.getContent(artifactId, representation.getName()).asString();
          contentViews.add(new ContentView(representation.getType(), representation.getName(), value));
        } else if (representation.getType().equals("img")) {
          value = req.getServerPath() + req.getContextPath() + "/service/content?artifactId=" + URLEncoder.encode(artifactId, "UTF-8");
          contentViews.add(new ContentView(representation.getType(), representation.getName(), value));
        }
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    model.put("artifactId", artifact.getId());
    model.put("contentViews", contentViews);

  }
}
