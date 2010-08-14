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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentation;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNode;
import org.activiti.cycle.impl.RepositoryRegistry;
import org.activiti.cycle.impl.connector.signavio.action.OpenModelerAction;
import org.activiti.cycle.impl.connector.signavio.provider.Bpmn20Provider;
import org.activiti.cycle.impl.connector.signavio.provider.EmbeddableModelProvider;
import org.activiti.cycle.impl.connector.signavio.provider.Jpdl4Provider;
import org.activiti.cycle.impl.connector.signavio.provider.JsonProvider;
import org.activiti.cycle.impl.connector.signavio.provider.PngProvider;
import org.activiti.cycle.impl.connector.signavio.util.SignavioJsonHelper;
import org.activiti.cycle.impl.connector.signavio.util.SignavioLogHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

/**
 * TODO: refactor to differentiate between enterprise and os signavio.
 * 
 * @author christian.lipphardt@camunda.com
 */
public class SignavioConnector implements RepositoryConnector {

  private static Logger log = Logger.getLogger(SignavioConnector.class.getName());

  // register Signavio stencilsets to identify file types
  public static final String SIGNAVIO_BPMN_2_0 = "http://b3mn.org/stencilset/bpmn2.0#";
  public static final String SIGNAVIO_BPMN_JBPM4 = "http://b3mn.org/stencilset/jbpm4#";

  public static final String BPMN_2_0_XML = "bpm2.0";

