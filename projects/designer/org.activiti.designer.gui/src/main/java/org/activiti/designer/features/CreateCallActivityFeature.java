package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateCallActivityFeature extends AbstractCreateBPMNFeature {

	public static final String FEATURE_ID_KEY = "callactivity";

	public CreateCallActivityFeature(IFeatureProvider fp) {
		super(fp, "CallActivity", "Add call activity");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		// TODO: revisit once lanes and pools have been added
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		CallActivity callActivity = Bpmn2Factory.eINSTANCE.createCallActivity();
		callActivity.setId(getNextId());
		callActivity.setName("Call activity");

		getDiagram().eResource().getContents().add(callActivity);

		// do the add
		addGraphicalRepresentation(context, callActivity);

		// return newly created business object(s)
		return new Object[] { callActivity };

	}

	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_SUBPROCESS_COLLAPSED;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return Bpmn2Factory.eINSTANCE.createCallActivity().getClass();
	}

}
