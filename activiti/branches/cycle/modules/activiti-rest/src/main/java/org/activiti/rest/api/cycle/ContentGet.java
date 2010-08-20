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
package org.activiti.rest.api.cycle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * @author Nils Preusker
 */
public class ContentGet extends AbstractWebScript {

  public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

    // Retrieve the artifactId from the request
    String artifactId = req.getParameter("artifactId");
    if (artifactId == null || artifactId.length() == 0) {
      throw new RuntimeException("Missing required parameter: artifactId");
    }

    // Retrieve session and repo connector
    String cuid = getCurrentUserId(req);
    HttpSession session = ((WebScriptServletRequest) req).getHttpServletRequest().getSession(true);
    RepositoryConnector conn = SessionUtil.getRepositoryConnector(cuid, session);

    // Retrieve the artifact from the repository
    RepositoryArtifact artifact = conn.getArtifactDetails(artifactId);

    Collection<ContentRepresentationDefinition> representations = artifact.getContentRepresentationDefinitions();
    for (ContentRepresentationDefinition representation : representations) {
      if (representation.getType().equals("img")) {
        byte[] content = conn.getContent(artifact.getId(), representation.getName()).asByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(content);
        streamContentImpl(req, res, in, false, new Date(0), "W/\"647-1281077702000\"", null);
      }
    }

  }

  protected void streamContentImpl(WebScriptRequest req, WebScriptResponse res, InputStream in, boolean attach, Date modified, String eTag,
          String attachFileName) throws IOException {
    setAttachment(res, attach, attachFileName);

    // establish mimetype
    String mimetype = "image/png"; // TODO: create a mimetype map and
    // a utility to determine the
    // mimetypes
    // String extensionPath = req.getExtensionPath();
    // if (mimetype == null || mimetype.length() == 0) {
    // int extIndex = extensionPath.lastIndexOf('.');
    // if (extIndex != -1) {
    // String ext = extensionPath.substring(extIndex + 1);
    // }
    // }

    // set mimetype for the content and the character encoding + length for the
    // stream
    res.setContentType(mimetype);
    // res.setContentEncoding(reader.getEncoding());

    // res.setHeader("Content-Length", Long.toString(reader.getSize()));

    // set caching
    Cache cache = new Cache();
    cache.setNeverCache(false);
    cache.setMustRevalidate(true);
    cache.setMaxAge(0L);
    cache.setLastModified(modified);
    cache.setETag(eTag);
    res.setCache(cache);

    // get the content and stream directly to the response output stream
    // assuming the repository is capable of streaming in chunks, this should
    // allow large files
    // to be streamed directly to the browser response stream.
    try {
      byte[] buffer = new byte[0xFFFF];
      for (int len; (len = in.read(buffer)) != -1;)
        res.getOutputStream().write(buffer, 0, len);
    } catch (SocketException e1) {
      // the client cut the connection - our mission was accomplished apart from
      // a little error message
      // if (logger.isInfoEnabled())
      // logger.info("Client aborted stream read:\n\tcontent: " + reader);
    }
    // catch (ContentIOException e2)
    // {
    // if (logger.isInfoEnabled())
    // logger.info("Client aborted stream read:\n\tcontent: " + reader);
    // }
  }

  /**
   * Set attachment header
   * 
   * @param res
   * @param attach
   * @param attachFileName
   */
  protected void setAttachment(WebScriptResponse res, boolean attach, String attachFileName) {
    if (attach == true) {
      String headerValue = "attachment";
      if (attachFileName != null && attachFileName.length() > 0) {
        // if (logger.isDebugEnabled())
        // logger.debug("Attaching content using filename: " + attachFileName);

        headerValue += "; filename=" + attachFileName;
      }

      // set header based on filename - will force a Save As from the browse if
      // it doesn't recognize it
      // this is better than the default response of the browser trying to
      // display the contents
      res.setHeader("Content-Disposition", headerValue);
    }
  }

  /**
   * Returns the username for the current user.
   * 
   * @param req
   *          The webscript request
   * @return THe username of the current user
   */
  protected String getCurrentUserId(WebScriptRequest req) {
    String authorization = req.getHeader("Authorization");
    if (authorization != null) {
      String[] parts = authorization.split(" ");
      if (parts.length == 2) {
        return new String(Base64.decode(parts[1])).split(":")[0];
      }
    }
    return null;
  }

}
