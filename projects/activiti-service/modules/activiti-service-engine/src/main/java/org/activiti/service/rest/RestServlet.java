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
package org.activiti.service.rest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiConfiguration;
import org.activiti.service.api.ActivitiException;
import org.activiti.service.api.model.User;
import org.activiti.service.impl.rest.handler.HelpHandler;
import org.activiti.service.impl.rest.handler.RegisterHandler;
import org.activiti.service.impl.rest.handler.RestRequestContext;
import org.activiti.service.impl.rest.handler.TaskHandler;
import org.activiti.service.impl.rest.handler.TasksHandler;
import org.activiti.service.impl.rest.impl.BadRequestException;
import org.activiti.service.impl.rest.impl.HttpServletMethod;
import org.activiti.service.impl.rest.impl.Parameter;
import org.activiti.service.impl.rest.impl.RestException;
import org.activiti.service.impl.rest.impl.RestHandler;
import org.activiti.service.impl.rest.impl.UrlMatcher;
import org.apache.commons.codec.binary.Base64;


/**
 * @author Tom Baeyens
 */
public class RestServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static Logger log = Logger.getLogger(RestServlet.class.getName());

  protected Map<String, RestHandler> staticHandlers = new HashMap<String, RestHandler>();
  protected List<UrlMatcher> dynamicHandlers = new ArrayList<UrlMatcher>();

  protected Activiti activiti;

  public RestServlet() { 
    register(new HelpHandler(this));
    register(new RegisterHandler());
    register(new TasksHandler());
    register(new TaskHandler());
    activiti = new ActivitiConfiguration().buildActiviti();
  }

  public void register(RestHandler handler) {
    String urlPattern = handler.getUrlPattern();
    if (urlPattern.indexOf('{')==-1) {
      staticHandlers.put(urlPattern, handler);
    } else {
      dynamicHandlers.add(new UrlMatcher(handler));
    }
  }

  private RestHandler getRestHandler(RestRequestContext restRequestContext) {
    String pathInfo = restRequestContext.getPathInfo();
    RestHandler restHandler = staticHandlers.get(pathInfo);
    if (restHandler==null) {
      Iterator<UrlMatcher> iter = dynamicHandlers.iterator();
      while (restHandler==null && iter.hasNext()) {
        UrlMatcher urlMatcher = iter.next();
        Map<String, String> urlVariables = urlMatcher.matches(restRequestContext);
        if (urlVariables!=null) {
          restRequestContext.setUrlVariables(urlVariables);
          restHandler = urlMatcher.getRestHandler();
        }
      }
      if (restHandler==null) {
        throw new BadRequestException("invalid url "+pathInfo);
      }
    }
    return restHandler;
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    authenticateAndHandle(HttpServletMethod.GET, request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    authenticateAndHandle(HttpServletMethod.POST, request, response);
  }

  protected void authenticateAndHandle(HttpServletMethod method, HttpServletRequest request, HttpServletResponse response) {
    RestRequestContext restRequestContext = new RestRequestContext(activiti, method, request, response);
    try {
      RestHandler restHandler = getRestHandler(restRequestContext);
      if (restHandler.requiresAuthentication()) {
        authenticate(restRequestContext);
      }
      restHandler.handle(restRequestContext);

    } catch (RestException e) {
      logException(e, restRequestContext);
      try {
        restRequestContext.getHttpServletResponse().sendError(e.getResponseCode(), e.getMessage());
      } catch (Exception e1) {
        log.log(Level.SEVERE, "problem while sending error", e);
      }
      throw e;
      
    } catch (Throwable e) {
      logException(e, restRequestContext);
      throw new ActivitiException("rest exception", e);
    }
  }

  protected void logException(Throwable e, RestRequestContext restRequestContext) {
    log.log(Level.SEVERE, "exception while processing "+restRequestContext.getHttpServletRequest().getPathInfo()+" : "+e.getMessage(), e);
  }

  protected void authenticate(RestRequestContext restRequestContext) {
    HttpServletRequest httpServletRequest = restRequestContext.getHttpServletRequest();
    String headerText = httpServletRequest.getHeader("Auth");
    if (headerText==null) {
      headerText = httpServletRequest.getHeader("Authorization");
    }
    if ( headerText!=null 
         && headerText.startsWith("Basic ") 
       ) {
      String encodedHeader = headerText.substring(6);
      String decodedHeader = new String(Base64.decodeBase64(encodedHeader));
      int colonIndex = decodedHeader.indexOf(':');
      if (colonIndex!=-1) {
        String username = decodedHeader.substring(0, colonIndex);
        String password = decodedHeader.substring(colonIndex+1);
        
        User user = activiti
          .getUsers()
          .findUser(username);
        
        boolean authenticationOk = password.equals(user.getPassword());
        
        if (authenticationOk) {
           restRequestContext.setAuthenticatedUserId(username);
           return;
        }
      }
    }
    
    restRequestContext.getHttpServletResponse().setHeader("WWW-Authenticate", "Basic realm=\"Activiti\"");
    throw new RestException(HttpServletResponse.SC_UNAUTHORIZED, "basic authentication required");
  }

  protected void sendHelp(HttpServletResponse response) {
    response.setContentType("text/html");
    try {
      ServletOutputStream out = response.getOutputStream();
      out.println("<html><body>");
      out.println("<h1>Activiti Rest Interface</h1>");
      out.println("<p>This page documents the rest interface of Activiti</p>");
      
      Map<String, RestHandler> restHandlers = new HashMap<String, RestHandler>();
      restHandlers.putAll(staticHandlers);
      for (UrlMatcher urlMatcher: dynamicHandlers) {
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
  
  public Activiti getActiviti() {
    return activiti;
  }
  
  public void setActiviti(Activiti activiti) {
    this.activiti = activiti;
  }

  
  public Map<String, RestHandler> getStaticHandlers() {
    return staticHandlers;
  }

  
  public List<UrlMatcher> getDynamicHandlers() {
    return dynamicHandlers;
  }
}
