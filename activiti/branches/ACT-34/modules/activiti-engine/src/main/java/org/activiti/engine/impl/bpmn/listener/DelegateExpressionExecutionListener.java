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
package org.activiti.engine.impl.bpmn.listener;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.delegate.ExecutionListenerInvocation;
import org.activiti.engine.impl.delegate.JavaDelegateInvocation;


/**
 * @author Joram Barrez
 */
public class DelegateExpressionExecutionListener implements ExecutionListener {
  
  protected Expression expression;
  
  public DelegateExpressionExecutionListener(Expression expression) {
    this.expression = expression;
  }
  
  public void notify(DelegateExecution execution) throws Exception {
    // Note: we can't cache the result of the expression, because the
    // execution can change: eg. delegateExpression='${mySpringBeanFactory.randomSpringBean()}'
    Object delegate = expression.getValue(execution);
    
    if (delegate instanceof ExecutionListener) {
      Context.getProcessEngineConfiguration()
        .getDelegateInterceptor()
        .handleInvocation(new ExecutionListenerInvocation((ExecutionListener) delegate, execution));
    } else if (delegate instanceof JavaDelegate) {
      Context.getProcessEngineConfiguration()
        .getDelegateInterceptor()
        .handleInvocation(new JavaDelegateInvocation((JavaDelegate) delegate, execution));
    } else {
      throw new ActivitiException("Delegate expression " + expression 
              + " did not resolve to an implementation of " + ExecutionListener.class 
              + " nor " + JavaDelegate.class);
    }
  }

}
