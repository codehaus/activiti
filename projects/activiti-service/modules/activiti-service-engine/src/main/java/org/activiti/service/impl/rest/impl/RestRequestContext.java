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

package org.activiti.service.impl.rest.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.service.api.Activiti;
import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public class RestRequestContext {

  protected Activiti activiti;
  protected String authenticatedUserId;
  protected List<String> parameterErrors = null;
  protected HttpServletMethod httpServletMethod;
  protected HttpServletRequest httpServletRequest;
  protected HttpServletResponse httpServletResponse;
  protected Map<String, String> urlVariables;
  protected List<String> choppedPath;
  
  public RestRequestContext(Activiti activiti, HttpServletMethod httpServletMethod, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    this.activiti = activiti;
    this.httpServletMethod = httpServletMethod;
    this.httpServletRequest = httpServletRequest;
    this.httpServletResponse = httpServletResponse;
  }

  public void sendResponseJsonBean(Object bean) {
    String jsonText = activiti
      .getJsonConverter()
      .toJsonText(bean);
    sendResponseJsonText(jsonText);
  }
  
  public void sendResponseJsonBeans(List<? extends Object> beans) {
    JsonConverter jsonConverter = activiti.getJsonConverter();
    BasicDBList jsonList = new BasicDBList();
    for (Object bean: beans) {
      DBObject jsonBean = (DBObject) jsonConverter.toJson(bean);
      jsonList.add(jsonBean);
    }
    sendResponseJsonText(jsonList.toString());
  }

  public void sendResponseJsonText(String json) {
    sendResponse("text/json", json);
  }

  public void sendResponsePlainText(String text) {
    sendResponse("text/plain", text);
  }

  public void sendResponse(String mimeType, String text) {
    httpServletResponse.setContentType(mimeType);
    try {
      httpServletResponse
        .getOutputStream()
        .println(text);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getPathInfo() {
    return httpServletRequest.getPathInfo();
  }
  
  public void setUrlVariables(Map<String, String> urlVariables) {
    this.urlVariables = urlVariables;
  }
  
  public void addParameterError(String msg) {
    if (parameterErrors==null) {
      parameterErrors = new ArrayList<String>();
    }
    parameterErrors.add(msg);
  }
  
  public List<String> getChoppedPath() {
    if (choppedPath==null) {
      choppedPath = new ArrayList<String>();
      StringTokenizer tokenizer = new StringTokenizer(getPathInfo(), "/");
      while (tokenizer.hasMoreTokens()) {
        choppedPath.add(tokenizer.nextToken());
      }
    }
    return choppedPath;
  }
  
  public Map<String, String> getUrlVariables() {
    return urlVariables;
  }

  public HttpServletMethod getHttpServletMethod() {
    return httpServletMethod;
  }

  public HttpServletRequest getHttpServletRequest() {
    return httpServletRequest;
  }

  public HttpServletResponse getHttpServletResponse() {
    return httpServletResponse;
  }
  
  public String getAuthenticatedUserId() {
    return authenticatedUserId;
  }
  
  public void setAuthenticatedUserId(String authenticatedUserId) {
    this.authenticatedUserId = authenticatedUserId;
  }

  public Activiti getActiviti() {
    return activiti;
  }
}
