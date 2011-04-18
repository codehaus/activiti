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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.activiti.service.impl.rest.impl.HttpServletMethod;
import org.activiti.service.impl.rest.impl.Parameter;
import org.activiti.service.impl.rest.impl.RestHandler;
import org.activiti.service.impl.rest.impl.UrlMatcher;
import org.activiti.service.rest.RestServlet;


/**
 * @author Tom Baeyens
 */
public class HelpHandler extends RestHandler {

  RestServlet restServlet;

  public HelpHandler(RestServlet restServlet) {
    this.restServlet = restServlet;
  }

  public HttpServletMethod getMethod() {
    return HttpServletMethod.GET;
  }

  public boolean requiresAuthentication() {
    return false;
  }

  public String getUrlPattern() {
    return "/help";
  }

  public void handle(RestRequestContext restRequestContext) {
    HttpServletResponse httpServletResponse = restRequestContext.getHttpServletResponse();
    httpServletResponse.setContentType("text/html");
    try {
      ServletOutputStream out = httpServletResponse.getOutputStream();
      out.println("<html><body>");
      out.println("<h1>Activiti Rest Interface</h1>");
      out.println("<p>This page documents the rest interface of Activiti</p>");
      
      Map<String, RestHandler> restHandlers = new HashMap<String, RestHandler>();
      restHandlers.putAll(restServlet.getStaticHandlers());
      for (UrlMatcher urlMatcher: restServlet.getDynamicHandlers()) {
        RestHandler restHandler = urlMatcher.getRestHandler();
        restHandlers.put(restHandler.getUrlPattern(), restHandler);
      }
      SortedSet<String> urlPatterns = new TreeSet<String>(restHandlers.keySet());
      for (String urlPattern: urlPatterns) {
        RestHandler restHandler = restHandlers.get(urlPattern);
        String parametersTemplate = "";
        List<String> parameterDescriptions = new ArrayList<String>();
        for (Field handlerField: restHandler.getClass().getDeclaredFields()) {
          if (Parameter.class.isAssignableFrom(handlerField.getType())) {
            handlerField.setAccessible(true);
            Parameter<?> parameter = (Parameter<?>) handlerField.get(restHandler);
            String parameterName = parameter.getName();
            if (parameter.isParameter()) {
              if (parametersTemplate.length()==0) {
                parametersTemplate += "?";
              } else {
                parametersTemplate += "&";
              }
              if (parameter.isRequired()) {
                parametersTemplate += parameterName+"={"+parameterName+"}";
              } else {
                parametersTemplate += "["+parameterName+"={"+parameterName+"}]";
              }
            }
            StringBuilder parameterDescription = new StringBuilder();
            parameterDescription.append("  <li><b><code>"+parameterName+"</code></b> ");
            if (parameter.isParameter()) {
              parameterDescription.append(": parameter \n");
            } else {
              parameterDescription.append(": url part variable \n");
            }
            parameterDescription.append("    <ul> \n");
            parameterDescription.append("      <li>"+parameter.getDescription()+"</li> \n");
            parameterDescription.append("      <li>"+(parameter.isRequired() ? "required" : "optional")+"</li> \n");
            parameterDescription.append("      <li>"+parameter.getTypeDescription()+"</li> \n");
            parameterDescription.append("    </ul> \n");
            parameterDescription.append("  </li> \n");
            parameterDescriptions.add(parameterDescription.toString());
          }
        }
        
        out.print("<h3 style='margin-bottom:5px;'><code>");
        out.print(restHandler.getMethod().toString()+" "+urlPattern+parametersTemplate);
        out.println("</code></h3>");
        out.println("  <ul style='margin-top:5px;'>");
        
        for (String parameterDescription: parameterDescriptions) {
          out.print(parameterDescription);
        }
        
        out.println("  </ul>");
        
        out.println("</body></html>");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
