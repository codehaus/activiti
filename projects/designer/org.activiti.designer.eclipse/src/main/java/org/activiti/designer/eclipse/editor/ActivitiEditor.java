package org.activiti.designer.eclipse.editor;

import org.activiti.designer.common.editor.ContentProvider;
import org.activiti.designer.common.editor.Editor;
import org.activiti.designer.common.model.SemanticElement;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.deployment.DeploymentInfo;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class ActivitiEditor extends Editor { 

	public static final String DEPLOYMENT_EDITOR_ID = "org.activiti.designer.eclipse.editor.ActivitiEditor";
	private IResourceChangeListener resourceChangeListener;
	private ActivitiDeploymentEditorPage deploymentPage;
	private static IFile file = null;
	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		initResourceChangeListener();
		initPartName();
	}
	
	public void setFile(IFile crntFile){
		file = crntFile;
	}
	
	public IFile getFile(){
		return file;
	}
	
	private void initResourceChangeListener() {
		resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				handleResourceChange(event);
			}
		};
		getWorkspace().addResourceChangeListener(resourceChangeListener);
	}
	

	private void handleResourceChange(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			if (file!=null && !file.exists()) {
				deleteProcessFolder(file);
			}
		} 
	}
	
	private void deleteProcessFolder(IFile file) {
		final IContainer processFolder = getWorkspace().getRoot().getFolder(file.getFullPath().removeLastSegments(1));
		if (processFolder != null && processFolder.exists()) {
			WorkspaceJob job = new WorkspaceJob("delete") {
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					processFolder.delete(true, null);
					return Status.OK_STATUS;
				}
			};
			job.setRule(getWorkspace().getRuleFactory().deleteRule(processFolder));
			job.schedule();
		}
	}
	
	private IWorkspace getWorkspace() {
		return ActivitiPlugin.getWorkspace();
	}
	
	private void initPartName() {
		setPartName(((StringEditorInput)getEditorInput()).getName());
	}

	protected ContentProvider createContentProvider() {
		return null;
	}

	protected void createPages() {
		super.createPages();
		initDeploymentPage();
	}
	
	protected void initDeploymentPage() {
		deploymentPage = new ActivitiDeploymentEditorPage(this);
		deploymentPage.setFile(file);
		addPage(0, deploymentPage, "Deployment");	
		DeploymentInfo deploymentInfo = getDeploymentInfo();
		deploymentPage.setDeploymentInfo(deploymentInfo);
	}

	protected SemanticElement createMainElement() {
		return getSemanticElementFactory().createById("org.activiti.designer.eclipse.processDefinition");
	}
	
	public DeploymentInfo getDeploymentInfo() {
		return deploymentPage.getDeploymentInfo();
	}

	public void dispose() {
		getWorkspace().removeResourceChangeListener(resourceChangeListener);
		super.dispose();
	}

}
