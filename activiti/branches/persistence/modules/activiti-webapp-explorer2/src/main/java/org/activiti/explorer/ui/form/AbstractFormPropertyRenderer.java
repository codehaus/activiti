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

package org.activiti.explorer.ui.form;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;

import com.vaadin.ui.Field;


/**
 * @author Frederik Heremans
 */
public abstract class AbstractFormPropertyRenderer implements FormPropertyRenderer {

  private Class<? extends FormType> formType;
  
  public AbstractFormPropertyRenderer(Class< ? extends FormType> formType) {
    this.formType = formType;
  }

  public Class< ? extends FormType> getFormType() {
    return formType;
  }

  public String getPropertyLabel(FormProperty formProperty) {
    if(formProperty.getName() != null) {
      return formProperty.getName();
    } else {
      return formProperty.getId();
    }
  }
  
  public String getFieldValue(FormProperty formProperty, Field field) {
    // Just returns toString() on the value in the field
    Object value = field.getValue();
    if(value != null) {
      return value.toString();
    }
    return null;
  }
  
  public abstract Field getPropertyField(FormProperty formProperty);
  
}
