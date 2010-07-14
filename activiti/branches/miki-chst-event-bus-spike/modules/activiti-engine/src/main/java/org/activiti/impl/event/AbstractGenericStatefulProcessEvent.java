/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.impl.event;

import org.activiti.pvm.event.GenericStatefulProcessEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * A basic stateful process event implementation supporting to store attributes
 * within an internal map.
 *
 * @author Micha Kiener
 * @param <T> the type of the payload this event is handling
 */
public abstract class AbstractGenericStatefulProcessEvent<T> extends AbstractProcessEvent implements GenericStatefulProcessEvent<T> {
  /** The lazy initialized attribute map for the generic event state. */
  private Map<String, Object> attributes;

  /**
   * Standard constructor used to create a new process event based on the given
   * relations.
   *
   * @param processDefinitionId the id of the process definition this event is
   * related to (must not be <code>null</code>)
   * @param processInstanceId the id of the process instance this event is
   * related to (must not be <code>null</code>)
   * @param activityId the optional id of the activity this event is created in
   * relation to
   */
  protected AbstractGenericStatefulProcessEvent(String processDefinitionId,
                                                String processInstanceId, String activityId) {
    super(processDefinitionId, processInstanceId, activityId);
  }

  /**
   * @see org.activiti.pvm.event.GenericStatefulProcessEvent#getAttribute(String)
   */
  public <A> A getAttribute(String key) {
    if (attributes == null) {
      return null;
    }

    return (A) attributes.get(key);
  }

  /**
   * @see org.activiti.pvm.event.GenericStatefulProcessEvent#removeAttribute(String)
   */
  public <A> A removeAttribute(String key) {
    if (attributes == null) {
      return null;
    }

    return (A) attributes.remove(key);
  }

  /**
   * @see org.activiti.pvm.event.GenericStatefulProcessEvent#setAttribute(String,
   *      java.lang.Object)
   */
  public <A> A setAttribute(String key, A value) {
    if (attributes == null) {
      attributes = new HashMap<String, Object>();
    }
    
    return (A) attributes.put(key, value);
  }

  /**
   * @see org.activiti.pvm.event.GenericStatefulProcessEvent#isStateful()
   */
  public boolean isStateful() {
    return true;
  }
}
