package org.activiti.designer.eclipse.popupmenu;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.editor.ActivitiDeploymentEditorPage;
import org.activiti.designer.eclipse.editor.ActivitiEditor;
import org.activiti.designer.eclipse.editor.StringEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.EditorManager;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.part.FileEditorInput;

public class DeploymentMenu implements org.eclipse.ui.IObjectActionDelegate{

	IAction fAction;
	private IWorkbenchPart fView;
	ISelection fSelection;

	@Override
	public void run(IAction action) {
		Object selection = ( (IStructuredSelection) fSelection).getFirstElement();
		if(selection instanceof IFile) {
			ActivitiEditor activitiEditor = new ActivitiEditor();
			ActivitiDeploymentEditorPage editor = new ActivitiDeploymentEditorPage(activitiEditor);
			
			WorkbenchWindow window = (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			WorkbenchPage page = (WorkbenchPage) window.getActivePage();
			
			String diagramName ="my_bpmn2_diagram";
			
			EditorDescriptor desc =  EditorDescriptor.createForProgram(diagramName + ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION);
			EditorAreaHelper pres = new EditorAreaHelper(page);
			EditorManager manager = new EditorManager(window, page, pres);
			IFile diagramFile = (IFile) selection;
			
			FileEditorInput fileInput = new FileEditorInput(diagramFile); 
			EditorReference ref = new EditorReference(manager, fileInput, desc);
			EditorSite site = new EditorSite(ref, editor, page, desc);
			try {
				editor.setFile(diagramFile);
				activitiEditor.setFile(diagramFile);
				
				editor.init(site, new StringEditorInput("Deployment"));
				editor.createPartControl(new Shell());
				page.openEditor(new StringEditorInput("Deployment"), editor.DEPLOYMENT_EDITOR_ID);

			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fAction = action;
		fSelection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fAction = action;
		fView = targetPart;
	}
}

