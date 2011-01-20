package org.activiti.designer.eclipse.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author Yvo Swillens
 * @since 0.6.0
 * @version 1
 * 
 */
public class ActivitiMultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	private static final String DIAGRAM_PANE_TILE = "Diagram";
	private static final String XML_PANE_TITLE = "BPMN2.0";

	/** The diagram editor used in page 0. */
	private ActivitiDiagramEditor diagramEditor;

	/** The XML editor used in page 1. */
	private StructuredTextEditor bpmnEditor;

	/**
	 * Creates a multi-page editor example.
	 */
	public ActivitiMultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	private void createDiagramPage() {

		try {
			diagramEditor = new ActivitiDiagramEditor();
			int index = addPage(diagramEditor, getEditorInput());
			setPageText(index, ActivitiMultiPageEditor.DIAGRAM_PANE_TILE);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested Activiti Diagram editor", null,
					e.getStatus());
		}
	}

	private void createBPMN2Page() {
		try {
			bpmnEditor = new StructuredTextEditor();
			// bpmnEditor.setEditorPart(this);

			int index = addPage(bpmnEditor, new FileEditorInput(
					getAssociatedBPMN2IFile()));

			setPageText(index, ActivitiMultiPageEditor.XML_PANE_TITLE);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested Activiti BPMN2.0 editor", null,
					e.getStatus());
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createDiagramPage();
		createBPMN2Page();
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor monitor) {
	  int activePage = getActivePage();
	  if(activePage == 0) {
	    getEditor(0).doSave(monitor);
	  } else if(activePage == 1) {
	    getEditor(1).doSave(monitor);
	  }
	}

	public void doSaveAs() {
	  int activePage = getActivePage();
    if(activePage == 0) {
  		IEditorPart editor = getEditor(0);
  		editor.doSaveAs();
  		setPageText(0, editor.getTitle());
  		setInput(editor.getEditorInput());
    } else if(activePage == 1) {
      IEditorPart editor = getEditor(1);
      editor.doSaveAs();
      setPageText(1, editor.getTitle());
      setInput(editor.getEditorInput());
    }
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput) && !(editorInput instanceof DiagramEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public ActivitiDiagramEditor getActivitiDiagramEditor() {
		IEditorPart editor = getEditor(0);
		return (ActivitiDiagramEditor) editor;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) bpmnEditor.getEditorInput())
								.getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(bpmnEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	/**
	 * Reloads contents of BPMN2 pane when selected
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 1) {
			bpmnEditor.setInput(new FileEditorInput(getAssociatedBPMN2IFile()));
		}
	}

	private IFile getAssociatedBPMN2IFile() {

		URI uri = getAssociatedBPMN2URI();

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IFile bpmn20File = workspace.getRoot().getFile(
				new Path(uri.toPlatformString(true)));

		return bpmn20File;
	}

	private URI getAssociatedBPMN2URI() {

		final Diagram diagram = ((DiagramEditorInput) diagramEditor
				.getEditorInput()).getDiagram();
		final URI originalURI = diagram.eResource().getURI();
		final URI parentURI = originalURI.trimSegments(1);
		final String REGEX_FILENAME = "\\" + "$originalFile" + "";

		String finalSegment = "$originalFile" + ".bpmn20.xml";
		finalSegment = finalSegment.replaceAll(REGEX_FILENAME,
				originalURI.lastSegment());

		return parentURI.appendSegment(finalSegment);
	}
}
