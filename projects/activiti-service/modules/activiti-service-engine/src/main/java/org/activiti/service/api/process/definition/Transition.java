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

package org.activiti.service.api.process.definition;



/**
 * @author Tom Baeyens
 */
public class Transition {

  String id;
  String to;
  Condition condition;

  public String getId() {
    return id;
  }

  public Transition setId(String id) {
    this.id = id;
    return this;
  }

  public String getTo() {
    return to;
  }

  public Transition setTo(String to) {
    this.to = to;
    return this;
  }
  
  public Condition getCondition() {
    return condition;
  }

  public Transition setCondition(Condition condition) {
    this.condition = condition;
    return this;
  }
}
