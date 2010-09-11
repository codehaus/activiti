package org.activiti.graphiti.bpmn.designer.features;

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

		// do the add
		addGraphicalRepresentation(context, startEvent);
		
		// return newly created business object(s)
		return new Object[] { startEvent };
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
