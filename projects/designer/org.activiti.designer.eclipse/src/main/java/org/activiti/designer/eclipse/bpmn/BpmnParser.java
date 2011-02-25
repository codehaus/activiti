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

package org.activiti.designer.eclipse.bpmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.eclipse.bpmn2.ActivitiListener;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;


/**
 * @author Tijs Rademakers
 */
public class BpmnParser {
  
  private static final String ACTIVITI_EXTENSIONS_NAMESPACE = "http://activiti.org/bpmn";
  private static final String CLASS_TYPE = "classType";
  private static final String EXPRESSION_TYPE = "expressionType";
  
  public boolean bpmdiInfoFound;
  public List<FlowElement> bpmnList = new ArrayList<FlowElement>();
  public List<SequenceFlowModel> sequenceFlowList = new ArrayList<SequenceFlowModel>();
  private List<BoundaryEventModel> boundaryList = new ArrayList<BoundaryEventModel>();
  public Map<String, GraphicInfo> locationMap = new HashMap<String, GraphicInfo>();
  public org.eclipse.bpmn2.Process process;
  
  public void parseBpmn(XMLStreamReader xtr) {
    try {
      boolean processExtensionAvailable = false;
      SubProcess activeSubProcess = null;
      while(xtr.hasNext()) {
        xtr.next();
        
        if(xtr.isEndElement() && "subProcess".equalsIgnoreCase(xtr.getLocalName())) {
          activeSubProcess = null;
        }
        
        if(xtr.isStartElement() == false) continue;
        
        if(xtr.isStartElement() && "definitions".equalsIgnoreCase(xtr.getLocalName())) {
          
        } else if(xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
          processExtensionAvailable = true;
        
        } else if(processExtensionAvailable == true && xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          process = Bpmn2Factory.eINSTANCE.createProcess();
          process.getExecutionListeners().addAll(parseListeners(xtr));
          processExtensionAvailable = false;
        
        } else {
          
          processExtensionAvailable = false;
          
          if(xtr.isStartElement() && "startEvent".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            StartEvent startEvent = parseStartEvent(xtr);
            startEvent.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(startEvent);
            bpmnList.add(startEvent);
            
          } else if(xtr.isStartElement() && "subProcess".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            SubProcess subProcess = parseSubProcess(xtr);
            subProcess.setId(elementid);
            activeSubProcess = subProcess;
            bpmnList.add(subProcess);
            
          } else if(xtr.isStartElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            UserTask userTask = parseUserTask(xtr);
            userTask.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(userTask);
            bpmnList.add(userTask);
            
          } else if(xtr.isStartElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            Task task = null;
            if("mail".equalsIgnoreCase(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "type"))) {
              task = parseMailTask(xtr);
            } else {
              task = parseServiceTask(xtr);
            }
            task.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(task);
            bpmnList.add(task);
          
          } else if(xtr.isStartElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ScriptTask scriptTask = parseScriptTask(xtr);
            scriptTask.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(scriptTask);
            bpmnList.add(scriptTask);
            
          } else if(xtr.isStartElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ManualTask manualTask = parseManualTask(xtr);
            manualTask.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(manualTask);
            bpmnList.add(manualTask);
            
          } else if(xtr.isStartElement() && "receiveTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ReceiveTask receiveTask = parseReceiveTask(xtr);
            receiveTask.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(receiveTask);
            bpmnList.add(receiveTask);
            
          } else if(xtr.isStartElement() && "callActivity".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            CallActivity callActivity = parseCallActivity(xtr);
            callActivity.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(callActivity);
            bpmnList.add(callActivity);
            
          } else if(xtr.isStartElement() && "endEvent".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            EndEvent endEvent = parseEndEvent(xtr);
            endEvent.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(endEvent);
            bpmnList.add(endEvent);
          
          } else if(xtr.isStartElement() && "exclusiveGateway".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ExclusiveGateway exclusiveGateway = parseExclusiveGateway(xtr);
            exclusiveGateway.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(exclusiveGateway);
            bpmnList.add(exclusiveGateway);
            
          } else if(xtr.isStartElement() && "parallelGateway".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ParallelGateway parallelGateway = parseParallelGateway(xtr);
            parallelGateway.setId(elementid);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(parallelGateway);
            bpmnList.add(parallelGateway);
          
          } else if(xtr.isStartElement() && "boundaryEvent".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            BoundaryEventModel event = parseBoundaryEvent(xtr);
            event.boundaryEvent.setId(elementid);
            boundaryList.add(event);
            if(activeSubProcess != null) activeSubProcess.getFlowElements().add(event.boundaryEvent);
            bpmnList.add(event.boundaryEvent);
            
          } else if(xtr.isStartElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
            SequenceFlowModel sequenceFlow = parseSequenceFlow(xtr);
            sequenceFlowList.add(sequenceFlow);
            
          } else if(xtr.isStartElement() && "BPMNShape".equalsIgnoreCase(xtr.getLocalName())) {
            bpmdiInfoFound = true;
            String id = xtr.getAttributeValue(null, "bpmnElement");
            boolean readyWithBPMNShape = false;
            while(readyWithBPMNShape == false && xtr.hasNext()) {
              xtr.next();
              if(xtr.isStartElement() && "Bounds".equalsIgnoreCase(xtr.getLocalName())) {
                GraphicInfo graphicInfo = new GraphicInfo();
                graphicInfo.x = Double.valueOf(xtr.getAttributeValue(null, "x")).intValue();
                graphicInfo.y = Double.valueOf(xtr.getAttributeValue(null, "y")).intValue();
                graphicInfo.height = Double.valueOf(xtr.getAttributeValue(null, "height")).intValue();
                locationMap.put(id, graphicInfo);
                readyWithBPMNShape = true;
              }
            }
          }
        }
      }
      
      for (FlowElement flowElement : bpmnList) {
        if(flowElement instanceof BoundaryEvent) {
          BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
          for (BoundaryEventModel eventModel : boundaryList) {
            if(boundaryEvent.getId().equals(eventModel.boundaryEvent.getId())) {
              for (FlowElement attachElement : bpmnList) {
                if(attachElement instanceof Activity) {
                  if(attachElement.getId().equals(eventModel.attachedRef)) {
                    boundaryEvent.setAttachedToRef((Activity) attachElement);
                  }
                }
              }
            }
          }
        }
      }
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private StartEvent parseStartEvent(XMLStreamReader xtr) {
    StartEvent startEvent = Bpmn2Factory.eINSTANCE.createStartEvent();
    startEvent.setName("Start");
    if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey") != null) {
      startEvent.setFormKey(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "formKey"));
    }
    return startEvent;
  }
  
  private EndEvent parseEndEvent(XMLStreamReader xtr) {
    EndEvent endEvent = Bpmn2Factory.eINSTANCE.createEndEvent();
    endEvent.setName("End");
    return endEvent;
  }
  
  private SubProcess parseSubProcess(XMLStreamReader xtr) {
    SubProcess subProcess = Bpmn2Factory.eINSTANCE.createSubProcess();
    String name = xtr.getAttributeValue(null, "name");
    if(name != null) {
      subProcess.setName(name);
    } else {
      subProcess.setName(xtr.getAttributeValue(null, "id"));
    }
    return subProcess;
  }
  
  private ExclusiveGateway parseExclusiveGateway(XMLStreamReader xtr) {
    ExclusiveGateway exclusiveGateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
    String name = xtr.getAttributeValue(null, "name");
    if(name != null) {
      exclusiveGateway.setName(name);
    } else {
      exclusiveGateway.setName(xtr.getAttributeValue(null, "id"));
    }
    return exclusiveGateway;
  }
  
  private ParallelGateway parseParallelGateway(XMLStreamReader xtr) {
    ParallelGateway parallelGateway = Bpmn2Factory.eINSTANCE.createParallelGateway();
    parallelGateway.setName(xtr.getAttributeValue(null, "name"));
    return parallelGateway;
  }
  
  private SequenceFlowModel parseSequenceFlow(XMLStreamReader xtr) {
    
    SequenceFlowModel sequenceFlow = new SequenceFlowModel();
    sequenceFlow.sourceRef = xtr.getAttributeValue(null, "sourceRef");
    sequenceFlow.targetRef = xtr.getAttributeValue(null, "targetRef");
    FormalExpression conditionValue = parseSequenceFlowCondition(xtr, sequenceFlow);
    sequenceFlow.conditionExpression = conditionValue;
    return sequenceFlow;
  }
  
  private static FormalExpression parseSequenceFlowCondition(XMLStreamReader xtr, SequenceFlowModel sequenceFlow) {
    FormalExpression condition = null;
    boolean readyWithSequenceFlow = false;
    try {
      while (readyWithSequenceFlow == false && xtr.hasNext()) {
        xtr.next();
        if (xtr.isStartElement() && "conditionExpression".equalsIgnoreCase(xtr.getLocalName())) {
          condition = Bpmn2Factory.eINSTANCE.createFormalExpression();
          condition.setBody(xtr.getElementText());
        
        } else if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          sequenceFlow.listenerList.addAll(parseListeners(xtr));
        
        } else if (xtr.isEndElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithSequenceFlow = true;
        }
      }
    } catch (Exception e) {}
    return condition;
  }

  
  private static UserTask parseUserTask(XMLStreamReader xtr) {
    UserTask userTask = Bpmn2Factory.eINSTANCE.createUserTask();
    userTask.setName(xtr.getAttributeValue(null, "name"));
    
    if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "assignee") != null) {
      String assignee = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "assignee");
      userTask.setAssignee(assignee);
    
    } else if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers") != null) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers");
      String[] expressionList = null;
      if (expression.contains(";")) {
        expressionList = expression.split(";");
      } else {
        expressionList = new String[] { expression };
      }
      for (String user : expressionList) {
        CandidateUser candidateUser = Bpmn2Factory.eINSTANCE.createCandidateUser();
        candidateUser.setUser(user);
        userTask.getCandidateUsers().add(candidateUser);
      }
      
    } else if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups") != null) {
      String expression = xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups");
      String[] expressionList = null;
      if (expression.contains(";")) {
        expressionList = expression.split(";");
      } else {
        expressionList = new String[] { expression };
      }
      for (String group : expressionList) {
        CandidateGroup candidateGroup = Bpmn2Factory.eINSTANCE.createCandidateGroup();
        candidateGroup.setGroup(group);
        userTask.getCandidateGroups().add(candidateGroup);
      }
      
    } 
    boolean readyWithUserTask = false;
    try {
      while(readyWithUserTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "formalExpression".equalsIgnoreCase(xtr.getLocalName())) {
          CandidateGroup group = Bpmn2Factory.eINSTANCE.createCandidateGroup();
          group.setGroup(xtr.getElementText());
          userTask.getCandidateGroups().add(group);
        
        } else if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          userTask.getActivitiListeners().addAll(parseListeners(xtr));
          
        } else if(xtr.isEndElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithUserTask = true;
        }
      }
    } catch(Exception e) {}
    return userTask;
  }
  
  private ScriptTask parseScriptTask(XMLStreamReader xtr) {
    ScriptTask scriptTask = Bpmn2Factory.eINSTANCE.createScriptTask();
    scriptTask.setName(xtr.getAttributeValue(null, "name"));
    scriptTask.setScriptFormat(xtr.getAttributeValue(null, "scriptFormat"));
    boolean readyWithScriptTask = false;
    try {
      while(readyWithScriptTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "script".equalsIgnoreCase(xtr.getLocalName())) {
          scriptTask.setScript(xtr.getElementText());
        
        } else if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          scriptTask.getActivitiListeners().addAll(parseListeners(xtr));
          
        } else if(xtr.isEndElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithScriptTask = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return scriptTask;
  }
  
  private MailTask parseMailTask(XMLStreamReader xtr) {
    MailTask mailTask = Bpmn2Factory.eINSTANCE.createMailTask();
    mailTask.setName(xtr.getAttributeValue(null, "name"));
    
    boolean readyWithServiceTask = false;
    try {
      while(readyWithServiceTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          fillExtensionsForMailTask(xtr, mailTask);
          
        } else if(xtr.isEndElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithServiceTask = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return mailTask;
  }
  
  private ServiceTask parseServiceTask(XMLStreamReader xtr) {
    ServiceTask serviceTask = Bpmn2Factory.eINSTANCE.createServiceTask();
    serviceTask.setName(xtr.getAttributeValue(null, "name"));
    if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "class") != null) {
      serviceTask.setImplementationType(CLASS_TYPE);
      serviceTask.setImplementation(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "class"));
    } else if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "expression") != null) {
      serviceTask.setImplementationType(EXPRESSION_TYPE);
      serviceTask.setImplementation(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "expression"));
    }
    
    if(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName") != null) {
      serviceTask.setResultVariableName(xtr.getAttributeValue(ACTIVITI_EXTENSIONS_NAMESPACE, "resultVariableName"));
    }
    
    boolean readyWithServiceTask = false;
    try {
      while(readyWithServiceTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          fillExtensionsForServiceTask(xtr, serviceTask);
          
        } else if(xtr.isEndElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithServiceTask = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return serviceTask;
  }
  
  private static void fillExtensionsForServiceTask(XMLStreamReader xtr, ServiceTask serviceTask) {
    List<FieldExtension> extensionList = new ArrayList<FieldExtension>();
    boolean readyWithExtensions = false;
    try {
      ActivitiListener listener = null;
      while(readyWithExtensions == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
          FieldExtension extension = parseFieldExtension(xtr);
          extensionList.add(extension);
          
        } else if(xtr.isStartElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          if(extensionList.size() > 0) {
            serviceTask.getFieldExtensions().addAll(extensionList);
            extensionList = new ArrayList<FieldExtension>();
          }
          listener = parseListener(xtr);
          serviceTask.getActivitiListeners().add(listener);
          
        } else if(xtr.isEndElement() && "executionListener".equalsIgnoreCase(xtr.getLocalName())) {
          if(extensionList.size() > 0) {
            listener.getFieldExtensions().addAll(extensionList);
            extensionList = new ArrayList<FieldExtension>();
          }
          serviceTask.getActivitiListeners().add(listener);
          
        } else if(xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          if(extensionList.size() > 0) {
            serviceTask.getFieldExtensions().addAll(extensionList);
          }
          readyWithExtensions = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private static void fillExtensionsForMailTask(XMLStreamReader xtr, MailTask mailTask) {
    boolean readyWithExtensions = false;
    try {
      while(readyWithExtensions == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
          String name = xtr.getAttributeValue(null, "name");
          if("to".equalsIgnoreCase(name)) {
            mailTask.setTo(getFieldExtensionValue(xtr));
          } else if("from".equalsIgnoreCase(name)) {
              mailTask.setFrom(getFieldExtensionValue(xtr));
          } else if("cc".equalsIgnoreCase(name)) {
            mailTask.setCc(getFieldExtensionValue(xtr));
          } else if("bcc".equalsIgnoreCase(name)) {
            mailTask.setBcc(getFieldExtensionValue(xtr));
          } else if("subject".equalsIgnoreCase(name)) {
            mailTask.setSubject(getFieldExtensionValue(xtr));
          } else if("html".equalsIgnoreCase(name)) {
            mailTask.setHtml(getFieldExtensionValue(xtr));
          } else if("text".equalsIgnoreCase(name)) {
            mailTask.setText(getFieldExtensionValue(xtr));
          } 
        } else if(xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithExtensions = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private static FieldExtension parseFieldExtension(XMLStreamReader xtr) {
    FieldExtension extension = Bpmn2Factory.eINSTANCE.createFieldExtension();
    extension.setFieldname(xtr.getAttributeValue(null, "name"));
    extension.setExpression(getFieldExtensionValue(xtr));
    return extension;
  }
  
  private static String getFieldExtensionValue(XMLStreamReader xtr) {
    if(xtr.getAttributeValue(null, "stringValue") != null) {
      return xtr.getAttributeValue(null, "stringValue");
      
    } else if(xtr.getAttributeValue(null, "expression") != null) {
      return xtr.getAttributeValue(null, "expression");
      
    } else {
      boolean readyWithFieldExtension = false;
      try {
        while (readyWithFieldExtension == false && xtr.hasNext()) {
          xtr.next();
          if (xtr.isStartElement() && "string".equalsIgnoreCase(xtr.getLocalName())) {
            return xtr.getElementText();
            
          } else if (xtr.isStartElement() && "expression".equalsIgnoreCase(xtr.getLocalName())) {
            return xtr.getElementText();
            
          } else if (xtr.isEndElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
            return null;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  private static List<ActivitiListener> parseListeners(XMLStreamReader xtr) {
    List<ActivitiListener> listenerList = new ArrayList<ActivitiListener>();
    boolean readyWithListener = false;
    try {
      ActivitiListener listener = null;
      while(readyWithListener == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && ("executionListener".equalsIgnoreCase(xtr.getLocalName()) ||
                "taskListener".equalsIgnoreCase(xtr.getLocalName()))) {
          
          listener = parseListener(xtr);
          listenerList.add(listener);
          
        } else if(xtr.isStartElement() && "field".equalsIgnoreCase(xtr.getLocalName())) {
          listener.getFieldExtensions().add(parseFieldExtension(xtr));
        } else if(xtr.isEndElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithListener = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    return listenerList;
  }
  
  private static ActivitiListener parseListener(XMLStreamReader xtr) {
    ActivitiListener listener = Bpmn2Factory.eINSTANCE.createActivitiListener();
    if(xtr.getAttributeValue(null, "class") != null) {
      listener.setImplementation(xtr.getAttributeValue(null, "class"));
      listener.setImplementationType(CLASS_TYPE);
    } else if(xtr.getAttributeValue(null, "expression") != null) {
      listener.setImplementation(xtr.getAttributeValue(null, "expression"));
      listener.setImplementationType(EXPRESSION_TYPE);
    }
    listener.setEvent(xtr.getAttributeValue(null, "event"));
    return listener;
  }
  
  private ManualTask parseManualTask(XMLStreamReader xtr) {
    ManualTask manualTask = Bpmn2Factory.eINSTANCE.createManualTask();
    manualTask.setName(xtr.getAttributeValue(null, "name"));
    boolean readyWithTask = false;
    try {
      while(readyWithTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          manualTask.getActivitiListeners().addAll(parseListeners(xtr));
          
        } else if(xtr.isEndElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithTask = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return manualTask;
  }
  
  private CallActivity parseCallActivity(XMLStreamReader xtr) {
    CallActivity callActivity = Bpmn2Factory.eINSTANCE.createCallActivity();
    callActivity.setName(xtr.getAttributeValue(null, "name"));
    if(xtr.getAttributeValue(null, "calledElement") != null && xtr.getAttributeValue(null, "calledElement").length() > 0) {
      org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
      process.setId(xtr.getAttributeValue(null, "calledElement"));
      callActivity.setCalledElementRef(process);
    }
    return callActivity;
  }
  
  private ReceiveTask parseReceiveTask(XMLStreamReader xtr) {
    ReceiveTask receiveTask = Bpmn2Factory.eINSTANCE.createReceiveTask();
    receiveTask.setName(xtr.getAttributeValue(null, "name"));
    boolean readyWithTask = false;
    try {
      while(readyWithTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "extensionElements".equalsIgnoreCase(xtr.getLocalName())) {
          receiveTask.getActivitiListeners().addAll(parseListeners(xtr));
          
        } else if(xtr.isEndElement() && "receiveTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithTask = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return receiveTask;
  }
  
  private BoundaryEventModel parseBoundaryEvent(XMLStreamReader xtr) {
    BoundaryEvent boundaryEvent = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
    boundaryEvent.setName(xtr.getAttributeValue(null, "name"));
    BoundaryEventModel model = new BoundaryEventModel();
    model.boundaryEvent = boundaryEvent;
    model.attachedRef = xtr.getAttributeValue(null, "attachedToRef");
    boolean readyWithEvent = false;
    try {
      while(readyWithEvent == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "timeDuration".equalsIgnoreCase(xtr.getLocalName())) {
          TimerEventDefinition eventDef = Bpmn2Factory.eINSTANCE.createTimerEventDefinition();
          FormalExpression expression = Bpmn2Factory.eINSTANCE.createFormalExpression();
          expression.setBody(xtr.getElementText());
          eventDef.setTimeDuration(expression);
          boundaryEvent.getEventDefinitions().add(eventDef);
          readyWithEvent = true;
          
        } else if(xtr.isEndElement() && "boundaryEvent".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithEvent = true;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return model;
  }
}
