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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.pvm.event.ProcessEvent;
import org.activiti.pvm.event.ProcessEventBus;
import org.activiti.pvm.event.ProcessEventHandler;

/**
 * Simple implementation of the {@link org.activiti.pvm.event.ProcessEventBus}
 * used by the process virtual machine.
 *
 * @author Micha Kiener
 */
public class SimpleProcessEventBus implements ProcessEventBus {
  /** The map of statically registered handlers. */
  private Map<Class<?>, List<ProcessEventHandler<ProcessEvent>>> handlers = new HashMap<Class<?>, List<ProcessEventHandler<ProcessEvent>>>();

  /**
   * @see org.activiti.pvm.event.ProcessEventBus#postEvent(org.activiti.pvm.event.ProcessEvent)
   */
  public void postEvent(ProcessEvent event) {
    dispatchEvent(event);
  }

  /**
   * Internal hook method to dispatch the given event.
   *
   * @param event the event to be dispatched
   */
  protected void dispatchEvent(ProcessEvent event) {
    // get the handlers for this event
    List<ProcessEventHandler<ProcessEvent>> handlerList = handlers.get(event.getEventType());
    if (handlerList == null) {
      return;
    }

    for (ProcessEventHandler<ProcessEvent> handler : handlerList) {
      handler.handleEvent(event);
    }
  }

  /**
   * @see org.activiti.pvm.event.ProcessEventBus#subscribe(org.activiti.pvm.event.ProcessEventHandler, Class[])
   */
  public void subscribe(ProcessEventHandler<? extends ProcessEvent> handler, Class<?>... eventTypes) {
    for (Class<?> eventType : eventTypes) {
      List<ProcessEventHandler<ProcessEvent>> handlerList = handlers.get(eventType);
      if (handlerList == null) {
        handlerList = new ArrayList<ProcessEventHandler<ProcessEvent>>();
        handlers.put(eventType, handlerList);
      }
      handlerList.add((ProcessEventHandler<ProcessEvent>) handler);
    }
  }

  /**
   * @see org.activiti.pvm.event.ProcessEventBus#unsubscribe(org.activiti.pvm.event.ProcessEventHandler)
   */
  public void unsubscribe(ProcessEventHandler<? extends ProcessEvent> handler) {
    for (Iterator<Map.Entry<Class<?>, List<ProcessEventHandler<ProcessEvent>>>> it = handlers.entrySet().iterator(); it
            .hasNext();) {
      Map.Entry<Class<?>, List<ProcessEventHandler<ProcessEvent>>> entry = it.next();
      List<ProcessEventHandler<ProcessEvent>> handlerList = entry.getValue();
      if (handlerList.remove(handler)) {
        // if the list of handlers is now empty, remove the entry from the map
        if (handlerList.isEmpty()) {
          it.remove();
        }
      }
    }
  }
}

