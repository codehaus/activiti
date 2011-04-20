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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.service.api.ActivitiException;
import org.apache.commons.codec.binary.Base64;


/**
 * @author Tom Baeyens
 */
public class RestRequest {
  
  private static Logger log = Logger.getLogger(RestRequest.class.getName());

  protected String contextPath;
  protected String userId;
  protected String password;
  protected int expectedResponseCode = -1;
  protected String expectedResponseMessage = null;

  protected HttpURLConnection httpUrlConnection;

  public RestRequest(String baseContextUrl, String contextPath) {
    try {
      this.contextPath = contextPath;
      String url = baseContextUrl+contextPath;
      this.httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
    } catch (Exception e) {
      log.log(Level.SEVERE, "couldn't create rest request: "+e.getMessage(), e);
    }
  }
  
  public RestRequest authenticate(String userId, String password) {
    String authHeaderValue = userId+":"+password;
    String encodedAuthHeaderValue = new String(Base64.encodeBase64(authHeaderValue.getBytes()));
    httpUrlConnection.addRequestProperty("Auth", "Basic "+encodedAuthHeaderValue);
    return this;
  }
  
  public int getExpectedResponseCode() {
    return expectedResponseCode;
  }
  
  public RestRequest setExpectedResponseCode(int expectedResponseCode) {
    this.expectedResponseCode = expectedResponseCode;
    return this;
  }

  public String getExpectedResponseMessage() {
    return expectedResponseMessage;
  }
  
  public RestRequest setExpectedResponseMessage(String expectedResponseMessage) {
    this.expectedResponseMessage = expectedResponseMessage;
    return this;
  }

  public RestResponse post(String postParameters) {
    try {
      httpUrlConnection.setDoOutput(true);
      httpUrlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
      httpUrlConnection.setRequestProperty( "Content-Length", Integer.toString(postParameters.length()) );
      httpUrlConnection.getOutputStream().write(postParameters.getBytes());
      log.info("posting "+contextPath+" with encoded parameters \n"+postParameters);

      return new RestResponse(this);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ActivitiException("posting failed: \n "+postParameters, e);
    }
  }

  public RestResponse get() {
    log.info("getting "+contextPath);
    return new RestResponse(this);
  }
  
  public HttpURLConnection getHttpUrlConnection() {
    return httpUrlConnection;
  }
  
  public String getContextPath() {
    return contextPath;
  }
}
