package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateServiceTaskFeature extends AbstractCreateBPMNFeature {

	private static final String FEATURE_ID_KEY = "servicetask";

	private String customServiceTaskName;

	public CreateServiceTaskFeature(IFeatureProvider fp) {
		super(fp, "ServiceTask", "Add service task");
	}

	public CreateServiceTaskFeature(IFeatureProvider fp, String name, String description, String customServiceTaskName) {
		super(fp, name, description);
		this.customServiceTaskName = customServiceTaskName;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		ServiceTask newServiceTask = Bpmn2Factory.eINSTANCE.createServiceTask();

		newServiceTask.setId(getNextId());
		newServiceTask.setName("Service Task");

		if (this.customServiceTaskName != null) {
			newServiceTask.setImplementation("custom:" + this.customServiceTaskName);
		}

		getDiagram().eResource().getContents().add(newServiceTask);

		// do the add
		addGraphicalRepresentation(context, newServiceTask);
		return new Object[] { newServiceTask };
	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_SERVICETASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createServiceTask().getClass();
	}

}