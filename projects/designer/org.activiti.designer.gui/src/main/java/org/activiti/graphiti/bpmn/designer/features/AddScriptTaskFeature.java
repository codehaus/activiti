package org.activiti.graphiti.bpmn.designer.features;

import org.activiti.graphiti.bpmn.designer.ActivitiImageProvider;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddScriptTaskFeature extends AddTaskFeature {

	public AddScriptTaskFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	protected String getIcon() {
		return ActivitiImageProvider.IMG_SCRIPTTASK;
	}

}
