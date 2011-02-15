package org.activiti.cycle.impl.db;

import java.util.List;

import org.activiti.cycle.impl.db.entity.ProcessSolutionEntity;
import org.activiti.cycle.impl.db.entity.VirtualRepositoryFolderEntity;

/**
 * DAO for process solutions
 * 
 * @author daniel.meyer@camunda.com
 */
public interface CycleProcessSolutionDao {

  public ProcessSolutionEntity getProcessSolutionById(String id);

  public void deleteProcessSolutionById(String id);

  public List<ProcessSolutionEntity> getProcessSolutionList();

  public VirtualRepositoryFolderEntity getVirtualRepositoryFolderById(String id);

  public List<VirtualRepositoryFolderEntity> getVirtualForldersByProcessSolutionId(String id);

  public ProcessSolutionEntity saveProcessSolution(ProcessSolutionEntity processSolution);

  public List<VirtualRepositoryFolderEntity> addVirtualFoldersToSolution(String id, List<VirtualRepositoryFolderEntity> folders);

  public void deleteVirtualRepositoryFolderById(String id);
  
}
