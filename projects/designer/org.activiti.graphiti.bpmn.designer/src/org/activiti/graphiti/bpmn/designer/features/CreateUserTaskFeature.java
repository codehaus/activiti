package org.activiti.graphiti.bpmn.designer.features;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateUserTaskFeature extends AbstractCreateBPMNFeature {
	
	private static final String FEATURE_ID_KEY = "usertask";

	public CreateUserTaskFeature(IFeatureProvider fp) {
		super(fp, "UserTask", "Add user task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		UserTask newUserTask = Bpmn2Factory.eINSTANCE.createUserTask();
		
		newUserTask.setId(getNextId());
		newUserTask.setName("User Task");
		
		getDiagram().eResource().getContents().add(newUserTask);
		
		addGraphicalRepresentation(context, newUserTask);
		return new Object[] { newUserTask };
	}
	
	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createUserTask().getClass();
	}

}
