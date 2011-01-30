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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.bpmn.GraphicInfo;
import org.activiti.designer.eclipse.bpmn.SequenceFlowModel;
import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.Task;
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
  
  private static final int START_X = 30;
  private static final int START_Y = 200;
  private static final int EVENT_WIDTH = 55;
  private static final int EVENT_HEIGHT = 55;
  private static final int TASK_WIDTH = 105;
  private static final int TASK_HEIGHT = 55;
  private static final int GATEWAY_WIDTH = 60;
  private static final int GATEWAY_HEIGHT = 60;
  private static final int SEQUENCEFLOW_WIDTH = 60;
  private static final int GATEWAY_CHILD_HEIGHT = 100;

  public static void readBpmn(String filename, Diagram diagram, IFeatureProvider featureProvider) {
    File bpmnFile = new File(filename);
    if(bpmnFile.exists() == false) {
      System.out.println("bpmn file does not exist " + filename);
      return;
    }
    String processName = filename.substring(filename.lastIndexOf(File.separator) + 1);
    processName = processName.substring(0, processName.indexOf("."));
    try {
      FileInputStream inStream = new FileInputStream(bpmnFile);
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(inStream, "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);
      BpmnParser bpmnParser = new BpmnParser();
      bpmnParser.parseBpmn(xtr);
      
      if(bpmnParser.bpmnList.size() == 0) return;
      
      org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
      process.setId(processName);
      process.setName(processName);
      Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
      documentation.setId("documentation_process");
      documentation.setText("");
      process.getDocumentation().add(documentation);
      if(bpmnParser.process != null && bpmnParser.process.getExecutionListeners().size() > 0) {
        process.getExecutionListeners().addAll(bpmnParser.process.getExecutionListeners());
      }
      diagram.eResource().getContents().add(process);
      
      if(bpmnParser.bpmdiInfoFound == true) {
        drawDiagramWithBPMNDI(diagram, featureProvider, bpmnParser.bpmnList, bpmnParser.sequenceFlowList,
                bpmnParser.locationMap);
      } else {
       
        Map<String, GraphicInfo> yMap = new HashMap<String, GraphicInfo>();
        for (FlowElement flowElement : bpmnParser.bpmnList) {
          
          FlowElement sourceElement = sourceRef(flowElement.getId(), bpmnParser);
          GraphicInfo graphicInfo = getNextGraphicInfo(sourceElement, flowElement, yMap, bpmnParser);
        
          yMap.put(flowElement.getId(), graphicInfo);
          addBpmnElementToDiagram(flowElement, graphicInfo, diagram, featureProvider);
        }
        drawSequenceFlows(diagram, featureProvider, bpmnParser.bpmnList, 
                bpmnParser.sequenceFlowList);
      }
      setFriendlyIds(bpmnParser.bpmnList, diagram);
      xtr.close();
      in.close();
      inStream.close();
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private static GraphicInfo getNextGraphicInfo(FlowElement sourceFlowElement, FlowElement newFlowElement,
          Map<String, GraphicInfo> yMap, BpmnParser parser) {
    
    GraphicInfo graphicInfo = new GraphicInfo();
    GraphicInfo sourceInfo = null;
    if(sourceFlowElement != null) {
      if(yMap.containsKey(sourceFlowElement.getId())) {
        sourceInfo = yMap.get(sourceFlowElement.getId());
      }
    }
    
    int x = 0;
    int y = 0;
    if(sourceInfo != null) {
      x = sourceInfo.x;
      y = sourceInfo.y;
      
      if(sourceFlowElement instanceof Event) {
        x += EVENT_WIDTH;
      } else if(sourceFlowElement instanceof Gateway) {
        x += GATEWAY_WIDTH;
        y = calculateDirectGatewayChildY(sourceFlowElement.getId(), newFlowElement.getId(), sourceInfo.y, parser.sequenceFlowList);
      } else {
        x += TASK_WIDTH;
      }
      x += SEQUENCEFLOW_WIDTH;
      
      if(newFlowElement instanceof Gateway) {
        if(isEndGateway(newFlowElement.getId(), parser.sequenceFlowList)) {
          y = START_Y;
          x = getMaxX(newFlowElement.getId(), yMap, parser);
        }
      }
      
    } else {
      x = START_X;
      y = START_Y;
    }
    
    graphicInfo.x = x;
    graphicInfo.y = y;
    
    if(newFlowElement instanceof Event) {
      graphicInfo.height = EVENT_HEIGHT;
    } else if(newFlowElement instanceof Gateway) {
      graphicInfo.height = GATEWAY_HEIGHT;
      graphicInfo.y -= 3;
    } else {
      graphicInfo.height = TASK_HEIGHT;
    }
    
    return graphicInfo;
  }
  
  private static FlowElement sourceRef(String id, BpmnParser parser) {
    FlowElement sourceRef = null;
    String sourceRefString = null;
    for (SequenceFlowModel sequenceFlowModel : parser.sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(id)) {
        sourceRefString = sequenceFlowModel.sourceRef;
      }
    }
    if(sourceRefString != null) {
      for (FlowElement flowElement : parser.bpmnList) {
        if(flowElement.getId().equals(sourceRefString)) {
          sourceRef = flowElement;
        }
      }
    }
    return sourceRef;
  }
  
  private static int getMaxX(String id, Map<String, GraphicInfo> yMap, BpmnParser parser) {
    int maxX = 0;
    String sourceRef = null;
    for (SequenceFlowModel sequenceFlowModel : parser.sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(id)) {
        if(yMap.containsKey(sequenceFlowModel.sourceRef)) {
          int sourceX = yMap.get(sequenceFlowModel.sourceRef).x;
          if(sourceX > maxX) {
            maxX = sourceX;
            sourceRef = sequenceFlowModel.sourceRef;
          }
        }
      }
    }
    if(sourceRef != null) {
      for (FlowElement flowElement : parser.bpmnList) {
        if(flowElement.getId().equals(sourceRef)) {
          if(flowElement instanceof Event) {
            maxX += EVENT_WIDTH;
          } else if(flowElement instanceof Gateway) {
            maxX += GATEWAY_WIDTH;
          } else if(flowElement instanceof Task) {
            maxX += TASK_WIDTH;
          }
        }
      }
    }
    maxX += SEQUENCEFLOW_WIDTH;
    return maxX;
  }
  
  private static int calculateDirectGatewayChildY(String gatewayid, String id, int gatewayY, List<SequenceFlowModel> sequenceFlowList) {
    int y = 0;
    int counter = 0;
    int index = 0;
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.sourceRef.equals(gatewayid)) {
        counter++;
      }
      if(sequenceFlowModel.targetRef.equals(id)) {
        index = counter;
      }
    }
    
    if(counter % 2 == 0) {
      if(index > (counter / 2)) {
        y = gatewayY + ((index - (counter / 2)) * GATEWAY_CHILD_HEIGHT);
      } else {
        y = gatewayY - (((counter / 2) - index + 1) * GATEWAY_CHILD_HEIGHT);
      }
    } else {
      int middle = ((counter - 1) / 2) + 1;
      if(index > middle) {
        y = gatewayY + ((index - middle) * GATEWAY_CHILD_HEIGHT);
      } else if(index == middle) {
        y = gatewayY;
      } else {
        y = gatewayY - ((middle - index + 1) * GATEWAY_CHILD_HEIGHT);
      }
    }
    return y;
  }
  
  private static boolean isEndGateway(String id, List<SequenceFlowModel> sequenceFlowList) {
    boolean isEnd = false;
    int counter = 0;
    for (SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      if(sequenceFlowModel.targetRef.equals(id)) {
        counter++;
      }
    }
    if(counter > 1) {
      isEnd = true;
    }
    return isEnd;
  }
  
  private static void drawDiagramWithBPMNDI(Diagram diagram, IFeatureProvider featureProvider, List<FlowElement> bpmnList, 
          List<SequenceFlowModel> sequenceFlowList, Map<String, GraphicInfo> locationMap) {
    
    for (FlowElement flowElement : bpmnList) {
      String elementid = flowElement.getId();
      GraphicInfo graphicInfo = locationMap.get(elementid);
      addBpmnElementToDiagram(flowElement, graphicInfo, diagram, featureProvider);
    }
    drawSequenceFlows(diagram, featureProvider, bpmnList, sequenceFlowList);
  }
  
  private static void setFriendlyIds(List<FlowElement> bpmnList, Diagram diagram) {
    for (FlowElement flowElement : bpmnList) {
      if(flowElement instanceof StartEvent) {
        flowElement.setId(ActivitiUiUtil.getNextId(StartEvent.class, "startevent", diagram));
      } else if(flowElement instanceof EndEvent) {
        flowElement.setId(ActivitiUiUtil.getNextId(EndEvent.class, "endevent", diagram));
      } else if(flowElement instanceof ExclusiveGateway) {
        flowElement.setId(ActivitiUiUtil.getNextId(ExclusiveGateway.class, "exclusivegateway", diagram));
      } else if(flowElement instanceof ParallelGateway) {
        flowElement.setId(ActivitiUiUtil.getNextId(ParallelGateway.class, "parallelgateway", diagram));
      } else if(flowElement instanceof UserTask) {
        flowElement.setId(ActivitiUiUtil.getNextId(UserTask.class, "usertask", diagram));
      } else if(flowElement instanceof ScriptTask) {
        flowElement.setId(ActivitiUiUtil.getNextId(ScriptTask.class, "scripttask", diagram));
      } else if(flowElement instanceof ServiceTask) {
        flowElement.setId(ActivitiUiUtil.getNextId(ServiceTask.class, "servicetask", diagram));
      } else if(flowElement instanceof ManualTask) {
        flowElement.setId(ActivitiUiUtil.getNextId(ManualTask.class, "manualtask", diagram));
      } else if(flowElement instanceof ReceiveTask) {
        flowElement.setId(ActivitiUiUtil.getNextId(ReceiveTask.class, "receivetask", diagram));
      }
    }
  }
  
  private static void drawSequenceFlows(Diagram diagram, IFeatureProvider featureProvider, List<FlowElement> bpmnList, 
          List<SequenceFlowModel> sequenceFlowList) {
    
    for(SequenceFlowModel sequenceFlowModel : sequenceFlowList) {
      SequenceFlow sequenceFlow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
      sequenceFlow.setId(ActivitiUiUtil.getNextId(SequenceFlow.class, "flow", diagram));
      sequenceFlow.setSourceRef(getFlowNode(sequenceFlowModel.sourceRef, bpmnList));
      sequenceFlow.setTargetRef(getFlowNode(sequenceFlowModel.targetRef, bpmnList));
      if(sequenceFlowModel.conditionExpression != null) {
        sequenceFlow.setConditionExpression(sequenceFlowModel.conditionExpression);
      }
      if(sequenceFlowModel.listenerList.size() > 0) {
        sequenceFlow.getExecutionListeners().addAll(sequenceFlowModel.listenerList);
      }
      diagram.eResource().getContents().add(sequenceFlow);
      AddConnectionContext addContext = new AddConnectionContext(null, null);
      addContext.setNewObject(sequenceFlow);
      addContext.setTargetContainer(diagram);
      featureProvider.addIfPossible(addContext);
    }
  }
  
  private static FlowNode getFlowNode(String elementid, List<FlowElement> bpmnList) {
    FlowNode flowNode = null;
    for(FlowElement flowElement : bpmnList) {
      if(flowElement.getId().equalsIgnoreCase(elementid)) {
        flowNode = (FlowNode) flowElement;
      }
    }
    return flowNode;
  }
  
  private static void addBpmnElementToDiagram(FlowElement flowElement, GraphicInfo graphicInfo,
          Diagram diagram, IFeatureProvider featureProvider) {
    
    if(flowElement instanceof UserTask) {
      UserTask userTask = (UserTask) flowElement;
      if(userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
        for (CandidateUser candidateUser : userTask.getCandidateUsers()) {
          diagram.eResource().getContents().add(candidateUser);
        }
      }
      if(userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
        for (CandidateGroup candidateGroup : userTask.getCandidateGroups()) {
          diagram.eResource().getContents().add(candidateGroup);
        }
      }
    } else if(flowElement instanceof ServiceTask) {
      ServiceTask serviceTask = (ServiceTask) flowElement;
      if(serviceTask.getFieldExtensions() != null && serviceTask.getFieldExtensions().size() > 0) {
        for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
          diagram.eResource().getContents().add(fieldExtension);
        }
      }
    }
    
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
    } else if(flowElement instanceof ExclusiveGateway || flowElement instanceof ParallelGateway) {
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
