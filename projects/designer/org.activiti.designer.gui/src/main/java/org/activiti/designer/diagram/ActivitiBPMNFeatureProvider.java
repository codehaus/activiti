package org.activiti.designer.diagram;

import org.activiti.designer.features.AddEndEventFeature;
import org.activiti.designer.features.AddExclusiveGatewayFeature;
import org.activiti.designer.features.AddGatewayFeature;
import org.activiti.designer.features.AddMailTaskFeature;
import org.activiti.designer.features.AddManualTaskFeature;
import org.activiti.designer.features.AddScriptTaskFeature;
import org.activiti.designer.features.AddSequenceFlowFeature;
import org.activiti.designer.features.AddServiceTaskFeature;
import org.activiti.designer.features.AddStartEventFeature;
import org.activiti.designer.features.AddSubProcessFeature;
import org.activiti.designer.features.AddUserTaskFeature;
import org.activiti.designer.features.CopyFlowElementFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateSequenceFlowFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateSubProcessFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteFlowElementFeature;
import org.activiti.designer.features.DirectEditFlowElementFeature;
import org.activiti.designer.features.PasteFlowElementFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.activiti.designer.features.UpdateFlowElementFeature;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class ActivitiBPMNFeatureProvider extends DefaultFeatureProvider {

	public ActivitiBPMNFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass?
		if (context.getNewObject() instanceof StartEvent) {
			return new AddStartEventFeature(this);
		} else if (context.getNewObject() instanceof EndEvent) {
			return new AddEndEventFeature(this);
		} else if (context.getNewObject() instanceof SequenceFlow) {
			return new AddSequenceFlowFeature(this);
		} else if (context.getNewObject() instanceof UserTask) {
			return new AddUserTaskFeature(this);
		} else if (context.getNewObject() instanceof ScriptTask) {
			return new AddScriptTaskFeature(this);
		} else if (context.getNewObject() instanceof ServiceTask) {
			return new AddServiceTaskFeature(this);
		} else if (context.getNewObject() instanceof MailTask) {
			return new AddMailTaskFeature(this);
		} else if (context.getNewObject() instanceof ManualTask) {
			return new AddManualTaskFeature(this);
		} else if (context.getNewObject() instanceof ExclusiveGateway) {
			return new AddExclusiveGatewayFeature(this);
		} else if (context.getNewObject() instanceof Gateway) {
			return new AddGatewayFeature(this);
		} else if (context.getNewObject() instanceof SubProcess) {
			return new AddSubProcessFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateStartEventFeature(this), new CreateEndEventFeature(this),
				new CreateUserTaskFeature(this), new CreateScriptTaskFeature(this), new CreateServiceTaskFeature(this),
				new CreateMailTaskFeature(this), new CreateManualTaskFeature(this),
				new CreateParallelGatewayFeature(this), new CreateExclusiveGatewayFeature(this),
				new CreateSubProcessFeature(this) };
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IDeleteFeature ret = new DeleteFlowElementFeature(this);
		return ret;
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		return new CopyFlowElementFeature(this);
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		return new PasteFlowElementFeature(this);
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new CreateSequenceFlowFeature(this) };
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof FlowElement) {
				return new UpdateFlowElementFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof FlowElement) {
			return new DirectEditFlowElementFeature(this);
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[] { new SaveBpmnModelFeature(this) };
	}

}
