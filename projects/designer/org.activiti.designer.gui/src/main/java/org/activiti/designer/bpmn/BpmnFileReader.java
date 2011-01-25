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
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.bpmn.GraphicInfo;
import org.activiti.designer.eclipse.bpmn.SequenceFlowModel;
import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
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
  private static final int START_Y = 100;
  private static final int EVENT_WIDTH = 55;
  private static final int EVENT_HEIGHT = 55;
  private static final int TASK_WIDTH = 105;
  private static final int TASK_HEIGHT = 105;
  private static final int SEQUENCEFLOW_WIDTH = 60;

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
      bpmnParser.parseBpmn(xtr, diagram);
      
      if(bpmnParser.bpmnList.size() == 0) return;
      
      org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
      process.setId(processName);
      process.setName(processName);
      Documentation documentation = Bpmn2Factory.eINSTANCE.createDocumentation();
      documentation.setId("documentation_process");
      documentation.setText("");
      process.getDocumentation().add(documentation);
      diagram.eResource().getContents().add(process);
      
      if(bpmnParser.bpmdiInfoFound == true) {
        drawDiagramWithBPMNDI(diagram, featureProvider, bpmnParser.bpmnList, bpmnParser.sequenceFlowList,
                bpmnParser.locationMap);
      } else {
        int currentX = START_X;
        for (FlowElement flowElement : bpmnParser.bpmnList) {
          GraphicInfo graphicInfo = new GraphicInfo();
          if(flowElement instanceof StartEvent) {
            graphicInfo.height = EVENT_HEIGHT;
            graphicInfo.x = currentX;
            graphicInfo.y = START_Y;
            currentX += EVENT_WIDTH + SEQUENCEFLOW_WIDTH;
            
          } else if(flowElement instanceof Task) {
            graphicInfo.height = TASK_HEIGHT;
            graphicInfo.x = currentX;
            graphicInfo.y = START_Y;
            currentX += TASK_WIDTH + SEQUENCEFLOW_WIDTH;
            
          } else if(flowElement instanceof EndEvent) {
            graphicInfo.height = EVENT_HEIGHT;
            graphicInfo.x = currentX;
            graphicInfo.y = START_Y;
          }
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
