package org.activiti.cycle.impl.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.cycle.CycleComponentFactory;
import org.activiti.cycle.impl.db.CycleProcessSolutionDao;
import org.activiti.cycle.impl.processsolution.DefaultProcessSolutionTemplate;
import org.activiti.cycle.impl.processsolution.ProcessSolutionCreate;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.ProcessSolutionTemplate;
import org.activiti.cycle.processsolution.VirtualRepositoryFolder;
import org.activiti.cycle.service.CycleProcessSolutionService;

/**
 * Default implementation of the {@link CycleProcessSolutionService}-interface.
 * 
 * @author daniel.meyer@camunda.com
 */
public class CycleProcessSolutionServiceImpl implements CycleProcessSolutionService {

  protected CycleProcessSolutionDao dao;

  public ProcessSolution getProcessSolutionById(String id) {
    return dao.getProcessSolutionById(id);
  }

  public List<ProcessSolution> getProcessSolutions() {
    return new ArrayList<ProcessSolution>(dao.getProcessSolutionList());
  }

  public void createNewProcessSolution(String name) {
    ProcessSolutionCreate processSolutionCreate = CycleComponentFactory.getCycleComponentInstance("processSolutionCreate", ProcessSolutionCreate.class);
    processSolutionCreate.setName(name);
    processSolutionCreate.createNewProcessSolution();
  }

  public List<VirtualRepositoryFolder> getFoldersForProcessSolution(String id) {
    return new ArrayList<VirtualRepositoryFolder>(dao.getVirtualForldersByProcessSolutionId(id));
  }

  public void setDao(CycleProcessSolutionDao dao) {
    this.dao = dao;
  }

  public VirtualRepositoryFolder getVirtualRepositoryFolderById(String id) {
    return dao.getVirtualRepositoryFolderById(id);
  }

  public ProcessSolutionTemplate getDefaultProcessSolutionTemplate() {
    return CycleComponentFactory.getCycleComponentInstance(DefaultProcessSolutionTemplate.class, DefaultProcessSolutionTemplate.class);
  }

  public CycleProcessSolutionDao getDao() {
    return dao;
  }

}
