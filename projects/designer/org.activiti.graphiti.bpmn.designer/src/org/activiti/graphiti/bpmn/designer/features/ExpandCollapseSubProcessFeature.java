package org.activiti.graphiti.bpmn.designer.features;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class ExpandCollapseSubProcessFeature extends AbstractCustomFeature {

	public ExpandCollapseSubProcessFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Expand/Collapse Sub Process"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Expand or collapse the sub process"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return ActivitiUiUtil.contextPertainsToBusinessObject(context, SubProcessImpl.class);
	}

	public void execute(ICustomContext context) {
		try {
			System.out.println("Expanding or Collapsing");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
