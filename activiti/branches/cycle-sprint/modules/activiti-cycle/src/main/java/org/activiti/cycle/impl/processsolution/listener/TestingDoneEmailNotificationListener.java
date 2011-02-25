package org.activiti.cycle.impl.processsolution.listener;

import java.io.StringWriter;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.event.CycleEventListener;
import org.activiti.cycle.impl.processsolution.event.TestingDoneEvent;

/**
 * Listener sending an email when the implementation for a given process
 * solution is completed.
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class TestingDoneEmailNotificationListener extends AbstractProcessSolutionStateEmailListener<TestingDoneEvent> implements
        CycleEventListener<TestingDoneEvent> {

  protected String getSubject(TestingDoneEvent event) {
    return "Testing done in " + event.getProcessSolution().getLabel();
  }

  protected String getMessage(TestingDoneEvent event) {
    StringWriter writer = new StringWriter();
    writer.append("Dear collaborator in the " + event.getProcessSolution().getLabel() + "-Project,");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("the named project has completed the <em>testing</em> phase and the generated product/prototype is ready for rollout/demonstration.<br />");
    writer.append("Further requirements can now be specified (the project is back \"in specificcation\")");
    writer.append("<br />");
    writer.append("<br />");
    writer.append("Regards,");
    writer.append("Your Activiti Cycle");
    return writer.toString();
  }
}
