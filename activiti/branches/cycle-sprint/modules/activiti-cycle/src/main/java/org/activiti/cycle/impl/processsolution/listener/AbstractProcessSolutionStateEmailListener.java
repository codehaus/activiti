package org.activiti.cycle.impl.processsolution.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.cycle.CycleComponentFactory;
import org.activiti.cycle.event.CycleEventListener;
import org.activiti.cycle.impl.components.CycleEmailDispatcher;
import org.activiti.cycle.impl.processsolution.event.ProcessSolutionStateEvent;

/**
 * Abstract base class for {@link ProcessSolutionStateEvent}-listeners which
 * send a notification via email.
 * 
 * @author daniel.meyer@camunda.com
 */
public abstract class AbstractProcessSolutionStateEmailListener<T extends ProcessSolutionStateEvent> implements CycleEventListener<T> {

  Logger logger = Logger.getLogger(getClass().getName());

  String notifactionEmailAddress = "daniel.meyer@camunda.com";
  String fromEmailAddress = "activiti-cycle@localhost";

  protected CycleEmailDispatcher cycleEmailDispatcher = CycleComponentFactory.getCycleComponentInstance(CycleEmailDispatcher.class, CycleEmailDispatcher.class);

  public void onEvent(T event) {
    try {
      for (String address : getEmailAddresses()) {
        cycleEmailDispatcher.sendEmail(getFrom(), address, getSubject(event), getMessage(event));
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error while building email.", e);
      throw new RuntimeException("Error while building email.", e);
    }
  }

  protected abstract String getSubject(T event);

  protected abstract String getMessage(T event);

  protected String getFrom() {
    return fromEmailAddress;
  }

  protected List<String> getEmailAddresses() {
    List<String> adresses = new ArrayList<String>();
    adresses.add(notifactionEmailAddress);
    return adresses;
  }

}
