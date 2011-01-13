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

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.Expression;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.util.IoUtil;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * @author Ronald van Kuijk
 * @author Frederik Wiers
 */
public class ExecActivityBehavior extends JavaDelegateDelegate {

  // These should NOT be changed... Do not keep state.
  // Expressions can be references to variables so the result
  // of the 'getStringFromField(...)' still can be dynamic.
  private Expression command;
  private Expression path;
  private Expression params;

  public void execute(DelegateExecution execution) {
    String commandStr = getStringFromField(command, execution);
    String pathStr = getStringFromField(path, execution);
    String paramsStr = getStringFromField(params, execution);
    
    try {
      if (!(isEmpty(commandStr) || isEmpty(pathStr) || isEmpty(paramsStr))) {
        // do work here with fields from above
      } else {
        throw new ActivitiException("Could not execute command, one or more of the fields are not populated correctly");
      }
    } catch (Exception e) {
      if (e instanceof ActivitiException) {
        throw (ActivitiException) e;
      } else {
        throw new ActivitiException("Could not execute command", e);
      }
    }
  }
  protected String getStringFromField(Expression expression, DelegateExecution execution) {
    if (expression != null) {
      Object value = expression.getValue(execution);
      if (value != null) {
        return value.toString();
      }
    }
    return null;
  }
  
  private boolean isEmpty(String field) {
    return field == null || "".equals(field.trim());
  }
}
