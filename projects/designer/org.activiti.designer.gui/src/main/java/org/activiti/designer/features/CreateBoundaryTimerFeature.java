package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryTimerFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "boundarytimer";

	public CreateBoundaryTimerFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "BoundaryTimer", "Add boundary timer");
	}

	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess == false) {
      return false;
    }
    return true;
	}

	public Object[] create(ICreateContext context) {
	  BoundaryEvent boundaryEvent = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
		TimerEventDefinition timerEvent = Bpmn2Factory.eINSTANCE.createTimerEventDefinition();
		boundaryEvent.getEventDefinitions().add(timerEvent);
		
		boundaryEvent.setId(getNextId());
		
		getDiagram().eResource().getContents().add(boundaryEvent);

		// do the add
		addGraphicalRepresentation(context, boundaryEvent);
		
		// return newly created business object(s)
		return new Object[] { boundaryEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_ENDEVENT_NONE;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createTimerEventDefinition().getClass();
	}

}
