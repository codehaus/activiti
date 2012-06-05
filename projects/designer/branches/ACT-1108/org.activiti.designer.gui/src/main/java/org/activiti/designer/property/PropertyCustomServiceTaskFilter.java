package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyCustomServiceTaskFilter extends ActivitiPropertyFilter {

	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof ServiceTask && ExtensionUtil.isCustomServiceTask(bo)) {
			return true;
		}
		return false;
	}

}
