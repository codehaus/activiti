package org.activiti.graphiti.bpmn.eclipse.ui;

import org.activiti.graphiti.bpmn.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.graphiti.bpmn.eclipse.common.ActivitiPlugin;
import org.activiti.graphiti.bpmn.eclipse.common.FileService;
import org.activiti.graphiti.bpmn.eclipse.navigator.nodes.base.AbstractInstancesOfTypeContainerNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * The Class CreateDiagramWizard.
 */
public class CreateDiagramWizard extends BasicNewResourceWizard {

	private static final String PAGE_NAME_DIAGRAM_NAME = "Diagram Name";

	private Diagram diagram;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(new DiagramNameWizardPage(PAGE_NAME_DIAGRAM_NAME));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse
	 * .ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String diagramTypeId = "BPMNdiagram";

		ITextProvider namePage = (ITextProvider) getPage(PAGE_NAME_DIAGRAM_NAME);
		final String diagramName = namePage.getText();

		IProject project = null;
		IFolder diagramFolder = null;

		// Added check on IJavaProject
		// Kept IProject check for future facet implementation
		Object element = getSelection().getFirstElement();
		if (element instanceof IProject) {
			project = (IProject) element;
		} else if (element instanceof IJavaProject) {
			IJavaProject javaProject = (IJavaProject) element;
			project = javaProject.getProject();
		} else if (element instanceof AbstractInstancesOfTypeContainerNode) {
			AbstractInstancesOfTypeContainerNode aiocn = (AbstractInstancesOfTypeContainerNode) element;
			project = aiocn.getProject();
		} else if (element instanceof IFolder) {
			diagramFolder = (IFolder) element;
			project = diagramFolder.getProject();
		}

		if (project == null || !project.isAccessible()) {
			String error = "No open project was found for the current selection. Select a project and restart the wizard.";
			IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error);
			ErrorDialog.openError(getShell(), "No Project Found", null, status);
			return false;
		}

		Diagram diagram = Graphiti.getPeCreateService().createDiagram(diagramTypeId, diagramName, true);
		if (diagramFolder == null) {
			diagramFolder = project.getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER);
		}

		IFile diagramFile = diagramFolder.getFile(diagramName + ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION);
		URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);

		TransactionalEditingDomain domain = FileService.createEmfFileForDiagram(uri, diagram);
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		DiagramEditorInput editorInput = new DiagramEditorInput(EcoreUtil.getURI(diagram), domain, providerId);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(editorInput, DiagramEditor.DIAGRAM_EDITOR_ID);
		} catch (PartInitException e) {
			String error = "Error while opening diagram editor";
			IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error, e);
			ErrorDialog.openError(getShell(), "An error occured", null, status);
			return false;
		}

		return true;
	}

	/**
	 * Gets the diagram.
	 * 
	 * @return the diagram
	 */
	public Diagram getDiagram() {
		return diagram;
	}
}
