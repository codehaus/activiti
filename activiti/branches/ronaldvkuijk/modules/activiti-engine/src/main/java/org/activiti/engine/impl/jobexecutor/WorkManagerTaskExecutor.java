/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Adapted (simplified) version of org.springframework.jca.work.WorkManagerTaskExecutor */

package org.activiti.engine.impl.jobexecutor;

import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.spi.work.WorkManager;

/**
 * {@link org.springframework.core.task.TaskExecutor} implementation
 * that delegates to a JCA 1.5 WorkManager, implementing the
 * {@link javax.resource.spi.work.WorkManager} interface.
 *
 * <p>This is mainly intended for use within a JCA ResourceAdapter implementation,
 * but may also be used in a standalone environment, delegating to a locally
 * embedded WorkManager implementation (such as Geronimo's).
 *
 * <p>Also implements the JCA 1.5 WorkManager interface itself, delegating all
 * calls to the target WorkManager. Hence, a caller can choose whether it wants
 * to talk to this executor through the Spring TaskExecutor interface or the
 * JCA 1.5 WorkManager interface.
 *
 * <p>This adapter is also capable of obtaining a JCA WorkManager from JNDI.
 * This is for example appropriate on the Geronimo application server, where
 * WorkManager GBeans (e.g. Geronimo's default "DefaultWorkManager" GBean)
 * can be linked into the J2EE environment through "gbean-ref" entries
 * in the <code>geronimo-web.xml</code> deployment descriptor.
 *
 * <p><b>On JBoss and GlassFish, obtaining the default JCA WorkManager
 * requires special lookup steps.</b> See the
 * {@link org.springframework.jca.work.jboss.JBossWorkManagerTaskExecutor}
 * {@link org.springframework.jca.work.glassfish.GlassFishWorkManagerTaskExecutor}
 * classes which are the direct equivalent of this generic JCA adapter class.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see #setWorkManager
 * @see javax.resource.spi.work.WorkManager#scheduleWork
 */
public class WorkManagerTaskExecutor {

  private static Logger log = Logger.getLogger(WorkManagerTaskExecutor.class.getName());
  
  private WorkManager workManager;

  private String workManagerName;

  public WorkManagerTaskExecutor() {
  }
  
  /**
   * Create a new WorkManagerTaskExecutor for the given WorkManager.
   * @param workManager the JCA WorkManager to delegate to
   */
  public WorkManagerTaskExecutor(WorkManager workManager) {
    setWorkManager(workManager);
  }

  protected WorkManager getDefaultWorkManager() {
    return null;
  }
  /**
   * Specify the JCA WorkManager instance to delegate to.
   */
  public void setWorkManager(WorkManager workManager) {
    //TODO check for null
    this.workManager = workManager;
  }

  /**
   * Set the JNDI name of the JCA WorkManager.
   * <p>This can either be a fully qualified JNDI name,
   * or the JNDI name relative to the current environment
   * naming context if "resourceRef" is set to "true".
   * @see #setWorkManager
   * @see #setResourceRef
   */
  public void setWorkManagerName(String workManagerName) {
    this.workManagerName = workManagerName;
  }

  //TODO Rename method, since injection is done on the Config, NOT this bean!!!
  public void afterPropertiesSet() throws NamingException {
    if (this.workManager == null) {
      if (this.workManagerName != null) {
        this.workManager = lookup(this.workManagerName, WorkManager.class);
      }
      else {
        this.workManager = getDefaultWorkManager();
      }
    }
    if (this.workManager == null) {
      //TODO TPE als nu
    } else {
      //TODO TPE met TF
    }
  }

  public void execute(Runnable task, String queueName) {
    // TODO Implement
  }
  
  /** JNDI prefix used in a J2EE container */
  public static final String CONTAINER_PREFIX = "java:comp/env/";


  private boolean resourceRef = true;

  /**
   * Perform an actual JNDI lookup for the given name via the JndiTemplate.
   * <p>If the name doesn't begin with "java:comp/env/", this prefix is added
   * if "resourceRef" is set to "true".
   * @param jndiName the JNDI name to look up
   * @param requiredType the required type of the object
   * @return the obtained object
   * @throws NamingException if the JNDI lookup failed
   * @see #setResourceRef
   */
  // TODO, Make generic??? Use more from spring??? I Hate all this templating stuff....
  protected <T> T lookup(String jndiName, Class<T> requiredType) throws NamingException {
    //TODO Check for null on jndiName
    String convertedName = convertJndiName(jndiName);
    T jndiObject = null;
    InitialContext i = new InitialContext();
    try {
      jndiObject = (T) i.lookup(convertedName);
    }
    catch (NamingException ex) {
      if (!convertedName.equals(jndiName)) {
        // Try fallback to originally specified name...
        if (log.isLoggable(Level.FINE)) {
          log.fine("Converted JNDI name [" + convertedName +
              "] not found - trying original name [" + jndiName + "]. " + ex);
        }
        jndiObject = (T) i.lookup(jndiName);
      }
      else {
        throw ex;
      }
    }
    if (log.isLoggable(Level.FINE)) {
      log.fine("Located object with JNDI name [" + convertedName + "]");
    }
    return jndiObject;
  }

  /**
   * Convert the given JNDI name into the actual JNDI name to use.
   * <p>The default implementation applies the "java:comp/env/" prefix if
   * "resourceRef" is "true" and no other scheme (e.g. "java:") is given.
   * @param jndiName the original JNDI name
   * @return the JNDI name to use
   * @see #CONTAINER_PREFIX
   * @see #setResourceRef
   */
  protected String convertJndiName(String jndiName) {
    // Prepend container prefix if not already specified and no other scheme given.
    if (!jndiName.startsWith(CONTAINER_PREFIX) && jndiName.indexOf(':') == -1) {
      jndiName = CONTAINER_PREFIX + jndiName;
    }
    return jndiName;
  }

  
}
