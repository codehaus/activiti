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
package org.activiti.cycle.impl.connector.signavio.provider;

import org.activiti.cycle.ContentRepresentationType;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryException;
import org.restlet.data.Response;

public class JsonProvider extends SignavioContentRepresentationProvider {

  public static final String NAME = "json";

  public JsonProvider() {
    super(NAME, ContentRepresentationType.TEXT);
  }

  public byte[] getContent(RepositoryArtifact artifact) {
    try {
      Response jsonResponse = getJsonResponse(artifact, "/json");
      String jsonString = jsonResponse.getEntity().getText();
      return toBytes(jsonString);
    } catch (Exception ex) {
      throw new RepositoryException("Error while accessing Signavio repository", ex);
    }
  }

}
