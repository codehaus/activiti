package org.activiti.designer.eclipse.ui.wizard.diagram;

import java.lang.reflect.InvocationTargetException;

import org.activiti.designer.eclipse.editor.Bpmn2DiagramCreator;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;


/**
 * The Class CreateDefaultActivitiDiagramWizard.
 */
public class CreateDefaultActivitiDiagramWizard extends BasicNewResourceWizard {

  private Diagram diagram;
  private CreateDefaultActivitiDiagramNameWizardPage namePage;
  private CreateDefaultActivitiDiagramInitialContentPage initialContentPage;

  @Override
  public void addPages() {
    super.addPages();
    namePage = new CreateDefaultActivitiDiagramNameWizardPage(super.getSelection());
    addPage(namePage);
    initialContentPage = new CreateDefaultActivitiDiagramInitialContentPage();
    addPage(initialContentPage);
  }

  @Override
  public boolean canFinish() {
    return canCreateDiagramFile();
  }

  private boolean canCreateDiagramFile() {
    final IFile fileToCreate = getDiagramFile();
    if (fileToCreate != null) {
      return !fileToCreate.exists();
    }
    return false;
  }

  private IFile getDiagramFile() {

    final String diagramName = getDiagramName();
    if (StringUtils.isBlank(diagramName)) {
      return null;
    }

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    if(namePage.getContainerFullPath().segmentCount() == 1) {
    	return root.getProject(namePage.getContainerFullPath().lastSegment()).getFile(diagramName);
    } else {
	    IFolder diagramFolder = root.getFolder(namePage.getContainerFullPath());
	    return diagramFolder.getFile(diagramName);
    }
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

  @Override
  public IWizardPage getNextPage(IWizardPage page) {
    if (page instanceof CreateDefaultActivitiDiagramNameWizardPage) {

    }
    return super.getNextPage(page);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {

    final IFile diagramFile = getDiagramFile();
    
    String tempFileName = null;
		if(initialContentPage.contentSourceTemplate.getSelection() == true &&
        initialContentPage.templateTable.getSelectionIndex() >= 0) {
			
			tempFileName = this.getClass().getClassLoader().getResource("src/main/resources/templates/" + 
          TemplateInfo.templateFilenames[initialContentPage.templateTable.getSelectionIndex()]).getPath();
		}
		
		final String contentFileName = tempFileName;
    
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					IPath path = diagramFile.getFullPath();
					Bpmn2DiagramCreator factory = new Bpmn2DiagramCreator();
					IFolder folder = Bpmn2DiagramCreator.getTempFolder(path);
					factory.setDiagramFile(Bpmn2DiagramCreator.getTempFile(path, folder));
					factory.setDiagramFolder(folder);
					factory.createDiagram(true, contentFileName);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
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

  private CreateDefaultActivitiDiagramNameWizardPage getNamePage() {
    return (CreateDefaultActivitiDiagramNameWizardPage) getPage(CreateDefaultActivitiDiagramNameWizardPage.PAGE_NAME);
  }

  private String getDiagramName() {
    return getNamePage().getDiagramName();
  }

}
