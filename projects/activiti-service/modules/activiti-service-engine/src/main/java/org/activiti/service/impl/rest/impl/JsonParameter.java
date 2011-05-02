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

package org.activiti.service.impl.rest.impl;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiException;
import org.activiti.service.impl.json.JsonConverter;

import com.mongodb.util.JSON;


/**
 * @author Tom Baeyens
 */
public class JsonParameter <T> extends Parameter <T> {

  public JsonParameter(Class<T> type) {
    super(type);
  }

  @SuppressWarnings("unchecked")
  public T convert(String parameterValue, Activiti activiti) {
    try {
      Object json = JSON.parse(parameterValue);
      JsonConverter jsonConverter = activiti.getJsonConverter();
      T persistable = (T) jsonConverter.toBean(json, type);
      return persistable;
    } catch (Exception e) {
      throw new ActivitiException("couldn't create "+type.getName()+" based on json "+parameterValue, e);
    }
  }

  public String getTypeDescription() {
    return "json "+type.getName();
  }
}
