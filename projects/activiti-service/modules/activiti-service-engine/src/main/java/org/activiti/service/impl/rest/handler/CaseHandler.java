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

import org.activiti.service.api.Cases;
import org.activiti.service.api.cases.Case;
import org.activiti.service.impl.rest.impl.HttpServletMethod;
import org.activiti.service.impl.rest.impl.RestHandler;
import org.activiti.service.impl.rest.impl.RestRequestContext;
import org.activiti.service.impl.rest.parameter.StringParameter;


/**
 * @author Tom Baeyens
 */
public class CaseHandler extends RestHandler {

  public HttpServletMethod getMethod() {
    return HttpServletMethod.GET;
  }

  public String getUrlPattern() {
    return "/case/{caseOid}";
  }

  protected StringParameter caseOidParameter = (StringParameter) new StringParameter()
    .setUrlVariable()
    .setName("caseOid") 
    .setDescription("the oid of the case")
    .setMaxLength(20);

  public void handle(RestRequestContext restRequestContext) {
    String caseOid = caseOidParameter.get(restRequestContext);
    
    Case caze = restRequestContext
      .getActiviti()
      .getManager(Cases.class)
      .findOneByOid(caseOid);

    restRequestContext.sendResponseJsonBean(caze);
  }
}
