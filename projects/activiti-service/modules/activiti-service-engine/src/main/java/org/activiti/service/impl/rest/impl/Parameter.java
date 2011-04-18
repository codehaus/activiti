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

import javax.servlet.http.HttpServletResponse;

import org.activiti.service.impl.rest.handler.RestRequestContext;



/**
 * @author Tom Baeyens
 */
public abstract class Parameter <T> {
  
  protected Class<T> type;
  protected String name;
  protected String description;
  protected int maxLength = Integer.MAX_VALUE;
  protected boolean isParameter = true;
  /** defaultValue==null means this parameter is required */
  protected T defaultValue;

  public Parameter(Class<T> type) {
    this.type = type;
  }

  public Parameter(Class<T> type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }
  
  public Parameter<T> setName(String name) {
    this.name = name;
    return this;
  }

  public Parameter<T> setDescription(String description) {
    this.description = description;
    return this;
  }

  public Parameter<T> setMaxLength(int maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Parameter<T> setUrlVariable() {
    this.isParameter = false;
    return this;
  }

  public Parameter<T> setDefaultValue(T defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }
  
  public abstract T convert(String parameterValue);
  public abstract String getTypeDescription();

  public T get(RestRequestContext restRequestContext) {
    String textValue = null;
    if (isParameter) {
      textValue = restRequestContext
        .getHttpServletRequest()
        .getParameter(name);
    } else {
      textValue = restRequestContext
        .getUrlVariables()
        .get(name);
    }
    
    if (textValue==null) {
      if (defaultValue!=null) {
        return defaultValue;
      }
      throw new RestException(HttpServletResponse.SC_BAD_REQUEST, "parameter "+name+" is required");
    }
    
    return convert(textValue);
  }

  public Class<T> getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public boolean isRequired() {
    return defaultValue==null;
  }

  public String getDescription() {
    return description;
  }
  
  public int getMaxLength() {
    return maxLength;
  }
  
  public boolean isParameter() {
    return isParameter;
  }
}
