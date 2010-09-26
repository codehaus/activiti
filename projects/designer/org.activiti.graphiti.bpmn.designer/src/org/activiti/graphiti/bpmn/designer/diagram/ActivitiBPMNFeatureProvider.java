package org.activiti.graphiti.bpmn.designer.diagram;

import org.activiti.graphiti.bpmn.designer.features.AddEndEventFeature;
import org.activiti.graphiti.bpmn.designer.features.AddGatewayFeature;
import org.activiti.graphiti.bpmn.designer.features.AddScriptTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.AddSequenceFlowFeature;
import org.activiti.graphiti.bpmn.designer.features.AddServiceTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.AddStartEventFeature;
import org.activiti.graphiti.bpmn.designer.features.AddUserTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateEndEventFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateParallelGatewayFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateScriptTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateSequenceFlowFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateServiceTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateStartEventFeature;
import org.activiti.graphiti.bpmn.designer.features.CreateUserTaskFeature;
import org.activiti.graphiti.bpmn.designer.features.DeleteFlowElementFeature;
import org.activiti.graphiti.bpmn.designer.features.DirectEditFlowElementFeature;
import org.activiti.graphiti.bpmn.designer.features.SaveBpmnModelFeature;
import org.activiti.graphiti.bpmn.designer.features.UpdateFlowElementFeature;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
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
		} else if (context.getNewObject() instanceof Gateway) {
			return new AddGatewayFeature(this);
		}
        return super.getAddFeature(context);
    }
    
    @Override
    public ICreateFeature[] getCreateFeatures() {
        return new ICreateFeature[] { new CreateStartEventFeature(this), new CreateEndEventFeature(this), 
        		new CreateUserTaskFeature(this), new CreateScriptTaskFeature(this), 
        		new CreateServiceTaskFeature(this), new CreateParallelGatewayFeature(this)};
    }
    
    @Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IDeleteFeature ret = new DeleteFlowElementFeature(this);
		return ret;
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
		return new ICustomFeature[] { new SaveBpmnModelFeature(this)};
	}

}


