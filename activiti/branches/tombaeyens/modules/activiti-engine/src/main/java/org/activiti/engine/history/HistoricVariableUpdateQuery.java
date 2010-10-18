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

package org.activiti.engine.history;

import org.activiti.engine.query.Query;


/**
 * @author Tom Baeyens
 */
public interface HistoricVariableUpdateQuery extends Query<HistoricVariableUpdateQuery, HistoricVariableUpdate> {

  /** Only select historic variable updates with the given process instance.
   * {@link ProcessInstance) ids and {@link HistoricProcessInstance} ids match. */
  HistoricVariableUpdateQuery processInstanceId(String processInstanceId);

  /** Only select historic variable updates with the given variableName. */
  HistoricVariableUpdateQuery variableName(String variableName);

  
}
