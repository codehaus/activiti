package org.activiti.designer.eclipse.editor;

import org.activiti.designer.eclipse.deployment.ActivitiDeploymentForm;


import org.activiti.designer.eclipse.deployment.DeploymentInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;


public class ActivitiDeploymentEditorPage extends EditorPart {
	
	public static final String DEPLOYMENT_EDITOR_ID = "org.activiti.designer.eclipse.editor.ActivitiDeploymentEditorPage";
	ActivitiEditor editor;
	ActivitiDeploymentForm deploymentForm;
	static IFile file;
	
	public ActivitiDeploymentEditorPage() {
		editor = new ActivitiEditor();
	}
	
	public ActivitiDeploymentEditorPage(ActivitiEditor editor) {
		this.editor = editor;
	}
		
	public void setFile(IFile crntFile){
		file = crntFile;
	}
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Deployment");
		setPartLayout(form);
		createForm(toolkit, form.getBody());
	}
	
	private void setPartLayout(ScrolledForm form) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
	}

	private void createForm(FormToolkit toolkit, Composite form) {
		IFolder folder = getProcessFolder();
		if(folder!=null){
			deploymentForm = new ActivitiDeploymentForm(toolkit, form, folder, editor);
			deploymentForm.create();
		}
	}
	
	private IFolder getProcessFolder() {
		if(file==null){
			file = editor.getFile();
		}
		if (file!= null && file.getParent() instanceof IFolder) {
			return (IFolder)file.getParent();
		}
		return null;
	}
	
	public void setFocus() {	
	}
	
	public void doSave(IProgressMonitor monitor) {
	}
	
	public DeploymentInfo getDeploymentInfo() {
		return deploymentForm.getDeploymentInfo();
	}
	
	public void setDeploymentInfo(DeploymentInfo deploymentInfo) {
		deploymentForm.setDeploymentInfo(deploymentInfo);
	}
	
	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}
	
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		editor.init(site, input);
	}

}
