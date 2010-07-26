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

package org.activiti.impl.pvm.runtime;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.impl.pvm.event.Event;
import org.activiti.impl.pvm.event.EventContext;
import org.activiti.impl.pvm.event.EventListener;
import org.activiti.impl.pvm.process.Activity;
import org.activiti.impl.pvm.process.EventDispatcher;
import org.activiti.impl.pvm.process.Transition;
import org.activiti.impl.pvm.spi.ActivityBehaviour;


/**
 * @author Tom Baeyens
 */
public class ExecutionContext implements EventContext {

  private static Logger log = Logger.getLogger(ExecutionContext.class.getName());

  private static final AtomicOperation EVENTLISTENER_INVOKE = new EventListenerInvoke();
  private static final AtomicOperation PROCESS_START = new ProcessStart();
  private static final AtomicOperation ACTIVITY_START = new ActivityStart();
  private static final AtomicOperation TRANSITION_ACTIVITY_END = new TransitionActivityEnd();
  private static final AtomicOperation TRANSITION_TAKE = new TransitionTake();
  private static final AtomicOperation TRANSITION_ACTIVITY_START = new TransitionActivityStart();
  
  protected ProcessInstance processInstance;
  protected ActivityInstance activityInstance;
  protected ScopeInstance scopeInstance;
  
  protected String event;
  protected List<EventListener> eventListeners;
  protected int eventListenerIndex;
  protected AtomicOperation eventPostOperation;
  
  protected Transition transition;
  
  protected AtomicOperation nextAtomicOperation = null;
  protected boolean isOperating;

  public void startProcessInstance(ProcessInstance processInstance) {
    this.processInstance = processInstance;
    fireEvent(processInstance.getProcessDefinition(), Event.PROCESS_START, PROCESS_START);
  }

  public void takeTransition(ActivityInstance activityInstance, Transition transition) {
    this.activityInstance = activityInstance;
    this.transition = transition;
    fireEvent(activityInstance.activity, Event.ACTIVITY_END, TRANSITION_ACTIVITY_END);
  }

  private void fireEvent(EventDispatcher eventDispatcher, String event, AtomicOperation eventPostOperation) {
    eventListeners = eventDispatcher.getEventListeners().get(event);
    if ( (eventListeners!=null)
         && (!eventListeners.isEmpty())
       ) {
      this.eventListenerIndex = 0;
      this.eventPostOperation = eventPostOperation;
      perform(EVENTLISTENER_INVOKE);
      
    } else {
      perform(eventPostOperation);
    }
  }

  private void perform(AtomicOperation atomicOperation) {
    this.nextAtomicOperation = atomicOperation;
    if (!isOperating) {
      isOperating = true;
      while (nextAtomicOperation!=null) {
        AtomicOperation currentOperation = this.nextAtomicOperation;
        this.nextAtomicOperation = null;
        if (log.isLoggable(Level.FINEST)) {
          log.finest("AtomicOperation: " + currentOperation.getClass().getName());
        }
        currentOperation.perform(this);
      }
      isOperating = false;
    }
  }

  private interface AtomicOperation {
    void perform(ExecutionContext executionContext);
  }
  
  private static class EventListenerInvoke implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      if (executionContext.eventListenerIndex<executionContext.eventListeners.size()) {
        EventListener eventListener = executionContext.eventListeners.get(executionContext.eventListenerIndex);
        eventListener.event(executionContext);
        executionContext.eventListenerIndex++;
        executionContext.perform(EVENTLISTENER_INVOKE);

      } else {
        executionContext.eventListenerIndex = 0;
        executionContext.eventPostOperation = null;
        AtomicOperation operation = executionContext.eventPostOperation;
        executionContext.eventPostOperation = null;
        executionContext.perform(operation);
      }
    }
  }
  
  private static class ProcessStart implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      ProcessInstance processInstance = executionContext.processInstance;
      Activity initial = processInstance.getProcessDefinition().getInitial();
      executionContext.activityInstance = processInstance.createActivityInstance(initial);
      executionContext.fireEvent(executionContext.activityInstance.getActivity(), Event.ACTIVITY_START, ACTIVITY_START);
    }
  }

  private static class ActivityStart implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      ActivityInstance activityInstance = executionContext.activityInstance;
      activityInstance.setExecutionContext(executionContext);
      ActivityBehaviour activityBehaviour = activityInstance.getActivity().getActivityBehaviour();
      activityBehaviour.start(activityInstance);
    }
  }

  private static class TransitionActivityEnd implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      ActivityInstance activityInstance = executionContext.activityInstance;
      Transition transition = executionContext.transition;
      ScopeInstance parent = activityInstance.getParent();
      executionContext.scopeInstance = parent;
      parent.removeActivityInstance(activityInstance);
      executionContext.fireEvent(transition, Event.TRANSITION_TAKE, TRANSITION_TAKE);
    }
  }

  private static class TransitionTake implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      executionContext.fireEvent(executionContext.transition, Event.TRANSITION_TAKE, TRANSITION_ACTIVITY_START);
    }
  }

  private static class TransitionActivityStart implements AtomicOperation {
    @Override
    public void perform(ExecutionContext executionContext) {
      Activity destination = executionContext.transition.getDestination();
      executionContext.activityInstance = executionContext.scopeInstance.createActivityInstance(destination);
      executionContext.scopeInstance = null;
      executionContext.fireEvent(destination, Event.ACTIVITY_START, ACTIVITY_START);
    }
  }
}
