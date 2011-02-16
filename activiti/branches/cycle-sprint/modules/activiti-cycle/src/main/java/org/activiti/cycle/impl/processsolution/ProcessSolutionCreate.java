package org.activiti.cycle.impl.processsolution;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;

/**
 * Cycle Component for creating new ProcessSolutions
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(name = "processSolutionCreate", context = CycleContextType.REQUEST)
public class ProcessSolutionCreate {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
