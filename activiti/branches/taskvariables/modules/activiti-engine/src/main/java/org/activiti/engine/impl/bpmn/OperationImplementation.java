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
package org.activiti.engine.impl.bpmn;

/**
 * Represents an implementation of a {@link Operation}
 * 
 * @author Esteban Robles Luna
 */
public interface OperationImplementation {

  /**
   * @return the id of this implementation
   */
  String getId();
  
  /**
   * @return the name of this implementation
   */
  String getName();

  /**
   * Sends the message on behalf of operation
   * 
   * @param message the message to be sent
   * @param operation the operation that is interested on sending the message
   * @return the resulting message
   */
  MessageInstance sendFor(MessageInstance message, Operation operation);
}
