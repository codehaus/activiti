package org.activiti.designer.eclipse.editor;

import org.activiti.designer.eclipse.deployment.DeploymentInfo;
import org.eclipse.ui.IEditorInput;

public class ActivitiContentProvider {
	
	ActivitiEditor activitiEditor;
	
	public ActivitiContentProvider(ActivitiEditor activitiEditor) {
		this.activitiEditor = activitiEditor;
	}
	
	protected void addDeploymentInfo(DeploymentInfo deploymentInfo, IEditorInput editorInput) {
		
	}
	
}