  static {
    // initialize associated file types
    RepositoryRegistry.registerArtifactType(new ArtifactType("Signavio BPMN 2.0", SIGNAVIO_BPMN_2_0));
    RepositoryRegistry.registerArtifactType(new ArtifactType("Signavio BPMN for jBPM 4", SIGNAVIO_BPMN_JBPM4));

    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_2_0, Bpmn20Provider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_2_0, JsonProvider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_2_0, PngProvider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_2_0, EmbeddableModelProvider.class);

    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_JBPM4, JsonProvider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_JBPM4, Jpdl4Provider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_JBPM4, PngProvider.class);
    RepositoryRegistry.registerContentRepresentationProvider(SIGNAVIO_BPMN_JBPM4, EmbeddableModelProvider.class);

    RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_2_0, OpenModelerAction.class);
    // RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_2_0,
    // CopyBpmn20ToSvnAction.class);

    RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_JBPM4, OpenModelerAction.class);
    // RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_JBPM4,
    // CopyJpdl4ToSvnAction.class);
    // RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_JBPM4,
    // CreateJbpm4AntProject.class);

    // RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_2_0,
    // ShowModelViewerAction.class);
    // RepositoryRegistry.registerArtifactAction(SIGNAVIO_BPMN_JBPM4,
    // ShowModelViewerAction.class);

    // TODO: Retrieve model through modellink (without /info) and dynamically
    // initialize RepositoryRegistry with supported formats?
  }

  /**
   * Captcha ID for REST access to Signavio
   */
  private static final String SERVER_SECURITY_ID = "000000";

  /**
   * Security token obtained from Signavio after login for the current session
   */
  private String securityToken = "";

  private transient Client restletClient;

  private final SignavioConnectorConfiguration conf;

  public SignavioConnector(SignavioConnectorConfiguration signavioConfiguration) {
    this.conf = signavioConfiguration;
  }

  public Client initClient() {
    // TODO: Check timeout on client and re-create it

    if (restletClient == null) {
      // Create and initialize HTTP client for HTTP REST API calls
      restletClient = new Client(new Context(), Protocol.HTTP);
      restletClient.getContext().getParameters().add("converter", "com.noelios.restlet.http.HttpClientConverter");
    }
    return restletClient;
  }

  public Response sendRequest(Request request) throws IOException {
    injectSecurityToken(request);

    if (log.isLoggable(Level.INFO)) {
      SignavioLogHelper.logHttpRequest(log, Level.INFO, request);
    }

    Client client = initClient();
    Response response = client.handle(request);

    if (log.isLoggable(Level.INFO)) {
      SignavioLogHelper.logHttpResponse(log, Level.INFO, response);
    }

    if (response.getStatus().isSuccess()) {
      return response;
    }

    throw new RepositoryException("Encountered error while retrieving response (HttpStatus: " + response.getStatus() + ", Body: "
            + response.getEntity().getText() + ")");
  }

  private Request injectSecurityToken(Request request) {
    Form requestHeaders = new Form();
    requestHeaders.add("token", getSecurityToken());
    request.getAttributes().put("org.restlet.http.headers", requestHeaders);

    return request;
  }

  public Response getJsonResponse(String url) throws IOException {
    Request jsonRequest = new Request(Method.GET, new Reference(url));
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));

    return sendRequest(jsonRequest);
  }

  public boolean registerUserWithSignavio(String firstname, String lastname, String email, String password) throws IOException {
    // Create the Post Parameters for registering a new user
    Form registrationForm = new Form();
    registrationForm.add("mode", "external");
    registrationForm.add("firstName", firstname);
    registrationForm.add("lastName", lastname);
    registrationForm.add("mail", email);
    registrationForm.add("password", password);
    registrationForm.add("serverSecurityId", SERVER_SECURITY_ID);
    Representation registrationRep = registrationForm.getWebRepresentation();

    Request registrationRequest = new Request(Method.POST, conf.getRegistrationUrl(), registrationRep);
    Response registrationResponse = sendRequest(registrationRequest);

    if (registrationResponse.getStatus().equals(Status.SUCCESS_CREATED)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean login(String username, String password) {
    try {
      log.info("Logging into Signavio on url: " + conf.getLoginUrl());

      // Login a user
      Form loginForm = new Form();
      loginForm.add("name", username);
      loginForm.add("password", password);
      loginForm.add("tokenonly", "true");
      Representation loginRep = loginForm.getWebRepresentation();

      Request loginRequest = new Request(Method.POST, conf.getLoginUrl(), loginRep);
      Response loginResponse = sendRequest(loginRequest);

      if (loginResponse.getStatus().isSuccess()) {
        Representation representation = loginResponse.getEntity();
        setSecurityToken(representation.getText());
        log.fine("SecurityToken: " + getSecurityToken());
        // if (getSecurityToken() != null && getSecurityToken().length() > 0) {
        // return true;
        // }

        return true;
      }

      return false;
    } catch (Exception ex) {
      throw new RepositoryException("Error during login to Signavio", ex);
    }
  }

  public JSONObject getPublicRootDirectory() throws IOException, JSONException {
    Response directoryResponse = getJsonResponse(conf.getDirectoryUrl());
    JsonRepresentation jsonData = new JsonRepresentation(directoryResponse.getEntity());
    JSONArray rootJsonArray = jsonData.toJsonArray();

    if (log.isLoggable(Level.FINEST)) {
      SignavioLogHelper.logJSONArray(log, Level.FINEST, rootJsonArray);
    }

    // find the directory of type public which contains all directories and
    // models of this account
    for (int i = 0; i < rootJsonArray.length(); i++) {
      JSONObject rootObject = rootJsonArray.getJSONObject(i);
      if ("public".equals(rootObject.getJSONObject("rep").get("type"))) {
        return rootObject;
      }
    }

    throw new RepositoryException("No directory root found in signavio repository.");
  }

  private RepositoryFolder getFolderInfo(JSONObject jsonDirectoryObject) throws JSONException {
    if (!"dir".equals(jsonDirectoryObject.getString("rel"))) {
      // TODO: Think about that!
      throw new RepositoryException(jsonDirectoryObject + " is not a directory");
    }
    String directoryName = jsonDirectoryObject.getJSONObject("rep").getString("name");
    log.finest("Directoryname: " + directoryName);

    // String directoryDescription =
    // jsonDirectoryObject.getJSONObject("rep").getString("description");
    String href = jsonDirectoryObject.getString("href");

    // for (JSONObject subDirectoryInfo : getSubDirectoryInfos(href)) {
    // printDirectory(subDirectoryInfo, indention);
    // }
    //
    // for (JSONObject modelInfo : getSubModelInfos(href)) {
    // String modelName = modelInfo.getJSONObject("rep").getString("name");
    // String modelType = modelInfo.getJSONObject("rep").getString("type");
    // System.out.println(indention + "- MODEL " + modelName + " (" + modelType
    // + ")");
    // }

    RepositoryFolder folderInfo = new RepositoryFolder(this);
    // folderInfo.setId( directoryId );
    // TODO: Check where we get the real ID from!
    folderInfo.setId(conf.getDirectoryIdFromUrl(href));

    folderInfo.getMetadata().setName(directoryName);
    // TODO: Where do we get the path from?
    // folderInfo.getMetadata().setPath();

    return folderInfo;
  }

  private RepositoryArtifact getArtifactInfoFromFolderLink(JSONObject json) throws JSONException {
    String id = conf.getModelIdFromUrl(json.getString("href"));
    return getArtifactInfoFromFile(id, json.getJSONObject("rep"));
  }

  private RepositoryArtifact getArtifactInfoFromFile(String id, JSONObject json) throws JSONException {
    RepositoryArtifact fileInfo = new RepositoryArtifact(this);
    fileInfo.setId(id);

    fileInfo.getMetadata().setName(SignavioJsonHelper.getValueIfExists(json, "name"));
    // TODO: This seems not to work 100% correctly
    fileInfo.getMetadata().setVersion(SignavioJsonHelper.getValueIfExists(json, "rev"));

    // TODO: Check if that is really last author and if we can get the original
    // author
    fileInfo.getMetadata().setLastAuthor(SignavioJsonHelper.getValueIfExists(json, "author"));
    fileInfo.getMetadata().setCreated(SignavioJsonHelper.getDateValueIfExists(json, "created"));
    fileInfo.getMetadata().setLastChanged(SignavioJsonHelper.getDateValueIfExists(json, "updated"));

    if (json.has("namespace")) {
      // Signavio way of doing it
      String fileTypeIdentifier = json.getString("namespace");
      fileInfo.setArtifactType(RepositoryRegistry.getArtifactTypeByIdentifier(fileTypeIdentifier));
    } else {
      // Oryx way of doing it
      String fileTypeIdentifier = json.getString("type");
      fileInfo.setArtifactType(RepositoryRegistry.getArtifactTypeByIdentifier(fileTypeIdentifier));
    }

    // relObject.getJSONObject("rep").getString("revision"); --> UUID of
    // revision
    // relObject.getJSONObject("rep").getString("description");
    // relObject.getJSONObject("rep").getString("comment");
    return fileInfo;

    // TODO: Add file actions here (jpdl4/bpmn20/png), maybe define an action
    // factory which produces the concrete actions?
  }

  public RepositoryFolder getRootFolder() {
    try {
      return getFolderInfo(getPublicRootDirectory());
    } catch (Exception e) {
      throw new RepositoryException("Error while accessing the Signavio Repository", e);
    }
  }

  public List<RepositoryNode> getChildNodes(String parentId) {
    try {
      Response directoryResponse = getJsonResponse(conf.getDirectoryUrl() + parentId);
      JsonRepresentation jsonData = new JsonRepresentation(directoryResponse.getEntity());
      JSONArray relJsonArray = jsonData.toJsonArray();

      if (log.isLoggable(Level.FINEST)) {
        SignavioLogHelper.logJSONArray(log, Level.FINEST, relJsonArray);
      }

      ArrayList<RepositoryNode> nodes = new ArrayList<RepositoryNode>();
      for (int i = 0; i < relJsonArray.length(); i++) {
        JSONObject relObject = relJsonArray.getJSONObject(i);

        if ("dir".equals(relObject.getString("rel"))) {
          RepositoryFolder folderInfo = getFolderInfo(relObject);
          nodes.add(folderInfo);
        } else if ("mod".equals(relObject.getString("rel"))) {
          RepositoryArtifact fileInfo = getArtifactInfoFromFolderLink(relObject);
          nodes.add(fileInfo);
        }
      }
      return nodes;
    } catch (Exception ex) {
      throw new RepositoryException("Exception while accessing Signavio repository", ex);
    }
  }

  public ContentRepresentation getContent(String nodeId, String representationName) {
    RepositoryArtifact artifact = getArtifactDetails(nodeId);
    return RepositoryArtifact.getContentRepresentation(artifact, representationName);
  }

  public RepositoryArtifact getArtifactDetails(String id) {
    JsonRepresentation jsonData = null;
    JSONObject jsonObject = null;

    try {
      Response modelResponse = getJsonResponse(getModelUrl(id) + "/info");
      jsonData = new JsonRepresentation(modelResponse.getEntity());
      if (log.isLoggable(Level.FINE)) {
        log.fine("JsonData - (" + jsonData.getText() + ")");
      }
      jsonObject = jsonData.toJsonObject();
      return getArtifactInfoFromFile(id, jsonObject);
    } catch (IOException ioe) {
      throw new RepositoryException("IOException while accessing Signavio repository", ioe);
    } catch (JSONException je) {
      throw new RepositoryException("Encountered error in JSON data while accessing Signavio repository", je);
    }
  }

  public String getSecurityToken() {
    return securityToken;
  }

  public void setSecurityToken(String securityToken) {
    this.securityToken = securityToken;
  }

  public SignavioConnectorConfiguration getSignavioConfiguration() {
    return conf;
  }


  public void createNewArtifact(String folderId, RepositoryArtifact artifact, ContentRepresentation representation) {
    // TODO: how to get values for jsonData, comment and description
    // createNewModel(folderId, file.getMetadata().getName(), jsonData, "", "");
  }

  public void createNewSubFolder(String parentFolderId, RepositoryFolder subFolder) {
    Form createFolderForm = new Form();
    createFolderForm.add("name", subFolder.getMetadata().getName());
    createFolderForm.add("description", ""); // TODO: what should we use here?
    createFolderForm.add("parent", "/directory/" + parentFolderId);
    Representation createFolderRep = createFolderForm.getWebRepresentation();

    Request jsonRequest = new Request(Method.POST, new Reference(conf.getDirectoryUrl()), createFolderRep);
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = null;

    try {
      jsonResponse = sendRequest(jsonRequest);
    } catch (IOException ioe) {
      log.log(Level.SEVERE, "IOException while sending request", ioe);
    }

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to create subFolder " + subFolder);
  }

  public void deleteArtifact(String artifactId) {
    Request jsonRequest = new Request(Method.DELETE, new Reference(conf.getModelUrl() + artifactId));
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = null;

    try {
      jsonResponse = sendRequest(jsonRequest);
    } catch (IOException ioe) {
      log.log(Level.SEVERE, "IOException while sending request", ioe);
    }

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to delete model " + artifactId);
  }

  public void deleteSubFolder(String subFolderId) {
    Request jsonRequest = new Request(Method.DELETE, new Reference(conf.getDirectoryUrl() + subFolderId));
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = null;

    try {
      jsonResponse = sendRequest(jsonRequest);
    } catch (IOException ioe) {
      log.log(Level.SEVERE, "IOException while sending request", ioe);
    }

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to delete directory " + subFolderId);
  }

  public String getModelUrl(RepositoryArtifact artifact) {
    return getModelUrl(artifact.getId());
  }
  public String getModelUrl(String artifactId) {
    return getSignavioConfiguration().getModelUrl() + artifactId;
  }

  public void commitPendingChanges(String comment) {
  }

  public void moveModel(String targetFolderId, String modelId) throws IOException {
    Form bodyForm = new Form();
    bodyForm.add("parent", "/directory/" + targetFolderId);
    Representation bodyRep = bodyForm.getWebRepresentation();

    Request jsonRequest = new Request(Method.PUT, new Reference(conf.getModelUrl() + modelId), bodyRep);
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = sendRequest(jsonRequest);

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to move model " + modelId + " to folder " + targetFolderId);
  }

  public void moveDirectory(String targetFolderId, String directoryId) throws IOException {
    Form bodyForm = new Form();
    bodyForm.add("parent", "/directory/" + targetFolderId);
    Representation bodyRep = bodyForm.getWebRepresentation();

    Request jsonRequest = new Request(Method.PUT, new Reference(conf.getDirectoryUrl() + directoryId), bodyRep);
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = sendRequest(jsonRequest);

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to move folder " + directoryId + " to directory " + targetFolderId);
  }

  public void createNewModel(String parentFolderId, String name, String jsonData, String comment, String description) throws IOException {
    try {
      // do this to check if jsonString is valid
      JSONObject jsonModel = new JSONObject(jsonData);

      Form modelForm = new Form();
      modelForm.add("comment", comment);
      modelForm.add("description", description);
      modelForm.add("glossary_xml", new JSONArray().toString());
      // modelForm.add("id", null);
      modelForm.add("json_xml", jsonModel.toString());
      modelForm.add("name", name);
      modelForm.add("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
      modelForm.add("parent", "/directory/" + parentFolderId);
      modelForm
              .add("svg_xml",
                      "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:oryx=\"http://oryx-editor.org\" id=\"sid-80D82B67-3B30-4B35-A6CB-16EEE17A719F\" width=\"50\" height=\"50\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:svg=\"http://www.w3.org/2000/svg\"><defs/><g stroke=\"black\" font-family=\"Verdana, sans-serif\" font-size-adjust=\"none\" font-style=\"normal\" font-variant=\"normal\" font-weight=\"normal\" line-heigth=\"normal\" font-size=\"12\"><g class=\"stencils\" transform=\"translate(25, 25)\"><g class=\"me\"/><g class=\"children\"/><g class=\"edge\"/></g></g></svg>");
      modelForm.add("type", "BPMN 2.0");
      // modelForm.add("views", new JSONArray().toString());
      Representation modelRep = modelForm.getWebRepresentation();

      Request jsonRequest = new Request(Method.POST, new Reference(conf.getModelUrl()), modelRep);
      jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
      Response jsonResponse = sendRequest(jsonRequest);

      if (jsonResponse.getStatus().isSuccess()) {
        return;
      }

      throw new RepositoryException("Unable to create model");
    } catch (JSONException je) {
      throw new RepositoryException("Unable to create model", je);
    }
  }

  public void restoreRevisionOfModel(String parentFolderId, String modelId, String revisionId, String comment) throws IOException {
    Form reivisionForm = new Form();
    reivisionForm.add("comment", comment);
    reivisionForm.add("parent", "/directory/" + parentFolderId);
    reivisionForm.add("revision", revisionId);
    Representation modelRep = reivisionForm.getWebRepresentation();

    Request jsonRequest = new Request(Method.PUT, new Reference(conf.getModelUrl()), modelRep);
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    Response jsonResponse = sendRequest(jsonRequest);

    if (jsonResponse.getStatus().isSuccess()) {
      return;
    }

    throw new RepositoryException("Unable to restore revision " + revisionId + " of model " + modelId + " in directory " + parentFolderId);
  }
}
