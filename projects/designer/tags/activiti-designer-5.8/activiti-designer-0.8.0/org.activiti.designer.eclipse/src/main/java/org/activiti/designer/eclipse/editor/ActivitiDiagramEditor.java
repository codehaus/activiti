package org.activiti.designer.eclipse.editor;

import java.util.Collection;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.export.SequenceFlowSynchronizer;
import org.activiti.designer.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.eclipse.ui.ExportMarshallerRunnable;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class ActivitiDiagramEditor extends DiagramEditor {

	public final static String ID = "org.activiti.designer.diagmrameditor"; //$NON-NLS-1$
	private static GraphicalViewer activeGraphicalViewer;

	public ActivitiDiagramEditor() {
		super();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class type) {
		return super.getAdapter(type);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		// Get a reference to the editor part

		final IEditorPart editorPart;
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor() instanceof ActivitiMultiPageEditor) {
			editorPart = ((ActivitiMultiPageEditor) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor()).getActivitiDiagramEditor();
		} else {
			editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor();
		}
		final DiagramEditorInput editorInput;

		editorInput = (DiagramEditorInput) editorPart.getEditorInput();
		SequenceFlowSynchronizer.synchronize(editorInput.getDiagram()
				.getConnections(), (DiagramEditor) editorPart);

		activeGraphicalViewer = (GraphicalViewer) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getAdapter(GraphicalViewer.class);

		// Regular save
		try {
		  super.doSave(monitor);
		} catch(Throwable e) {
		  e.printStackTrace();
		}

		// Determine list of ExportMarshallers to invoke after regular save
		final Collection<ExportMarshaller> marshallers = ExtensionPointUtil
				.getActiveExportMarshallers();

		if (marshallers.size() > 0) {
			// Get the resource belonging to the editor part
			final Diagram diagram = editorInput.getDiagram();

			// Get the progress service so we can have a progress monitor
			final IProgressService progressService = PlatformUI.getWorkbench()
					.getProgressService();

			try {
				final ExportMarshallerRunnable runnable = new ExportMarshallerRunnable(
						diagram, marshallers);
				progressService.busyCursorWhile(runnable);
			} catch (Exception e) {
				Logger.logError("Exception while performing save", e);
			}
		}
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new ActivitiEditorContextMenuProvider(getGraphicalViewer(),
				getActionRegistry(), getConfigurationProvider());
	}

	public static GraphicalViewer getActiveGraphicalViewer() {
		return activeGraphicalViewer;
	}
}
