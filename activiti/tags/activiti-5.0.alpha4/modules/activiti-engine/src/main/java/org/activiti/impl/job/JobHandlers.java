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
package org.activiti.impl.job;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Tom Baeyens
 */
public class JobHandlers {

  Map<String, JobHandler> jobHandlers = new HashMap<String, JobHandler>();
  
  public JobHandlers addJobHandler(JobHandler jobHandler) {
    jobHandlers.put(jobHandler.getType(), jobHandler);
    return this;
  }

  public void removeJobHandler(JobHandler jobHandler) {
    jobHandlers.remove(jobHandler.getType());
  }

  public JobHandler getJobHandler(String jobCommandType) {
    return jobHandlers.get(jobCommandType);
  }

  
  public Map<String, JobHandler> getJobHandlers() {
    return jobHandlers;
  }

  
  public void setJobHandlers(Map<String, JobHandler> jobHandlers) {
    this.jobHandlers = jobHandlers;
  }
}
