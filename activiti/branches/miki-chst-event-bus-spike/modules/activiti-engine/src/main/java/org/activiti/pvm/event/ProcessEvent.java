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
package org.activiti.pvm.event;

/**
 * This interface is common to all process events being triggered by the
 * workflow system and handled by the {@link org.activiti.pvm.event.ProcessEventBus}.
 * <p> Each event is identified through a type which is a class to provide type
 * safety and must extend the {@link org.activiti.pvm.event.ProcessEvent}
 * interface. It is a good practice to use the same class as the event itself,
 * however, in certain cases this might not be an option.<br/>
 * A process event is always related to a {@link org.activiti.ProcessInstance}
 * and optionally to an {@link org.activiti.pvm.Activity}.
 *
 * @author Micha Kiener
 */
public interface ProcessEvent {
  /**
   * Returns the type of this event which is used within the bus to search for
   * an appropriate handler consuming this event. It is the mapping between
   * publisher (producer) and subscribers (consumers) and is handled by the
   * bus.
   *
   * @return the type of this event
   */
  Class<?> getEventType();

  /**
   * Returns <code>true</code>, if this is a stateful event, meaning it
   * implements the {@link StatefulProcessEvent} interface and provides stateful
   * information as a payload.
   *
   * @return <code>true</code> if this event is stateful and provides a payload
   *         in addition to the related elements like definition, instance and
   *         activity
   */
  boolean isStateful();

  /**
   * @return the id of the process definition this event is related to (never
   *         <code>null</code>)
   */
  String getProcessDefinitionId();

  /**
   * @return the id of the process instance this event is related to (never
   *         <code>null</code>)
   */
  String getProcessInstanceId();

  /**
   * @return the id of the optional activity this event is related to (might be
   *         <code>null</code>)
   */
  String getActivityId();
}
