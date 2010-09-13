package org.activiti.graphiti.bpmn.designer.features;

import org.activiti.graphiti.bpmn.designer.xml.BpmnXMLExport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class SaveBpmnModelFeature extends AbstractCustomFeature {

	public SaveBpmnModelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Save to bpmn 2.0"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Generate the bpmn 2.0 xml file"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	public void execute(ICustomContext context) {
		try {
			URI uri = getDiagram().eResource().getURI();
			uri = uri.trimFragment();
			uri = uri.trimFileExtension();
			uri = uri.appendFileExtension("bpmn20.xml");
			BpmnXMLExport.createBpmnFile(uri, getDiagram().eResource().getContents());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
