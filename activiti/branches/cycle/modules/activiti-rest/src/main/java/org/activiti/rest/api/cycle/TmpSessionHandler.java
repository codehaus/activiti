package org.activiti.rest.api.cycle;

import java.util.HashMap;
import java.util.Map;

import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.impl.RestClientRepositoryConnector;
import org.activiti.cycle.impl.connector.demo.DemoConnector;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class TmpSessionHandler {

  // TODO
  // This should be done with the HTTPSession of the web application.
  // This static map is just a temporary HTTPSession replacement until we know
  // how to get the HTTPSession.
  private static Map<String, HashMap<String, Object>> mySessions;

  static {
    if (mySessions == null) {
      mySessions = new HashMap<String, HashMap<String, Object>>();
    }
  }

  /**
   * Returns the session of the user identified by userId or creates a new
   * session if no session is found in the map.
   * 
   * @param userId the id of the user who's session shall be returned
   * @return a session object for the user identified by userId
   */
  public static Map<String, Object> getSessionByUserId(String userId) {
    // Look for session in mySessions map
    for (String key : mySessions.keySet()) {
      if (key.equals(userId)) {
        return mySessions.get(key);
      }
    }
    // Create session if no instance could be found in the mySessions map.
    HashMap<String, Object> mySession = new HashMap<String, Object>();
    mySessions.put(userId, mySession);
    return mySession;
  }

  public static RepositoryConnector getRepositoryConnector(WebScriptRequest req, Map<String, Object> mySession) {
    RepositoryConnector conn = (RepositoryConnector)mySession.get("repoConnector");
    if (conn == null) {
      String contextPath = req.getContextPath();
      // TODO
      // Repository name and connector type should be determined based on a
      // persistent settings object.
      conn = new RestClientRepositoryConnector("demo-repo", contextPath, new DemoConnector());
      // session.setAttribute("conn", conn); // put(cuid, conn);
      mySession.put("repoConnector", conn);
    }
    return conn;
  }
  
}
