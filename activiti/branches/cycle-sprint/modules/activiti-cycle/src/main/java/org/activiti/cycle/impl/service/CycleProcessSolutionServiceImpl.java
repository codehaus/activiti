package org.activiti.cycle.impl.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.cycle.impl.db.CycleProcessSolutionDao;
import org.activiti.cycle.processsolution.ProcessSolution;
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

  public void createNewProcessSolution() {
    // todo
  }

  public List<VirtualRepositoryFolder> getFoldersForProcessSolution(String id) {
    return new ArrayList<VirtualRepositoryFolder>(dao.getVirtualForldersByProcessSolutionId(id));
  }

  public void setDao(CycleProcessSolutionDao dao) {
    this.dao = dao;
  }

}
