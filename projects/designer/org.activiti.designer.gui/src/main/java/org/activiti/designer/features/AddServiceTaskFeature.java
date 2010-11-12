package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.property.extension.CustomServiceTaskContext;
import org.activiti.designer.property.extension.ExtensionUtil;
import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddServiceTaskFeature extends AddTaskFeature {

	public AddServiceTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected String getIcon(EObject bo) {

		if (ExtensionUtil.isCustomServiceTask(bo)) {
			final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil
					.getCustomServiceTaskContexts(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));
			for (CustomServiceTaskContext customServiceTaskContext : customServiceTaskContexts) {
				String impl = ((ServiceTask) bo).getImplementation();
				// TODO: string separator
				impl = impl.substring(impl.indexOf(":") + 1, impl.length());
				if (impl.equals(customServiceTaskContext.getServiceTask().getClass().getCanonicalName())) {
					return customServiceTaskContext.getImageKey();
				}
			}
		}
		return ActivitiImageProvider.IMG_SERVICETASK;
	}
}