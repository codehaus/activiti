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
package org.activiti.cycle;

import java.util.logging.Logger;

/**
 * The file action defines an action you can execute upon a file / artifact
 * normally depending on the {@link ArtifactType}.
 * 
 * And it can have come functionality to do something with the file. Idea: Could
 * this be maybe implemented as some kind of pipeline, where the file can be
 * transformed in multiple steps and somewhere ends in a last action which cares
 * about the GUI representation?
 * 
 * @author bernd.ruecker@camunda.com
 */
public abstract class ArtifactAction {

  protected static Logger log = Logger.getLogger(ArtifactAction.class.getName());

  private RepositoryArtifact artifact;

  private final String name = this.getClass().getName();

  public ArtifactAction() {
  }

  public ArtifactAction(RepositoryArtifact artifact) {    
    setArtifact(artifact);
  }

  public abstract String getLabel();

   public RepositoryArtifact getArtifact() {
    return artifact;
  }
  
  public void setArtifact(RepositoryArtifact artifact) {
    this.artifact = artifact;
  }
  
  public String getName() {
    return name;
  }

}
