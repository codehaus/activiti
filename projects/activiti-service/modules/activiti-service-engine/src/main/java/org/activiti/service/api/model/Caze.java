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

package org.activiti.service.api.model;

import org.activiti.service.impl.persistence.Persistable;


/**
 * @author Tom Baeyens
 */
public class Caze extends Persistable {
  
  String title;
  String description;
  String assignee;

  public String getTitle() {
    return title;
  }
  
  public Caze setTitle(String title) {
    this.title = title;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public Caze setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public String getAssignee() {
    return assignee;
  }
  
  public Caze setAssignee(String assignee) {
    this.assignee = assignee;
    return this;
  }
}
