package org.activiti.cycle.impl.processsolution.listener;

import java.io.StringWriter;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.impl.processsolution.event.SpecificationDoneEvent;

/**
 * Listener sending an email when the specification for a given process solution
 * is completed.
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class SpecificationDoneEmailNotificationListener extends AbstractProcessSolutionStateEmailListener<SpecificationDoneEvent> {

  protected String getSubject(SpecificationDoneEvent event) {
    return "Specification done in " + event.getProcessSolution().getLabel();
  }

  protected String getMessage(SpecificationDoneEvent event) {
    StringWriter writer = new StringWriter();
    writer.append("Dear collaborator in the " + event.getProcessSolution().getLabel() + "-Project,");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("the named project has completed the <em>specification</em> phase and is now in <em>implementation</em>.<br />");
    writer.append("The corresponding technical project can be found in the <em>Implementation</em>-Folder of the referenced process-solution. ");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("Regards,");
    writer.append("Your Activiti Cycle");
    return writer.toString();
  }
}
