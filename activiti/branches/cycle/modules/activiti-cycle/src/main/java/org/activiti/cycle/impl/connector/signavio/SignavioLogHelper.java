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
package org.activiti.cycle.impl.connector.signavio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.util.Series;

/**
 * 
 * @author christian.lipphardt@camunda.com
 */
public class SignavioLogHelper {

	public static void logCookies(Logger log, Series<CookieSetting> cookies) {
		if (cookies != null && !cookies.isEmpty()) {
			Map<String, String> cookieVals = cookies.getValuesMap();
			for (Iterator<Entry<String, String>> it = cookieVals.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
			  log.info("Cookie - (" + entry.getKey() + " = " + entry.getValue() + ")");
			}
		}
	}

	public static void logFormAttributes(Logger log, Map attributes) {
	  org.restlet.data.Form formAttributes = (Form) attributes.get("org.restlet.http.headers");
	  if (formAttributes != null) {
	    try {
	      log.info("FormAttributes (URLDecoded) - " + URLDecoder.decode(formAttributes.getMatrixString(), "UTF-8"));
	    } catch (UnsupportedEncodingException e) {
	      // do nothing because utf-8 is everywhere... ;)
	    }
	  } else {
	    log.info("FormAttributes (URLDecoded) - NONE");
	  }
	}
	
	public static void logJSONArray(Logger log, JSONArray jsonArray) {
		try {
			log.info(jsonArray.toString(2));
		} catch (JSONException je) {
		  log.log(Level.SEVERE, "JSONException while trying to log JSONArray", je);
		}
	}
	
	public static void logHttpRequest(Logger log, Request request) {
	  log.info("----- Start HttpRequest -----");
	  log.info("Method - (" + request.getMethod() + ")");
	  log.info("ResourceRef - (" + request.getResourceRef() + ")");
	  if (request.getEntity() != null && request.getEntity().isAvailable()) {
  	  try {
        log.info("Body - (" + request.getEntity().getText() + ")");
      } catch (IOException e1) {
        e1.printStackTrace();
      }
	  }
	  if (request.getAttributes() != null) {
      logFormAttributes(log, request.getAttributes());
    }
	  log.info("----- End HttpRequest -----");
	}

	public static void logHttpResponse(Logger log, Response response) {
	  log.info("----- Start HttpResponse -----");
	  if (response != null) {
	    if (response.getStatus() != null) {
	      log.info("HttpStatus - (" + response.getStatus() + ")");
	    }
	    if (response.getAttributes() != null) {
    	  logFormAttributes(log, response.getAttributes());
	    }
  	  if (response.getCookieSettings() != null) {
  	    logCookies(log, response.getCookieSettings());
  	  }
  	  if (response.getEntity() != null && response.getEntity().isAvailable()) {
  	    // TODO: how to log the response's body if it is transient???
//        try {
          Representation requestRepresentation = response.getEntity();
//          log.fine("Body - (" + requestRepresentation.getText() + ")");
//        } catch (IOException e1) {
//          e1.printStackTrace();
//        }
      }
  	  if (response.getLocationRef() != null) {
  	    log.info("Location - " + response.getLocationRef());
  	  }
  	  if (response.getServerInfo() != null) {
  	    log.info("ServerInfo - (Address: " + response.getServerInfo().getAddress() + 
  	            ", Agent: " + response.getServerInfo().getAgent() + 
  	            ", Port: " + response.getServerInfo().getPort() + ")");
  	  }
	  } else {
	    log.info("HttpResponse is NULL!");
	  }
	  log.info("----- End HttpResponse -----");
	}

}
