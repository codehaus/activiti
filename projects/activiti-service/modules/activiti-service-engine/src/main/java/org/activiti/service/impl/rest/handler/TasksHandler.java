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

package org.activiti.service.impl.rest.handler;

import java.util.List;

import org.activiti.service.impl.rest.RestRequestContext;
import org.activiti.service.impl.rest.impl.HttpServletMethod;
import org.activiti.service.impl.rest.impl.IntegerParameter;
import org.activiti.service.impl.rest.impl.RestHandler;

import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class TasksHandler extends RestHandler {
  
  // private static Logger log = Logger.getLogger(TasksHandler.class.getName());
  
  public HttpServletMethod getMethod() {
    return HttpServletMethod.GET;
  }
  
  public String getUrlPattern() {
    return "/tasks";
  }

  protected IntegerParameter firstResult = new IntegerParameter()
    .setName("first")
    .setDescription("first result to be shown starting from 0 (zero)")
    .setDefaultValue(0)
    .setMaxLength(20)
    .setMinValue(0)
    .setMaxValue(Integer.MAX_VALUE);
  
  protected IntegerParameter maxResults = new IntegerParameter()
    .setName("max") 
    .setDescription("max number of tasks to be retrieved")
    .setDefaultValue(10)
    .setMaxLength(20)
    .setMinValue(1)
    .setMaxValue(Integer.MAX_VALUE);

  public void handle(RestRequestContext restRequestContext) {
    // call the activiti api
    List<DBObject> tasksJson = restRequestContext
      .getActiviti()
      .getTasks()
      .findTasksJson(restRequestContext.getAuthenticatedUserId(), firstResult.get(restRequestContext), maxResults.get(restRequestContext));
    
    // send response
    restRequestContext.sendResponse(tasksJson);
  }
}
