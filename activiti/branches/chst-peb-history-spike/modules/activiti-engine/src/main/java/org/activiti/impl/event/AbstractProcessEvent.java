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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activiti.pvm.event.ProcessEvent;

/**
 * The abstract base class for a {@link org.activiti.pvm.event.ProcessEvent},
 * supporting the basic relations like process definition, instance and
 * activity, the event is related to.
 *
 * @author Micha Kiener
 */
public abstract class AbstractProcessEvent<T> implements ProcessEvent<T> {
  private final String processDefinitionId;
  private final String processInstanceId;
  private final String activityId;
  private final Map<String, ?> headerAttributesMap;
  private final T payload;

  /**
   * Standard constructor used to create a new process event based on the given
   * relations.
   *
   * @param processDefinitionId the id of the process definition this event is
   * related to (must not be <code>null</code>)
   * @param processInstanceId the id of the process instance this event is
   * related to (must not be <code>null</code>)
   * @param activityId the optional id of the activity this event is created in
   * @param headerAttributesMap
   * @param payload
   */
  protected AbstractProcessEvent(String processDefinitionId, String processInstanceId, String activityId, Map<String, ?> headerAttributesMap, T payload) {
    this.processDefinitionId = processDefinitionId;
    this.processInstanceId = processInstanceId;
    this.activityId = activityId;
    this.headerAttributesMap = Collections.unmodifiableMap(headerAttributesMap);
    this.payload = payload;
  }

  /**
   * This basic implementation just returns the class of this event, you have to
   * override it, if the type does not match the event implementing class.
   *
   * @see org.activiti.pvm.event.ProcessEvent#getEventType()
   */
  public Class<?> getEventType() {
    return this.getClass();
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getProcessDefinitionId()
   */
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getProcessInstanceId()
   */
  public String getProcessInstanceId() {
    return processInstanceId;
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getActivityId()
   */
  public String getActivityId() {
    return activityId;
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getHeaderAttributesMap()
   */
  public Map<String, ?> getHeaderAttributesMap() {
    return headerAttributesMap;
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getHeaderAttribute(String)  
   */
  public <A> A getHeaderAttribute(String key) {
    return (A) headerAttributesMap.get(key);
  }

  /**
   * @see org.activiti.pvm.event.ProcessEvent#getPayload()
   */
  public T getPayload() {
    return payload;
  }
}
