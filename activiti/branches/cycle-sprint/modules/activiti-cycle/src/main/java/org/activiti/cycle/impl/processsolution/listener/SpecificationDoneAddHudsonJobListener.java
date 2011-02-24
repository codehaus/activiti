package org.activiti.cycle.impl.processsolution.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.activiti.cycle.RepositoryFolder;
import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.event.CycleCompensatingEventListener;
import org.activiti.cycle.impl.connector.ci.hudson.action.CreateHudsonJob;
import org.activiti.cycle.impl.processsolution.event.SpecificationDoneEvent;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;
import org.activiti.cycle.service.CycleServiceFactory;
import org.activiti.engine.identity.User;


/**
 * {@link CycleCompensatingEventListener} adding the technical project 
 * to the Hudson CI server (in the first iteration) 
 * and doing nothing in subsequent iterations.
 * 
 * <b>EXPERIMENTAL</b>
 * 
 * TODO: Remove hacky implementation extension for reusing stuff of {@link SpecificationDoneGenerateProjectListener}
 * 
 * @author bernd.ruecker@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class SpecificationDoneAddHudsonJobListener extends SpecificationDoneGenerateProjectListener implements CycleCompensatingEventListener<SpecificationDoneEvent> {

  public final String METADATA_SVN_URL = "svnUrl";

  protected static Logger log = Logger.getLogger(SpecificationDoneAddHudsonJobListener.class.getName());
  
  public final String HUDSON_URL = "http://localhost/hudson/";
  
  public void onEvent(SpecificationDoneEvent event) {
    ProcessSolution processSolution = event.getProcessSolution();
    VirtualRepositoryFolder implementationFolder = getImplementationFolder(processSolution);
    if (implementationFolder == null) {
      return;
    }

    RepositoryFolder underlyingFolder = CycleServiceFactory.getRepositoryService()
            .getRepositoryFolder(implementationFolder.getConnectorId(), implementationFolder.getReferencedNodeId());

    if (isSvnFolder(underlyingFolder)) {      
      new CreateHudsonJob().createCIJobForProject(HUDSON_URL, getSvnUrl(underlyingFolder), processSolution.getId(), getDeveloperEmails(processSolution));
    }
    else {
      log.warning("Using not supported repository for Hudson in implementation folder: " + underlyingFolder + ". Currently supported are: SVN");
    }
  }

  private List<String> getDeveloperEmails(ProcessSolution processSolution) {
    ArrayList<String> list = new ArrayList<String>();

    for (User user : CycleServiceFactory.getProcessSolutionService().getProcessSolutionCollaborators(processSolution.getId(), null)) {
      // TODO: Select role developer
      list.add(user.getEmail());
    }

    return list;
  }

  private boolean isSvnFolder(RepositoryFolder underlyingFolder) {
    return getSvnUrl(underlyingFolder)!=null;
  }

  private String getSvnUrl(RepositoryFolder underlyingFolder) {
    return underlyingFolder.getMetadata().getMetadata(METADATA_SVN_URL);
  }
}
