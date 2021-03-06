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
import org.activiti.service.impl.rest.impl.Parameter;


/**
 * @author Tom Baeyens
 */
public class StringParameter extends Parameter<String> {

  public StringParameter() {
    super(String.class);
  }

  public String convert(String parameterValue, Activiti activiti) {
    return parameterValue;
  }

  public String getTypeDescription() {
    return "string";
  }

}
