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

import java.util.List;
import java.util.Map;

import org.activiti.rest.util.ActivitiRequest;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

/**
 * @author Nils Preusker (nils.preusker@camunda.com)
 */
public class TagsGet extends ActivitiCycleWebScript {

  @Override
  void execute(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {

    String connectorId = req.getString("connectorId");
    String repositoryNodeId = req.getString("nodeId");
    String tag = req.getString("tag");

    List<String> tags;
    if (connectorId != null && repositoryNodeId != null) {
      tags = tagService.getTags(connectorId, repositoryNodeId);
    } else {
      tags = tagService.getSimiliarTagNames(tag != null ? tag : "");
    } 
    model.put("tags", tags);
  }
}
