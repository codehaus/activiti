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
package org.activiti.engine.impl.variable;

import org.activiti.engine.impl.bpmn.ItemInstance;

/**
 * Type for {@link ItemInstance}
 * 
 * @author Esteban Robles Luna
 */
public class ItemType implements VariableType {

  public String getTypeName() {
    return "item";
  }

  public Object getValue(ValueFields valueFields) {
    return null;
  }

  public boolean isAbleToStore(Object value) {
    if (value==null) {
      return true;
    }
    return ItemInstance.class.isAssignableFrom(value.getClass());
  }

  public boolean isCachable() {
    return false;
  }

  public void setValue(Object value, ValueFields valueFields) {
  }

}
