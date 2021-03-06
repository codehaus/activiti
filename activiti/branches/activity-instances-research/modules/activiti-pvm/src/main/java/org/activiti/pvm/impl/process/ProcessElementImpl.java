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

package org.activiti.pvm.impl.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.process.PvmProcessElement;


/** common properties for process definition, activity and transition 
 * including event listeners.
 * 
 * @author Tom Baeyens
 */
public class ProcessElementImpl implements PvmProcessElement {

  private static final long serialVersionUID = 1L;
  
  protected String id;
  protected ProcessDefinitionImpl processDefinition;
  protected Map<String, List<EventListener>> eventListeners;
  protected Map<String, Object> properties;
  
  public ProcessElementImpl(String id, ProcessDefinitionImpl processDefinition) {
    this.id = id;
    this.processDefinition = processDefinition;
  }

  public void addEventListener(String eventName, EventListener eventListener) {
    if (eventListeners==null) {
      eventListeners = new HashMap<String, List<EventListener>>();
    }
    List<EventListener> listeners = eventListeners.get(eventName);
    if (listeners==null) {
      listeners = new ArrayList<EventListener>();
      eventListeners.put(eventName, listeners);
    }
    listeners.add(eventListener);
  }
  
  public void setProperty(String name, Object value) {
    if (properties==null) {
      properties = new HashMap<String, Object>();
    }
    properties.put(name, value);
  }
  
  public Object getProperty(String name) {
    if (properties==null) {
      return null;
    }
    return properties.get(name);
  }
  
  @SuppressWarnings("unchecked")
  public Map<String, Object> getProperties() {
    if (properties==null) {
      return Collections.EMPTY_MAP;
    }
    return properties;
  }
  
  @SuppressWarnings("unchecked")
  public Map<String, List<EventListener>> getEventListeners() {
    if (eventListeners==null) {
      return Collections.EMPTY_MAP;
    }
    return eventListeners;
  }
  
  // getters and setters //////////////////////////////////////////////////////
  
  public String getId() {
    return id;
  }
  
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public ProcessDefinitionImpl getProcessDefinition() {
    return processDefinition;
  }
}
