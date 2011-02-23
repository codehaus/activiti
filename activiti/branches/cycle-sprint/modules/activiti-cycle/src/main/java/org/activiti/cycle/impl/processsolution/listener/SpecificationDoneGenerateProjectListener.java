package org.activiti.cycle.impl.processsolution.listener;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.cycle.CycleComponentFactory;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.RepositoryNodeCollection;
import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.event.CycleCompensatingEventListener;
import org.activiti.cycle.impl.components.CycleEmailDispatcher;
import org.activiti.cycle.impl.components.RuntimeConnectorList;
import org.activiti.cycle.impl.connector.signavio.action.CreateMavenProjectAction;
import org.activiti.cycle.impl.connector.signavio.repositoryartifacttype.SignavioBpmn20ArtifactType;
import org.activiti.cycle.impl.processsolution.event.SpecificationDoneEvent;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;
import org.activiti.cycle.service.CycleProcessSolutionService;
import org.activiti.cycle.service.CycleRepositoryService;
import org.activiti.cycle.service.CycleServiceFactory;
import org.activiti.engine.identity.User;

/**
 * {@link CycleCompensatingEventListener} generating a technical project-stub
 * (in the first iteration) and updating an existing technical project in
 * subsequent iterations.
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class SpecificationDoneGenerateProjectListener implements CycleCompensatingEventListener<SpecificationDoneEvent> {

  private CycleProcessSolutionService processSolutionService = CycleServiceFactory.getProcessSolutionService();
  private CycleRepositoryService repositoryservice = CycleServiceFactory.getRepositoryService();

  public void onEvent(SpecificationDoneEvent event) {
    ProcessSolution processSolution = event.getProcessSolution();
    VirtualRepositoryFolder implementationFolder = getImplementationFolder(processSolution);
    if (implementationFolder == null) {
      return;
    }
    RepositoryFolder underlyingFolder = repositoryservice
            .getRepositoryFolder(implementationFolder.getConnectorId(), implementationFolder.getReferencedNodeId());

    if (repositoryservice.getChildren(underlyingFolder.getConnectorId(), underlyingFolder.getNodeId()).asList().size() > 0) {
      updateProject(processSolution, underlyingFolder);
    } else {
      Map<RepositoryArtifact, RepositoryArtifact> processesMappedToBpmnXml = createProject(processSolution, underlyingFolder);
      sendEmailCreated(processSolution, processesMappedToBpmnXml);
    }

  }

  protected void updateProject(ProcessSolution processSolution, RepositoryFolder underlyingTechnicalFolder) {

  }

  protected Map<RepositoryArtifact, RepositoryArtifact> createProject(ProcessSolution processSolution, RepositoryFolder underlyingTechnicalFolder) {
    VirtualRepositoryFolder processes = getProcessesFolder(processSolution);
    // get all processmodels in the processes folder
    List<RepositoryArtifact> processModels = getProcessModels(processes);
    // configure parameters for CreateMavenProjectAction
    CreateMavenProjectAction createMavenProjectAction = new CreateMavenProjectAction();
    RepositoryConnector targetConnector = CycleComponentFactory.getCycleComponentInstance(RuntimeConnectorList.class, RuntimeConnectorList.class)
            .getConnectorById(processes.getConnectorId());
    String targetFolderId = underlyingTechnicalFolder.getNodeId();
    String targetName = processSolution.getLabel();
    String comment = "";
    boolean createLink = true;
    // create the technical project
    return createMavenProjectAction.createMavenProject(targetFolderId, targetName, comment, targetConnector, createLink, processModels);
  }

  protected List<RepositoryArtifact> getProcessModels(VirtualRepositoryFolder processes) {
    RepositoryConnector connector = CycleComponentFactory.getCycleComponentInstance(RuntimeConnectorList.class, RuntimeConnectorList.class).getConnectorById(
            processes.getConnectorId());
    List<RepositoryArtifact> resultList = new ArrayList<RepositoryArtifact>();
    String currentFolder = processes.getReferencedNodeId();
    getProcessModelsRec(connector, currentFolder, resultList);
    return resultList;
  }

  private void getProcessModelsRec(RepositoryConnector connector, String currentFolder, List<RepositoryArtifact> resultList) {
    RepositoryNodeCollection childNodes = connector.getChildren(currentFolder);
    for (RepositoryArtifact repositoryArtifact : childNodes.getArtifactList()) {
      if (repositoryArtifact.getArtifactType().equals(CycleComponentFactory.getCycleComponentInstance(SignavioBpmn20ArtifactType.class))) {
        resultList.add(repositoryArtifact);
      }
    }
    for (RepositoryFolder folder : childNodes.getFolderList()) {
      getProcessModelsRec(connector, folder.getNodeId(), resultList);
    }
  }

  protected VirtualRepositoryFolder getImplementationFolder(ProcessSolution processSolution) {
    // TODO: add dedicate query for this
    List<VirtualRepositoryFolder> virtualFolders = processSolutionService.getFoldersForProcessSolution(processSolution.getId());
    for (VirtualRepositoryFolder virtualRepositoryFolder : virtualFolders) {
      if ("Implementation".equals(virtualRepositoryFolder.getType())) {
        return virtualRepositoryFolder;
      }
    }
    return null;
  }

  protected VirtualRepositoryFolder getProcessesFolder(ProcessSolution processSolution) {
    // TODO: add dedicate query for this
    List<VirtualRepositoryFolder> virtualFolders = processSolutionService.getFoldersForProcessSolution(processSolution.getId());
    for (VirtualRepositoryFolder virtualRepositoryFolder : virtualFolders) {
      if ("Processes".equals(virtualRepositoryFolder.getType())) {
        return virtualRepositoryFolder;
      }
    }
    return null;
  }

  protected void sendEmailCreated(ProcessSolution processSolution, Map<RepositoryArtifact, RepositoryArtifact> processesMappedToBpmnXml) {
    CycleEmailDispatcher cycleEmailDispatcher = CycleComponentFactory.getCycleComponentInstance(CycleEmailDispatcher.class, CycleEmailDispatcher.class);
    for (User user : processSolutionService.getProcessSolutionCollaborators(processSolution.getId(), null)) {
      StringWriter writer = new StringWriter();
      writer.append("Hi " + user.getFirstName() + " " + user.getLastName() + ", <br /><br />");
      writer.append("Technical implementation models for the process solution " + processSolution.getLabel() + " have been created: <br />");
      writer.append("<ul>");
      for (Entry<RepositoryArtifact, RepositoryArtifact> processMappedToBpmnXml : processesMappedToBpmnXml.entrySet()) {
        writer.append("<li>");
        writer.append("The bpmn20.xml file for the process ");
        writer.append(processMappedToBpmnXml.getKey().getMetadata().getName());
        writer.append(" is located in ");
        writer.append(processMappedToBpmnXml.getValue().getNodeId() + ".");
        writer.append("</li>");
      }
      writer.append("</ul>");
      cycleEmailDispatcher.sendEmail("activiti-cycle@localhost", user.getEmail(), "Technical Model created", writer.toString());
    }
  }

  public void compensateEvent(SpecificationDoneEvent event) {

  }

}
