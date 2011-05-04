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

import java.util.ArrayList;
import java.util.List;

import org.activiti.service.impl.json.JsonList;
import org.activiti.service.impl.persistence.AbstractPersistable;


/**
 * @author Tom Baeyens
 */
public class Folder extends AbstractPersistable {

  String name;
  
  @JsonList(type=Folder.class)
  List<Folder> folders = new ArrayList<Folder>();

  public String getName() {
    return name;
  }
  
  public Folder addFolder(Folder folder) {
    folders.add(folder);
    return this;
  }
  
  public Folder setName(String name) {
    this.name = name;
    return this;
  }
  
  public List<Folder> getFolders() {
    return folders;
  }
}
