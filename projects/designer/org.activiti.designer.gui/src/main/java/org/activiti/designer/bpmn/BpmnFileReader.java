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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.model.GraphicInfo;
import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;


/**
 * @author Tijs Rademakers
 */
public class BpmnFileReader {

  public static void readBpmn(String filename, Diagram diagram, IFeatureProvider featureProvider) {
    File bpmnFile = new File(filename);
    if(bpmnFile.exists() == false) {
      System.out.println("bpmn file does not exist " + filename);
      return;
    }
    String processName = filename.substring(filename.lastIndexOf(File.separator) + 1);
    processName = processName.replace(".xml", "");
    processName = processName.replace(".bpmn20", "");
    try {
      FileInputStream inStream = new FileInputStream(bpmnFile);
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(inStream, "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);
      boolean definitionsTagFound = false;
      boolean processTagFound = false;
      List<FlowElement> bpmnList = new ArrayList<FlowElement>();
      Map<String, GraphicInfo> locationMap = new HashMap<String, GraphicInfo>();
      Map<String, String> idMap = new HashMap<String, String>();
      while(xtr.hasNext()) {
        xtr.next();
        if(xtr.isStartElement() && "definitions".equalsIgnoreCase(xtr.getLocalName())) {
          definitionsTagFound = true;
        } else if(xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
          processTagFound = true;
          org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
          process.setId(processName);
          process.setName(processName);
          Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
          documentation.setId("documentation_process");
          documentation.setText("");
          process.getDocumentation().add(documentation);
          diagram.eResource().getContents().add(process);
          
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
            
          } else if(xtr.isStartElement() && "manualTask".equalsIgnoreCase(xtr.getLocalName())) {
            
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
            
          } else if(xtr.isStartElement() && "sequenceFlow".equalsIgnoreCase(xtr.getLocalName())) {
            SequenceFlow sequenceFlow = parseSequenceFlow(xtr, diagram, bpmnList, idMap);
            bpmnList.add(sequenceFlow);
            
          } else if(xtr.isStartElement() && "BPMNShape".equalsIgnoreCase(xtr.getLocalName())) {
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
        if(flowElement instanceof SequenceFlow) {
          SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
          AddConnectionContext addContext = new AddConnectionContext(null, null);
          addContext.setNewObject(sequenceFlow);
          addContext.setTargetContainer(diagram);
          featureProvider.addIfPossible(addContext);
          
        } else {
          GraphicInfo graphicInfo = locationMap.get(idMap.get(flowElement.getId()));
          addBpmnElementToDiagram(flowElement, graphicInfo, diagram, featureProvider);
        }
      }
      xtr.close();
      in.close();
      inStream.close();
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private static StartEvent parseStartEvent(XMLStreamReader xtr, Diagram diagram) {
    StartEvent startEvent = Bpmn2Factory.eINSTANCE.createStartEvent();
    startEvent.setId(ActivitiUiUtil.getNextId(StartEvent.class, "startevent", diagram));
    startEvent.setName("Start");
    return startEvent;
  }
  
  private static EndEvent parseEndEvent(XMLStreamReader xtr, Diagram diagram) {
    EndEvent endEvent = Bpmn2Factory.eINSTANCE.createEndEvent();
    endEvent.setId(ActivitiUiUtil.getNextId(EndEvent.class, "endevent", diagram));
    endEvent.setName("End");
    return endEvent;
  }
  
  private static ExclusiveGateway parseExclusiveGateway(XMLStreamReader xtr, Diagram diagram) {
    ExclusiveGateway exclusiveGateway = Bpmn2Factory.eINSTANCE.createExclusiveGateway();
    exclusiveGateway.setId(ActivitiUiUtil.getNextId(EndEvent.class, "exclusivegateway", diagram));
    exclusiveGateway.setName(xtr.getAttributeValue(null, "name"));
    return exclusiveGateway;
  }
  
  private static SequenceFlow parseSequenceFlow(XMLStreamReader xtr, Diagram diagram, 
          List<FlowElement> bpmnList, Map<String, String> idMap) {
    
    SequenceFlow sequenceFlow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
    sequenceFlow.setId(ActivitiUiUtil.getNextId(SequenceFlow.class, "flow", diagram));
    String sourceRef = xtr.getAttributeValue(null, "sourceRef");
    sequenceFlow.setSourceRef(getFlowNode(sourceRef, bpmnList, idMap));
    String targetRef = xtr.getAttributeValue(null, "targetRef");
    sequenceFlow.setTargetRef(getFlowNode(targetRef, bpmnList, idMap));
    diagram.eResource().getContents().add(sequenceFlow);
    return sequenceFlow;
  }
  
  private static FlowNode getFlowNode(String elementid, List<FlowElement> bpmnList, Map<String, String> idMap) {
    FlowNode flowNode = null;
    for(String key : idMap.keySet()) {
      if(idMap.get(key).equalsIgnoreCase(elementid)) {
        for(FlowElement flowElement : bpmnList) {
          if(flowElement.getId().equalsIgnoreCase(key)) {
            flowNode = (FlowNode) flowElement;
          }
        }
      }
    }
    return flowNode;
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
  
  private static ServiceTask parseServiceTask(XMLStreamReader xtr, Diagram diagram) {
    ServiceTask serviceTask = Bpmn2Factory.eINSTANCE.createServiceTask();
    serviceTask.setId(ActivitiUiUtil.getNextId(ServiceTask.class, "servicetask", diagram));
    serviceTask.setName(xtr.getAttributeValue(null, "name"));
    return serviceTask;
  }
  
  private static void addBpmnElementToDiagram(FlowElement flowElement, GraphicInfo graphicInfo,
          Diagram diagram, IFeatureProvider featureProvider) {
    AddContext addContext = new AddContext();
    addContext.setNewObject(flowElement);
    addContext.setTargetContainer(diagram);
    addContext.setX(graphicInfo.x);
    if(flowElement instanceof StartEvent || flowElement instanceof EndEvent) {
      if(graphicInfo.height < 55) {
        addContext.setY(graphicInfo.y - 25);
      } else {
        addContext.setY(graphicInfo.y);
      }
    } else if(flowElement instanceof ExclusiveGateway) {
      if(graphicInfo.height < 60) {
        addContext.setY(graphicInfo.y - 20);
      } else {
        addContext.setY(graphicInfo.y);
      }
    } else {
      addContext.setY(graphicInfo.y);
    }
   
    IAddFeature addFeature = featureProvider.getAddFeature(addContext);
    if (addFeature.canAdd(addContext)) {
      addFeature.add(addContext);
    }
  }
}
