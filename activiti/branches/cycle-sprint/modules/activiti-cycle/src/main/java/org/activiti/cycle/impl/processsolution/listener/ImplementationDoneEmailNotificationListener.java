package org.activiti.cycle.impl.processsolution.listener;

import java.io.StringWriter;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.event.CycleEventListener;
import org.activiti.cycle.impl.processsolution.event.ImplementationDoneEvent;

/**
 * Listener sending an email when the implementation for a given process
 * solution is completed.
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class ImplementationDoneEmailNotificationListener extends AbstractProcessSolutionStateEmailListener<ImplementationDoneEvent> implements
        CycleEventListener<ImplementationDoneEvent> {

  protected String getSubject(ImplementationDoneEvent event) {
    return "Implementation done in " + event.getProcessSolution().getLabel();
  }

  protected String getMessage(ImplementationDoneEvent event) {
    StringWriter writer = new StringWriter();
    writer.append("Dear collaborator in the " + event.getProcessSolution().getLabel() + "-Project,");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("the named project has completed the <em>implementation</em> phase and is now in <em>testing</em>.<br />");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("Regards,");
    writer.append("Your Activiti Cycle");
    return writer.toString();
  }
}
