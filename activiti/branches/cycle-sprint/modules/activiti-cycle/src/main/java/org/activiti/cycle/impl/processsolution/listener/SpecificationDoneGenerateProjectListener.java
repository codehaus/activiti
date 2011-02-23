package org.activiti.cycle.impl.processsolution.listener;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.event.CycleCompensatingEventListener;
import org.activiti.cycle.impl.processsolution.event.SpecificationDoneEvent;

/**
 * {@link CycleCompensatingEventListener} generating a technical project-stub
 * (in the first iteration) and updating an existing technical project in
 * subsequent iterations.
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class SpecificationDoneGenerateProjectListener implements CycleCompensatingEventListener<SpecificationDoneEvent> {

  public void onEvent(SpecificationDoneEvent event) {

  }

  public void compensateEvent(SpecificationDoneEvent event) {

  }

}
