package org.activiti.graphiti.bpmn.designer.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.SequenceFlow;
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
		if (bo instanceof Task) {
			List<EObject> toDeleteSequenceFlows = new ArrayList<EObject>();
			Task task = (Task) bo;
			for(SequenceFlow incomingSequenceFlow : task.getIncoming()) {
				EObject toDeleteObject = deleteSequenceFlow(incomingSequenceFlow);
				if(toDeleteObject != null) {
					toDeleteSequenceFlows.add(toDeleteObject);
				}
			}
			for(SequenceFlow outgoingSequenceFlow : task.getOutgoing()) {
				EObject toDeleteObject = deleteSequenceFlow(outgoingSequenceFlow);
				if(toDeleteObject != null) {
					toDeleteSequenceFlows.add(toDeleteObject);
				}
			}
			for(EObject deleteObject : toDeleteSequenceFlows) {
				EcoreUtil.delete(deleteObject, true);
			}
		}
		if (bo instanceof EObject) {
			EcoreUtil.delete((EObject) bo, true);
		}
	}
	
	private EObject deleteSequenceFlow(SequenceFlow sequenceFlow) {
		for(EObject diagramObject : getDiagram().eResource().getContents()) {
			if(diagramObject instanceof SequenceFlow) {
			}
			if(diagramObject instanceof SequenceFlow && 
					((SequenceFlow) diagramObject).getId().equals(sequenceFlow.getId())) {
				
				return diagramObject;
			}
		}
		return null;
	}

}
