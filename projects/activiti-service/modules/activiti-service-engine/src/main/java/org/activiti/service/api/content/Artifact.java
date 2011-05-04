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

package org.activiti.service.api.content;

import java.util.List;

import org.activiti.service.impl.persistence.AbstractPersistable;


/**
 * @author Tom Baeyens
 */
public class Artifact extends AbstractPersistable {

  String userId;
  String repository;
  List<String> folder;
  String name;
  String type;
  String owner;
  String lastModified;
  
  public String getUserId() {
    return userId;
  }
  
  public Artifact setUserId(String userId) {
    this.userId = userId;
    return this;
  }
  
  public String getRepository() {
    return repository;
  }
  
  public Artifact setRepository(String repository) {
    this.repository = repository;
    return this;
  }
  
  public List<String> getFolder() {
    return folder;
  }
  
  public Artifact setFolder(List<String> folder) {
    this.folder = folder;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  public Artifact setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getType() {
    return type;
  }
  
  public Artifact setType(String type) {
    this.type = type;
    return this;
  }
  
  
}
