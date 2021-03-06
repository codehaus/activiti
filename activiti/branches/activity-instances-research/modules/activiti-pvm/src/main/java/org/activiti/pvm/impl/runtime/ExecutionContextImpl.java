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

package org.activiti.pvm.impl.runtime;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.pvm.PvmException;
import org.activiti.pvm.activity.ActivityBehavior;
import org.activiti.pvm.activity.ActivityContext;
import org.activiti.pvm.activity.CompositeActivityBehavior;
import org.activiti.pvm.activity.SignallableActivityBehaviour;
import org.activiti.pvm.event.Event;
import org.activiti.pvm.event.EventContext;
import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.impl.process.ActivityImpl;
import org.activiti.pvm.impl.process.ProcessElementImpl;
import org.activiti.pvm.impl.process.TransitionImpl;
import org.activiti.pvm.process.PvmActivity;
import org.activiti.pvm.process.PvmProcessDefinition;
import org.activiti.pvm.process.PvmTransition;
import org.activiti.pvm.runtime.PvmProcessInstance;


/**
 * @author Tom Baeyens
 */
public class ExecutionContextImpl implements EventContext, ActivityContext {

  private static Logger log = Logger.getLogger(ExecutionContextImpl.class.getName());

  private static final AtomicOperation EVENTLISTENER_INVOKE = new EventListenerInvoke();
  private static final AtomicOperation PROCESS_START = new ProcessStart();
  private static final AtomicOperation ACTIVITY_START = new ActivityStart();
  private static final AtomicOperation TRANSITION_ACTIVITY_END = new TransitionActivityEnd();
  private static final AtomicOperation TRANSITION_TAKE = new TransitionTake();
  private static final AtomicOperation TRANSITION_ACTIVITY_START = new TransitionActivityStart();
  private static final AtomicOperation ACTIVITY_SIGNAL = new ActivitySignal();
  private static final AtomicOperation ACTIVITY_END = new ActivityEnd();
  private static final AtomicOperation PROCESS_END = new ProcessEnd();
  
  protected ActivityImpl activity;
  
  protected ProcessInstanceImpl processInstance;
  protected ActivityInstanceImpl activityInstance;
  protected ScopeInstanceImpl scopeInstance;
  
  protected String eventName;
  protected List<EventListener> eventListeners;
  protected int eventListenerIndex;
  protected AtomicOperation eventPostOperation;
  
  protected String signalName;
  protected Object signalData;
  
  protected String endReason;
  
  protected TransitionImpl transition;
  
  protected AtomicOperation nextAtomicOperation = null;
  protected boolean isOperating;

  public static void startProcessInstance(ProcessInstanceImpl processInstance) {
    log.fine("starting new process instance for "+processInstance.getProcessDefinition());
    ExecutionContextImpl executionContext = new ExecutionContextImpl();
    executionContext.processInstance = processInstance;
    executionContext.scopeInstance = processInstance;
    executionContext.fireEvent(processInstance.getProcessDefinition(), Event.PROCESS_START, PROCESS_START);
  }

  public static void signal(ActivityInstanceImpl activityInstance, String signalName, Object data) {
    log.fine("receiving signal '"+signalName+"' on "+activityInstance);
    ExecutionContextImpl executionContext = new ExecutionContextImpl();
    executionContext.activityInstance = activityInstance;
    executionContext.scopeInstance = activityInstance;
    executionContext.signalName = signalName;
    executionContext.signalData = data;
    executionContext.perform(ACTIVITY_SIGNAL);
  }

  public static void endProcessInstance(ProcessInstanceImpl processInstance, String endReason) {
    log.fine("ending "+processInstance+" because "+endReason);
    ExecutionContextImpl executionContext = new ExecutionContextImpl();
    executionContext.scopeInstance = processInstance;
  }

  public void take(PvmTransition transition) {
    if (transition==null) {
      throw new PvmException("transition is null");
    }
    log.fine("taking transition "+transition);
    this.transition = (TransitionImpl) transition;
    fireEvent(activityInstance.activity, Event.ACTIVITY_END, TRANSITION_ACTIVITY_END);
  }

  public void endActivityInstance() {
    log.fine("ending "+activityInstance);
    fireEvent(activityInstance.activity, Event.ACTIVITY_END, ACTIVITY_END);
  }
  
  public void endProcessInstance() {
    log.fine("ending "+processInstance);
    fireEvent(processInstance.getProcessDefinition(), Event.PROCESS_END, PROCESS_END);
  }

