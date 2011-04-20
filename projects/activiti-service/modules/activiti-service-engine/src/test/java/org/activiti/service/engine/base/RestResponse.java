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

package org.activiti.service.engine.base;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.util.IoUtil;


/**
 * @author Tom Baeyens
 */
public class RestResponse {
  
  private static Logger log = Logger.getLogger(RestResponse.class.getName());

  RestRequest restRequest;
  protected HttpURLConnection httpUrlConnection;
  protected String content;

  public RestResponse(RestRequest restRequest) {
    this.restRequest = restRequest;
    this.httpUrlConnection = restRequest.getHttpUrlConnection();
    try {
      InputStream stream = httpUrlConnection.getInputStream();
      content = new String(IoUtil.readInputStream(stream, "test servlet response"));
      log.info("response: "+content);
      
    } catch (Exception e) {
      int responseCode = -1;
      String responseMessage = null;
      try {
        responseCode = httpUrlConnection.getResponseCode();
        responseMessage = httpUrlConnection.getResponseMessage();
      } catch (Exception e2) {
        log.log(Level.SEVERE, "exception getting response code and message"+e2.getMessage(), e2);
      }
      
      log.info("response: ["+responseCode+"] "+responseMessage);
      
      if (restRequest.getExpectedResponseCode()!=responseCode
           || ( (restRequest.getExpectedResponseMessage()!=null)
                && (!restRequest.getExpectedResponseMessage().equals(responseMessage))
              )
         ){
        throw new ActivitiException("exception while performing request "+restRequest.getContextPath()+" ["+responseCode+"] "+responseMessage, RestTestCase.servletException);
      }
    }
  }

  public HttpURLConnection getHttpUrlConnection() {
    return httpUrlConnection;
  }

  public String getContent() {
    return content;
  }
}
