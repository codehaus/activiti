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
 * An extension to the {@link StatefulProcessEvent} providing a generic
 * mechanism for a custom payload being attached to the event.
 *
 * @author Micha Kiener
 * @param <T> the type of the payload this event is handling
 */
public interface GenericStatefulProcessEvent<T> extends StatefulProcessEvent {
  /**
   * Stores the given attribute value within this process event, making it
   * available through {@link #getAttribute(String)}.
   *
   * @param key the key for the attribute
   * @param value the attribute value to be stored within this event
   * @param <A> the type of the attribute value being stored
   * @return the previous attribute stored under the same key, if any,
   *         <code>null</code> otherwise
   */
  <A> A setAttribute(String key, A value);

  /**
   * Returns the value for the attribute with the given key, if any,
   * <code>null</code> otherwise.
   *
   * @param key the key with which the attribute has been stored
   * @param <A> the expected type of the attribute
   * @return the value or <code>null</code> if not available
   */
  <A> A getAttribute(String key);

  /**
   * Removes the attribute stored with the given key and returns it, if any,
   * <code>null</code> otherwise.
   *
   * @param key the key to remove its attribute
   * @param <A> the expected type of the attribute
   * @return the old attribute value, if any, <code>null</code> otherwise
   */
  <A> A removeAttribute(String key);

  /**
   * Returns the payload of this event. If the workflow system is used in a
   * distributed or clustered environment, it might be necessary to make the
   * payload serializable, depending on the type of event, if it needs to be
   * broadcasted or not.
   *
   * @return the optional payload for this event, might be <code>null</code>
   */
  T getPayload();

  /**
   * Set the payload for this event. If this is a synchronous event, the payload
   * might even by changed by the consumer to return result values to the
   * producer. In this case, the producer and the consumer must know each
   * other.
   *
   * @param payload the payload to be set for this event
   */
  void setPayload(T payload);
}
