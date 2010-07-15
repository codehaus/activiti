/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.impl.history;

import org.activiti.ProcessInstance;
import org.activiti.pvm.Activity;

/**
 * @author Christian Stettler
 */
// TODO: move next to other services in org.activiti package ???
public interface HistoricDataService {

  HistoricProcessInstance createHistoricProcessInstance(ProcessInstance processInstance);

  HistoricProcessInstance findHistoricProcessInstance(String processInstanceId);

  HistoricProcessInstance markHistoricProcessInstanceEnded(String processInstanceId, String endStateName);

  HistoricActivityInstance createHistoricActivityInstance(Activity activity, ProcessInstance processInstance);

  HistoricActivityInstance findHistoricActivityInstance(String activityId, String processInstanceId);

  HistoricActivityInstance markHistoricActivityInstanceEnded(String activityId, String processInstanceId);

}
