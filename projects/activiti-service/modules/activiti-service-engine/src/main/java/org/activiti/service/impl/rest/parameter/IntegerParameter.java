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

package org.activiti.service.impl.rest.parameter;

import org.activiti.service.api.Activiti;
import org.activiti.service.impl.rest.impl.BadRequestException;
import org.activiti.service.impl.rest.impl.Parameter;


public class IntegerParameter extends Parameter<Integer> {
  
  int minValue = Integer.MIN_VALUE;
  int maxValue = Integer.MAX_VALUE;

  public IntegerParameter() {
    super(Integer.class);
  }

  public IntegerParameter(String name, String description) {
    super(Integer.class, name, description);
  }
  
  public IntegerParameter(String name, String description, int minValue, int maxValue) {
    super(Integer.class, name, description);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }
  
  
  public IntegerParameter setMinValue(int minValue) {
    this.minValue = minValue;
    return this;
  }
  
  public IntegerParameter setMaxValue(int maxValue) {
    this.maxValue = maxValue;
    return this;
  }
  
  public IntegerParameter setDefaultValue(Integer defaultValue) {
    super.setDefaultValue(defaultValue);
    return this;
  }

  public IntegerParameter setDescription(String description) {
    super.setDescription(description);
    return this;
  }

  public IntegerParameter setMaxLength(int maxLength) {
    super.setMaxLength(maxLength);
    return this;
  }

  public IntegerParameter setName(String name) {
    super.setName(name);
    return this;
  }

  public IntegerParameter setUrlVariable() {
    super.setUrlVariable();
    return this;
  }

  public Integer convert(String textValue, Activiti activiti) {
    try {
      Integer intValue = new Integer(textValue);
      if (intValue<minValue) {
        throw new BadRequestException("parameter "+name+" must be greater then "+minValue+": "+textValue);
      }
      if (intValue>maxValue) {
        throw new BadRequestException("parameter "+name+" must be less then "+maxValue+": "+textValue);
      }
      return intValue;
    } catch (NumberFormatException e) {
      throw new BadRequestException("parameter "+name+" is not a valid integer: "+textValue);
    }
  }

  public String getTypeDescription() {
    return "integer ["+minValue+".."+maxValue+"]";
  }
}