  public void executeTimerNestedActivity(ActivityImpl borderEventActivity) {
    throw new UnsupportedOperationException("please implement me");
  }

  protected void fireEvent(ProcessElementImpl processElement, String event, AtomicOperation eventPostOperation) {
    eventListeners = processElement
      .getEventListeners()
      .get(event);
    
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
  
  protected void perform(AtomicOperation atomicOperation) {
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

  protected interface AtomicOperation {
    void perform(ExecutionContextImpl executionContext);
  }
  
  protected static class EventListenerInvoke implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      if (executionContext.eventListenerIndex<executionContext.eventListeners.size()) {
        EventListener eventListener = executionContext.eventListeners.get(executionContext.eventListenerIndex);
        eventListener.event(executionContext);
        executionContext.eventListenerIndex++;
        executionContext.perform(EVENTLISTENER_INVOKE);

      } else {
        executionContext.eventListenerIndex = 0;
        AtomicOperation operation = executionContext.eventPostOperation;
        executionContext.eventPostOperation = null;
        executionContext.perform(operation);
      }
    }
  }
  
  protected static class ProcessStart implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ProcessInstanceImpl processInstance = executionContext.processInstance;
      ActivityImpl initial = processInstance.getProcessDefinition().getInitial();
      executionContext.activityInstance = processInstance.createActivityInstance(initial);
      executionContext.fireEvent(executionContext.activityInstance.getActivity(), Event.ACTIVITY_START, ACTIVITY_START);
    }
  }

  protected static class ActivityStart implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ActivityInstanceImpl activityInstance = executionContext.activityInstance;
      activityInstance.setExecutionContext(executionContext);
      ActivityImpl activity = activityInstance.getActivity();
      String activityId = activity.getId();
      ActivityBehavior activityBehaviour = activity.getActivityBehavior();
      if (activityBehaviour==null) {
        throw new PvmException("no activity behaviour specified in activty '"+activityId+"'");
      }
      try {
        activityBehaviour.start(executionContext);
      } catch (RuntimeException e) {
        log.log(Level.SEVERE, getDelegationExceptionMessage(activity, "start", e), e);
        throw e;
      } catch (Exception e) {
        String delegationExceptionMessage = getDelegationExceptionMessage(activity, "start", e);
        log.log(Level.SEVERE, delegationExceptionMessage, e);
        throw new PvmException(delegationExceptionMessage, e);
      }
    }
  }

  protected static String getDelegationExceptionMessage(ActivityImpl activity, String methodName, Exception e) {
    return "exception during "+methodName+" of activity '"+activity.getId()+"', behavior '"+activity.getActivityBehavior().getClass().getName()+"': "+e;
  }

  protected static class TransitionActivityEnd implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ActivityInstanceImpl activityInstance = executionContext.activityInstance;
      ScopeInstanceImpl parent = activityInstance.getParent();
      executionContext.scopeInstance = parent;
      activityInstance.setEnded(true);
      executionContext.fireEvent(activityInstance.getScope(), Event.ACTIVITY_END, TRANSITION_TAKE);
    }
  }

  protected static class TransitionTake implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ScopeInstanceImpl parentScopeInstance = executionContext.activityInstance.getParent();
      parentScopeInstance.removeActivityInstance(executionContext.activityInstance);
      executionContext.activityInstance = null;
      executionContext.fireEvent(executionContext.transition, Event.TRANSITION_TAKE, TRANSITION_ACTIVITY_START);
    }
  }

  protected static class TransitionActivityStart implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ActivityImpl destination = executionContext.transition.getDestination();
      executionContext.activityInstance = executionContext.scopeInstance.createActivityInstance(destination);
      executionContext.scopeInstance = executionContext.activityInstance;
      log.fine("starting "+destination);
      executionContext.fireEvent(destination, Event.ACTIVITY_START, ACTIVITY_START);
    }
  }

  protected static class ActivitySignal implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ActivityInstanceImpl activityInstance = executionContext.activityInstance;
      ActivityImpl activity = activityInstance.getActivity();
      SignallableActivityBehaviour signallableActivityBehaviour = (SignallableActivityBehaviour) activity.getActivityBehavior();
      try {
        signallableActivityBehaviour.signal(executionContext, executionContext.signalName, executionContext.signalData);
      } catch (RuntimeException e) {
        log.log(Level.SEVERE, getDelegationExceptionMessage(activity, "signal", e), e);
        throw e;
      } catch (Exception e) {
        String delegationExceptionMessage = getDelegationExceptionMessage(activity, "signal", e);
        log.log(Level.SEVERE, delegationExceptionMessage, e);
        throw new PvmException(delegationExceptionMessage, e);
      } finally {
        executionContext.signalName = null;
        executionContext.signalData = null;
      }
    }
  }

  protected static class ActivityEnd implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ActivityInstanceImpl activityInstance = executionContext.activityInstance;
      activityInstance.setEnded(true);
      ScopeInstanceImpl parent = activityInstance.getParent();
      activityInstance.destroy();
      parent.removeActivityInstance(activityInstance);
      executionContext.scopeInstance = parent;

      if (parent instanceof ProcessInstanceImpl) {
        if (parent.getActivityInstances().isEmpty()) {
          executionContext.activityInstance = null;
          executionContext.fireEvent(parent.getScope(), Event.PROCESS_END, PROCESS_END);
        }
      } else {
        executionContext.activityInstance = (ActivityInstanceImpl) parent;
        ActivityImpl activity = activityInstance.getActivity();
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if (activityBehavior instanceof CompositeActivityBehavior) {
          try {
            ((CompositeActivityBehavior)activityBehavior).activityInstanceEnded(executionContext);
          } catch (RuntimeException e) {
            log.log(Level.SEVERE, getDelegationExceptionMessage(activity, "activityInstanceEnded", e), e);
            throw e;
          } catch (Exception e) {
            String delegationExceptionMessage = getDelegationExceptionMessage(activity, "activityInstanceEnded", e);
            log.log(Level.SEVERE, delegationExceptionMessage, e);
            throw new PvmException(delegationExceptionMessage, e);
          } 
        }
      }
    }
  }

  protected static class ProcessEnd implements AtomicOperation {
    public void perform(ExecutionContextImpl executionContext) {
      ProcessInstanceImpl processInstance = (ProcessInstanceImpl) executionContext.scopeInstance;
      for (ActivityInstanceImpl activityInstance: processInstance.getActivityInstances()) {
        destroyActivityInstance(activityInstance);
      }
      processInstance.setEnded(true);
      processInstance.destroy();
    }
    private void destroyActivityInstance(ActivityInstanceImpl activityInstance) {
      for (ActivityInstanceImpl nestedActivityInstance: activityInstance.getActivityInstances()) {
        destroyActivityInstance(nestedActivityInstance);
      }
      activityInstance.destroy();
    }
  }

  // user variables ///////////////////////////////////////////////////////////
  
  public void setVariable(String variableName, Object value) {
    scopeInstance.setVariable(variableName, value);
  }
  
  public Object getVariable(String variableName) {
    return scopeInstance.getVariable(variableName);
  }

  public Map<String, Object> getVariables() {
    return scopeInstance.getVariables();
  }
  
  // system variables /////////////////////////////////////////////////////////
  
  public void setSystemVariable(String variableName, Object value) {
    scopeInstance.setSystemVariable(activity, variableName, value);
  }
  public Object getSystemVariable(String variableName) {
    return scopeInstance.getSystemVariable(activity, variableName);
  }

  
  // activity context methods /////////////////////////////////////////////////
  
  public PvmActivity getActivity() {
    return activityInstance.getActivity();
  }
  
  public ProcessInstanceImpl getProcessInstance() {
    return processInstance;
  }
  
  public ActivityInstanceImpl getActivityInstance() {
    return activityInstance;
  }
  
  public ScopeInstanceImpl getScopeInstance() {
    return scopeInstance;
  }

  @SuppressWarnings("unchecked")
  public List<PvmTransition> getOutgoingTransitions() {
    return (List) activityInstance.getActivity().getOutgoingTransitions();
  }

  public PvmTransition getOutgoingTransition(String transitionId) {
    return activityInstance.getActivity().getOutgoingTransition(transitionId);
  }

  @SuppressWarnings("unchecked")
  public List<PvmTransition> getIncomingTransitions() {
    return (List) activityInstance.getActivity().getIncomingTransitions();
  }

  @SuppressWarnings("unchecked")
  public List<PvmActivity> getActivities() {
    return (List) activityInstance.getActivity().getActivities();
  }

  public PvmProcessInstance createSubProcessInstance(PvmProcessDefinition processDefinition) {
    return null;
  }

  public void executeActivity(PvmActivity startActivity) {
  }

  public void keepAlive() {
  }

}
