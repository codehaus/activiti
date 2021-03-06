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
 * Programmatic querying for {@link HistoricProcessVariable}s.
 * 
 * @author Christian Lipphardt (camunda)
 */
public interface HistoricProcessVariableQuery extends Query<HistoricProcessVariableQuery, HistoricProcessVariable> {

  /** Only select historic process variables with the given process instance. */
  HistoricProcessVariableQuery processInstanceId(String processInstanceId);

  /** Only select historic process variables with the given variable name. */
  HistoricProcessVariableQuery variableName(String variableName);
  
  /** Only select historic process variables where the given variable name is like. */
  HistoricProcessVariableQuery variableNameLike(String variableNameLike);

  /**
   * only select historic process variables with the given name and value
   */
  HistoricProcessVariableQuery variableValueEquals(String variableName, Object variableValue);

  HistoricProcessVariableQuery orderByProcessInstanceId();
  
  HistoricProcessVariableQuery orderByVariableName();
  
  HistoricProcessVariableQuery orderByTime();
}
