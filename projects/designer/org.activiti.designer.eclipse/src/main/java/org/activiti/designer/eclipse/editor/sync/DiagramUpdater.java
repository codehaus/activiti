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

/**
 * @Autor Yvo Swillens
 *
 * Updates existing (Flow)elements in Activiti Diagram
 * based on elements in BPMN2.0.xml 
 */
package org.activiti.designer.eclipse.editor.sync;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.designer.eclipse.bpmn.BpmnParser;
import org.activiti.designer.eclipse.util.ActivitiUiUtil;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * @author Yvo Swillens
 */
public class DiagramUpdater {

  public static void syncDiagram(DiagramEditor diagramEditor, Diagram diagram, IStorage bpmnStorage) {

    if (diagramEditor == null) {
      System.out.println("diagramEditor cannot be null");
      return;
    }
    if (diagram == null) {
      System.out.println("diagram cannot be null");
      return;
    }
    if (bpmnStorage == null) {
      System.out.println("bpmnStorage cannot be null");
      return;
    }

    TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
    IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
            "org.activiti.designer.diagram.ActivitiBPMNDiagramTypeProvider"); //$NON-NLS-1$
    IFeatureProvider featureProvider = dtp.getFeatureProvider();

    List<FlowElement> bpmnFlowElements = readBpmn(editingDomain, diagram, bpmnStorage);

    if (bpmnFlowElements == null || bpmnFlowElements.size() == 0)
      return;

    HashMap<EObject, FlowElement> modifyList = new HashMap<EObject, FlowElement>();

    for (FlowElement bpmnFlowElement : bpmnFlowElements) {
      EObject diagramFlowElement = lookupFlowElementInDiagram(bpmnFlowElement, diagram);
      if (diagramFlowElement != null) {
        modifyList.put(diagramFlowElement, bpmnFlowElement);
      }
    }

    updateFlowElementsInDiagram(editingDomain, modifyList, featureProvider);
  }

  private static List<FlowElement> readBpmn(TransactionalEditingDomain editingDomain, Diagram diagram, IStorage bpmnStorage) {

    BpmnParser bpmnParser = new BpmnParser();
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(bpmnStorage.getContents(), "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);

      bpmnParser.parseBpmn(xtr, diagram);

      xtr.close();
      in.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return bpmnParser.bpmnList;
  }

  private static EObject lookupFlowElementInDiagram(FlowElement sourceElement, Diagram diagram) {

    EObject modifyElement = null;
    for (EObject targetElement : diagram.eResource().getContents()) {

      if ((targetElement instanceof FlowElement) && flowIdIsEqual(sourceElement, (FlowElement) targetElement)) {
        System.out.println("recognized " + sourceElement.getClass() + " with id: " + sourceElement.getId());
        modifyElement = targetElement;
        break;
      }

      /*
       * if (sourceElement instanceof StartEvent && targetElement instanceof
       * StartEvent) { System.out.println("found StartEvent");
       * 
       * if (flowIdIsEqual(sourceElement, (StartEvent) targetElement)) {
       * System.out.println("recognized StartEvent with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; }
       * 
       * } else if (sourceElement instanceof EndEvent && targetElement
       * instanceof EndEvent) { System.out.println("found EndEvent");
       * 
       * if (flowIdIsEqual(sourceElement, (EndEvent) targetElement)) {
       * System.out.println("recognized EndEvent with id: " +
       * sourceElement.getId()); if
       * (FlowElementUtil.elementsNeedUpdate(sourceElement, (EndEvent)
       * targetElement, FlowElementUtil.StringField.NAME)) // if
       * (elementsNeedUpdate(sourceElement, (EndEvent) targetElement, //
       * "name")) { // modifyElement = targetElement; // } break; } } else if
       * (sourceElement instanceof ExclusiveGateway && targetElement instanceof
       * ExclusiveGateway) { System.out.println("found ExclusiveGateway");
       * 
       * if (flowIdIsEqual(sourceElement, (ExclusiveGateway) targetElement)) {
       * System.out.println("recognized ExclusiveGateway with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof ParallelGateway && targetElement
       * instanceof ParallelGateway) {
       * System.out.println("found ParallelGateway");
       * 
       * if (flowIdIsEqual(sourceElement, (ParallelGateway) targetElement)) {
       * System.out.println("recognized ParallelGateway with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof UserTask && targetElement instanceof
       * UserTask) { System.out.println("found UserTask");
       * 
       * if (flowIdIsEqual(sourceElement, (UserTask) targetElement)) {
       * System.out.println("recognized UserTask with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof ScriptTask && targetElement instanceof
       * ScriptTask) { System.out.println("found ScriptTask");
       * 
       * if (flowIdIsEqual(sourceElement, (ScriptTask) targetElement)) {
       * System.out.println("recognized ScriptTask with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof ServiceTask && targetElement instanceof
       * ServiceTask) { System.out.println("found ServiceTask");
       * 
       * if (flowIdIsEqual(sourceElement, (ServiceTask) targetElement)) {
       * System.out.println("recognized ServiceTask with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof ManualTask && targetElement instanceof
       * ManualTask) { System.out.println("found ManualTask");
       * 
       * if (flowIdIsEqual(sourceElement, (ManualTask) targetElement)) {
       * System.out.println("recognized ManualTask with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } } else
       * if (sourceElement instanceof ReceiveTask && targetElement instanceof
       * ReceiveTask) { System.out.println("found ReceiveTask");
       * 
       * if (flowIdIsEqual(sourceElement, (ReceiveTask) targetElement)) {
       * System.out.println("recognized ReceiveTask with id: " +
       * sourceElement.getId()); modifyElement = targetElement; break; } }
       */
    }
    return modifyElement;
  }

  private static boolean flowIdIsEqual(FlowElement f1, FlowElement f2) {

    return (((FlowElement) f1).getId().equalsIgnoreCase(f2.getId())) ? true : false;
  }

  private static void updateFlowElementsInDiagram(TransactionalEditingDomain editingDomain, final HashMap<EObject, FlowElement> modifyList,
          final IFeatureProvider featureProvider) {

    System.out.println("start updating diagram models");

    ActivitiUiUtil.runModelChange(new Runnable() {

      public void run() {

        Iterator<Map.Entry<EObject, FlowElement>> it = modifyList.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<EObject, FlowElement> entry = it.next();

          ((FlowElement) entry.getKey()).setName(entry.getValue().getName());

          if (entry.getKey() instanceof UserTask) {
            // ((UserTask) entry.getValue()).getCandidateGroups();
          }

          if (entry.getKey() instanceof ScriptTask) {

            ((ScriptTask) entry.getKey()).setScriptFormat(((ScriptTask) entry.getValue()).getScriptFormat());
            ((ScriptTask) entry.getKey()).setScript(((ScriptTask) entry.getValue()).getScript());
          }

          updatePictogramContext((FlowElement) entry.getKey(), featureProvider);
        }

      }
    }, editingDomain, "Diagram Models Update");
  }

  private static void updatePictogramContext(FlowElement source, IFeatureProvider featureProvider) {

    PictogramElement pictoElem = featureProvider.getPictogramElementForBusinessObject(source);
    UpdateContext context = new UpdateContext(pictoElem);
    IUpdateFeature feature = featureProvider.getUpdateFeature(context);
    if (feature.canUpdate(context)) {
      IReason reason = feature.updateNeeded(context);
      if (reason.toBoolean()) {
        System.out.println("Updating picto element for " + source.getId() + " because " + reason.getText());
        feature.update(context);
      }
    }
  }
}
