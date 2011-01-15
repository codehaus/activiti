package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateReceiveTaskFeature extends AbstractCreateBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "receivetask";

	public CreateReceiveTaskFeature(IFeatureProvider fp) {
		super(fp, "ReceiveTask", "Add receive task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		ReceiveTask newReceiveTask = Bpmn2Factory.eINSTANCE.createReceiveTask();
		newReceiveTask.setId(getNextId());
		newReceiveTask.setName("Receive Task");
		
		getDiagram().eResource().getContents().add(newReceiveTask);
		
		addGraphicalRepresentation(context, newReceiveTask);
		return new Object[] { newReceiveTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_RECEIVETASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createReceiveTask().getClass();
	}

}
