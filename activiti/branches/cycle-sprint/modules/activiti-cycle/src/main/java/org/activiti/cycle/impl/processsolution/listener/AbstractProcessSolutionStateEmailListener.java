package org.activiti.cycle.impl.processsolution.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.cycle.CycleComponentFactory;
import org.activiti.cycle.event.CycleEventListener;
import org.activiti.cycle.impl.components.CycleEmailDispatcher;
import org.activiti.cycle.impl.processsolution.event.ProcessSolutionStateEvent;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

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

        HtmlEmail email = new HtmlEmail();
        email.addTo(address);
        email.setHtmlMsg(getMessage(event));
        email.setSubject(getSubject(event));
        email.setFrom(getFrom());
        setMailServerProperties(email);
        cycleEmailDispatcher.sendEmail(email);
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

  /* copied from MailActivityBehaviour in engine */
  protected void setMailServerProperties(Email email) {
    // for the moment, simply reuse activiti-engine mailconfiguration
    ProcessEngineConfigurationImpl processEngineConfiguration = ((ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine()).getProcessEngineConfiguration();

    String host = processEngineConfiguration.getMailServerHost();
    email.setHostName(host);

    int port = processEngineConfiguration.getMailServerPort();
    email.setSmtpPort(port);

    String user = processEngineConfiguration.getMailServerUsername();
    String password = processEngineConfiguration.getMailServerPassword();
    if (user != null && password != null) {
      email.setAuthentication(user, password);
    }
  }/* end copied from MailActivityBehaviour in engine */

  protected List<String> getEmailAddresses() {
    List<String> adresses = new ArrayList<String>();
    adresses.add(notifactionEmailAddress);
    return adresses;
  }

}
