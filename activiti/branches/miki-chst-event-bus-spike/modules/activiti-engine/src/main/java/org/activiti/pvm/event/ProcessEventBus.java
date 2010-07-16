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
package org.activiti.pvm.event;

/**
 * This process event bus is responsible for receiving events, most likely being
 * triggered by the engine and it will route them to subscribers (event
 * handlers), being registered for receiving events. <p> It supports synchronous
 * and asynchronous events and payloads.
 *
 * @author Micha Kiener
 */
public interface ProcessEventBus {
  /**
   * Posts the given event to the bus. As events are synchronous, it is
   * forwarded to all registered event handlers, listening to the event type
   * immediately, thus blocking the invoker until consumed by the handlers.
   *
   * @param event the event to be posted and consumed
   */
  void postEvent(ProcessEvent event);

  /**
   * Adds the given event handler to the internal list of consumers for the
   * specified event types. At least one event type has to be provided but the
   * handler might register itself to actually handle more than one event
   * type. If more than one handler is finally subscribed for the same event
   * type, they are being invoked in the same order they have been
   * registered.
   *
   * @param handler the event handler being registered within this bus
   * @param eventTypes one or more event types the handler will be subscribed
   * to
   */
  void subscribe(ProcessEventHandler<? extends ProcessEvent> handler, Class<?>... eventTypes);

  /**
   * Removes the given handler from the subscription list within the event
   * bus. If it was registered for more than one event type, the handler will
   * be removed from all event type registrations.
   *
   * @param handler the event handler to be removed from the bus
   */
  void unsubscribe(ProcessEventHandler<? extends ProcessEvent> handler);
}

