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

package org.activiti.designer.bpmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.model.GraphicInfo;
import org.activiti.designer.model.SequenceFlowModel;
import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;


/**
 * @author Tijs Rademakers
 */
public class BpmnParser {
  
  public boolean bpmdiInfoFound;
  public List<FlowElement> bpmnList = new ArrayList<FlowElement>();
  public List<SequenceFlowModel> sequenceFlowList = new ArrayList<SequenceFlowModel>();
  public Map<String, GraphicInfo> locationMap = new HashMap<String, GraphicInfo>();
  public Map<String, String> idMap = new HashMap<String, String>();
  
  public void parseBpmn(XMLStreamReader xtr, Diagram diagram, IFeatureProvider featureProvider) {
    try {
      while(xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "definitions".equalsIgnoreCase(xtr.getLocalName())) {
          
        } else if(xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
          
        } else {
          if(xtr.isStartElement() && "startEvent".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            StartEvent startEvent = parseStartEvent(xtr, diagram);
            bpmnList.add(startEvent);
            idMap.put(startEvent.getId(), elementid);
            
          } else if(xtr.isStartElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            UserTask userTask = parseUserTask(xtr, diagram);
            bpmnList.add(userTask);
            idMap.put(userTask.getId(), elementid);
            
          } else if(xtr.isStartElement() && "serviceTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ServiceTask serviceTask = parseServiceTask(xtr, diagram);
            bpmnList.add(serviceTask);
            idMap.put(serviceTask.getId(), elementid);
          
          } else if(xtr.isStartElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ScriptTask scriptTask = parseScriptTask(xtr, diagram);
            bpmnList.add(scriptTask);
            idMap.put(scriptTask.getId(), elementid);
            
          } else if(xtr.isStartElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ManualTask manualTask = parseManualTask(xtr, diagram);
            bpmnList.add(manualTask);
            idMap.put(manualTask.getId(), elementid);
            
          } else if(xtr.isStartElement() && "receiveTask".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ReceiveTask receiveTask = parseReceiveTask(xtr, diagram);
            bpmnList.add(receiveTask);
            idMap.put(receiveTask.getId(), elementid);
            
          } else if(xtr.isStartElement() && "endEvent".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            EndEvent endEvent = parseEndEvent(xtr, diagram);
            bpmnList.add(endEvent);
            idMap.put(endEvent.getId(), elementid);
          
          } else if(xtr.isStartElement() && "exclusiveGateway".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ExclusiveGateway exclusiveGateway = parseExclusiveGateway(xtr, diagram);
            bpmnList.add(exclusiveGateway);
            idMap.put(exclusiveGateway.getId(), elementid);
            
          } else if(xtr.isStartElement() && "parallelGateway".equalsIgnoreCase(xtr.getLocalName())) {
            String elementid = xtr.getAttributeValue(null, "id");
            ParallelGateway parallelGateway = parseParallelGateway(xtr, diagram);
            bpmnList.add(parallelGateway);
            idMap.put(parallelGateway.getId(), elementid);
            
          } else if(xtr.isStartElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
            SequenceFlowModel sequenceFlow = parseSequenceFlow(xtr, diagram, bpmnList, idMap);
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
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private StartEvent parseStartEvent(XMLStreamReader xtr, Diagram diagram) {
    StartEvent startEvent = Bpmn2Factory.eINSTANCE.createStartEvent();
    startEvent.setId(ActivitiUiUtil.getNextId(StartEvent.class, "startevent", diagram));
    startEvent.setName("Start");
    return startEvent;
  }
  
  private EndEvent parseEndEvent(XMLStreamReader xtr, Diagram diagram) {
    EndEvent endEvent = Bpmn2Factory.eINSTANCE.createEndEvent();
    endEvent.setId(ActivitiUiUtil.getNextId(EndEvent.class, "endevent", diagram));
    endEvent.setName("End");
    return endEvent;
  }
  
  private ExclusiveGateway parseExclusiveGateway(XMLStreamReader xtr, Diagram diagram) {
    ExclusiveGateway exclusiveGateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
    exclusiveGateway.setId(ActivitiUiUtil.getNextId(ExclusiveGateway.class, "exclusivegateway", diagram));
    exclusiveGateway.setName(xtr.getAttributeValue(null, "name"));
    return exclusiveGateway;
  }
  
  private ParallelGateway parseParallelGateway(XMLStreamReader xtr, Diagram diagram) {
    ParallelGateway parallelGateway = Bpmn2Factory.eINSTANCE.createParallelGateway();
    parallelGateway.setId(ActivitiUiUtil.getNextId(ParallelGateway.class, "parallelgateway", diagram));
    parallelGateway.setName(xtr.getAttributeValue(null, "name"));
    return parallelGateway;
  }
  
  private SequenceFlowModel parseSequenceFlow(XMLStreamReader xtr, Diagram diagram, 
          List<FlowElement> bpmnList, Map<String, String> idMap) {
    
    SequenceFlowModel sequenceFlow = new SequenceFlowModel();
    sequenceFlow.sourceRef = xtr.getAttributeValue(null, "sourceRef");
    sequenceFlow.targetRef = xtr.getAttributeValue(null, "targetRef");
    return sequenceFlow;
  }
  
  private static UserTask parseUserTask(XMLStreamReader xtr, Diagram diagram) {
    UserTask userTask = Bpmn2Factory.eINSTANCE.createUserTask();
    userTask.setId(ActivitiUiUtil.getNextId(UserTask.class, "usertask", diagram));
    userTask.setName(xtr.getAttributeValue(null, "name"));
    boolean readyWithUserTask = false;
    try {
      while(readyWithUserTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "formalExpression".equalsIgnoreCase(xtr.getLocalName())) {
          CandidateGroup group = Bpmn2Factory.eINSTANCE.createCandidateGroup();
          group.setGroup(xtr.getElementText());
          diagram.eResource().getContents().add(group);
          userTask.getCandidateGroups().add(group);
          readyWithUserTask = true;
        } else if(xtr.isEndElement() && "userTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithUserTask = true;
        }
      }
    } catch(Exception e) {}
    return userTask;
  }
  
  private ScriptTask parseScriptTask(XMLStreamReader xtr, Diagram diagram) {
    ScriptTask scriptTask = Bpmn2Factory.eINSTANCE.createScriptTask();
    scriptTask.setId(ActivitiUiUtil.getNextId(UserTask.class, "scripttask", diagram));
    scriptTask.setName(xtr.getAttributeValue(null, "name"));
    scriptTask.setScriptFormat(xtr.getAttributeValue(null, "scriptFormat"));
    boolean readyWithScriptTask = false;
    try {
      while(readyWithScriptTask == false && xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "script".equalsIgnoreCase(xtr.getLocalName())) {
          scriptTask.setScript(xtr.getElementText());
          readyWithScriptTask = true;
        } else if(xtr.isEndElement() && "scriptTask".equalsIgnoreCase(xtr.getLocalName())) {
          readyWithScriptTask = true;
        }
      }
    } catch(Exception e) {}
    return scriptTask;
  }
  
  private ServiceTask parseServiceTask(XMLStreamReader xtr, Diagram diagram) {
    ServiceTask serviceTask = Bpmn2Factory.eINSTANCE.createServiceTask();
    serviceTask.setId(ActivitiUiUtil.getNextId(ServiceTask.class, "servicetask", diagram));
    serviceTask.setName(xtr.getAttributeValue(null, "name"));
    return serviceTask;
  }
  
  private ManualTask parseManualTask(XMLStreamReader xtr, Diagram diagram) {
    ManualTask manualTask = Bpmn2Factory.eINSTANCE.createManualTask();
    manualTask.setId(ActivitiUiUtil.getNextId(ManualTask.class, "manualtask", diagram));
    manualTask.setName(xtr.getAttributeValue(null, "name"));
    return manualTask;
  }
  
  private ReceiveTask parseReceiveTask(XMLStreamReader xtr, Diagram diagram) {
    ReceiveTask receiveTask = Bpmn2Factory.eINSTANCE.createReceiveTask();
    receiveTask.setId(ActivitiUiUtil.getNextId(ReceiveTask.class, "receivetask", diagram));
    receiveTask.setName(xtr.getAttributeValue(null, "name"));
    return receiveTask;
  }
}
