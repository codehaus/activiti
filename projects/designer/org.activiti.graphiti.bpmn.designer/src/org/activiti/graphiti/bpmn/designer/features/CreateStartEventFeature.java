package org.activiti.graphiti.bpmn.designer.features;

import org.activiti.graphiti.bpmn.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateStartEventFeature extends AbstractCreateBPMNFeature {
	
	private static final String FEATURE_ID_KEY = "startevent";

	public CreateStartEventFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "StartEvent", "Add start event");
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		StartEvent startEvent = Bpmn2Factory.eINSTANCE.createStartEvent();
		
		startEvent.setId(getNextId());
		startEvent.setName("Start");
		
		getDiagram().eResource().getContents().add(startEvent);
		addGraphicalRepresentation(context, startEvent);
		
		// return newly created business object(s)
		return new Object[] { startEvent };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_STARTEVENT_NONE;
	}
	
	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createStartEvent().getClass();
	}

}
