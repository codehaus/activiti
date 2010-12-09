package org.activiti.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.property.extension.ExtensionUtil;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DeleteFlowElementFeature extends DefaultDeleteFeature {

	public DeleteFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected void deleteBusinessObject(Object bo) {
		if (bo instanceof Task || bo instanceof Gateway || bo instanceof Event || bo instanceof SubProcess) {
			List<EObject> toDeleteSequenceFlows = new ArrayList<EObject>();
			FlowNode flowNode = (FlowNode) bo;
			for (SequenceFlow incomingSequenceFlow : flowNode.getIncoming()) {
				EObject toDeleteObject = deleteSequenceFlow(incomingSequenceFlow);
				if (toDeleteObject != null) {
					toDeleteSequenceFlows.add(toDeleteObject);
				}
			}
			for (SequenceFlow outgoingSequenceFlow : flowNode.getOutgoing()) {
				EObject toDeleteObject = deleteSequenceFlow(outgoingSequenceFlow);
				if (toDeleteObject != null) {
					toDeleteSequenceFlows.add(toDeleteObject);
				}
			}
			for (EObject deleteObject : toDeleteSequenceFlows) {
				EcoreUtil.delete(deleteObject, true);
			}
		}

		if (bo instanceof EObject) {

			// If this is a custom service task, all of the linked custom
			// properties should also be removed
			if (bo instanceof ServiceTask && ExtensionUtil.isCustomServiceTask((EObject) bo)) {

				final List<EObject> toDeleteCustomProperties = new ArrayList<EObject>();

				final ServiceTask serviceTask = (ServiceTask) bo;
				for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
					toDeleteCustomProperties.add(customProperty);
				}

				for (final EObject deleteObject : toDeleteCustomProperties) {
					EcoreUtil.delete(deleteObject, true);
				}

			}

			EcoreUtil.delete((EObject) bo, true);
		}
	}

	private EObject deleteSequenceFlow(SequenceFlow sequenceFlow) {
		for (EObject diagramObject : getDiagram().eResource().getContents()) {
			if (diagramObject instanceof SequenceFlow) {
			}
			if (diagramObject instanceof SequenceFlow
					&& ((SequenceFlow) diagramObject).getId().equals(sequenceFlow.getId())) {

				return diagramObject;
			}
		}
		return null;
	}

}
