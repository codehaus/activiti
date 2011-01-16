package org.activiti.designer.popupmenus;

import java.io.File;
import java.io.IOException;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.OSEnum;
import org.activiti.designer.util.OSUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class ImportBpmnMenu implements org.eclipse.ui.IObjectActionDelegate{

	ISelection fSelection;

	@Override
	public void run(IAction action) {
		Object selection = ( (IStructuredSelection) fSelection).getFirstElement();
		IJavaProject javaProject = (IJavaProject) selection;
		IFolder diagramFolder = null;
		try {
		  diagramFolder = javaProject.getProject().getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER);
		  if(diagramFolder == null) {
		    return;
		  }
		} catch(Throwable e) {
		  return;
		}
		
		FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fd.setText("Choose BPMN 2.0 XML file to import");
    if(OSUtil.getOperatingSystem() == OSEnum.Windows) {
      fd.setFilterPath("C:/");
    } else {
      fd.setFilterPath("/");
    }
    String[] filterExt = { "*.xml"};
    fd.setFilterExtensions(filterExt);
    String bpmnFile = fd.open();
    
    System.out.println("bpmnFile " + bpmnFile);
    String processName = bpmnFile.substring(bpmnFile.lastIndexOf(File.separator) + 1);
    processName = processName.replace(".xml", "");
    processName = processName.replace(".bpmn20", "");
    System.out.println("processName " + processName);

    // Get the default resource set to hold the new resource
    ResourceSet resourceSet = new ResourceSetImpl();
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
    if (editingDomain == null) {
      // Not yet existing, create one
      editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
    }

    // Create the data within a command and save (must not happen inside
    // the command since finishing the command will trigger setting the 
    // modification flag on the resource which will be used by the save
    // operation to determine which resources need to be saved)
    ImportBpmnElementsCommand operation = new ImportBpmnElementsCommand(javaProject.getProject(), 
            editingDomain, processName, bpmnFile);
    editingDomain.getCommandStack().execute(operation);
    try {
      operation.getCreatedResource().save(null);
    } catch (IOException e) {
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), e.getMessage(), e); //$NON-NLS-1$
      ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error Occured", e.getMessage(), status);
    }

    // Dispose the editing domain to eliminate memory leak
    editingDomain.dispose();

    // Open the editor
    String platformString = operation.getCreatedResource().getURI().toPlatformString(true);
    IFile file = javaProject.getProject().getParent().getFile(new Path(platformString));
    IFileEditorInput input = new FileEditorInput(file);
    
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ActivitiBPMNDiagramConstants.DIAGRAM_EDITOR_ID);
    } catch (PartInitException e) {
      String error = "Error while opening diagram editor";
      IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error, e);
      ErrorDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", null, status);
      return;
    }
  }

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

  @Override
  public void setActivePart(IAction action, IWorkbenchPart part) {
  }
}

