/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.activiti.cdi.impl;

import javax.enterprise.inject.Alternative;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;

/**
 * {@link ProcessEngineLookup} for looking up a {@link ProcessEngine} in Jndi.
 * 
 * Uses default values for the Jndi name:
 * <ul>
 * <li>In an Java EE environment, the default lookup String is
 * {@code java:comp/Activiti/default}</li>
 * <li>In a Java SE Environment (for example in a ServletContainer) the default
 * lookup String is {@code java:env/Activiti/default}
 * </ul>
 * Note that this in an {@link Alternative} implementation of the
 * {@link ProcessEngineLookup} interface: if you want to use this
 * implementation, enable it in the beans.xml deployment descriptor.
 * 
 * @author Daniel Meyer
 */
@Alternative
public class JndiProcessEngineLookup implements ProcessEngineLookup {

  protected String jndiName;

  public ProcessEngine getProcessEngine() {
    try {
      return (ProcessEngine) InitialContext.doLookup(jndiName);
    } catch (NamingException e) {
      throw new ActivitiException("No processEngine is bound to the jndi name '" + jndiName + "'.", e);
    } catch (ClassCastException e) {
      throw new ActivitiException("The object bound to '" + jndiName + "' appears not to be a ProcessEngine. ", e);
    }
  }
  public String getJndiName() {
    if (jndiName == null) {
      synchronized (this) {
        if (jndiName == null) {
          initJndiName();
        }
      }
    }
    return jndiName;
  }

  protected void initJndiName() {

  }

  public void setJndiName(String jndiName) {
    this.jndiName = jndiName;
  }
}
