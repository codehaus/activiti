package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateParallelGatewayFeature extends AbstractCreateBPMNFeature {
	
	private static final String FEATURE_ID_KEY = "parallelgateway";

	public CreateParallelGatewayFeature(IFeatureProvider fp) {
		// set name and description of the creation feature
		super(fp, "ParallelGateway", "Add parallel gateway");
	}

	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) {
		ParallelGateway parallelGateway = Bpmn2Factory.eINSTANCE.createParallelGateway();
		parallelGateway.setId(getNextId());
		parallelGateway.setName("Parallel Gateway");
		
		getDiagram().eResource().getContents().add(parallelGateway);
		
		addGraphicalRepresentation(context, parallelGateway);
		return new Object[] { parallelGateway };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_GATEWAY_PARALLEL;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createParallelGateway().getClass();
	}
	
}
