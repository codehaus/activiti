package org.activiti.designer.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.export.SequenceFlowSynchronizer;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class ActivitiDiagramEditor extends DiagramEditor {

	public final static String ID = "org.activiti.designer.bpmneditor"; //$NON-NLS-1$
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
		final IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		final DiagramEditorInput editorInput = (DiagramEditorInput) editorPart
				.getEditorInput();

		activeGraphicalViewer = (GraphicalViewer) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getAdapter(GraphicalViewer.class);

		SequenceFlowSynchronizer.synchronize(editorInput.getDiagram()
				.getConnections(), (DiagramEditor) editorPart);

		// Determine list of ExportMarshallers to invoke after regular save
		final List<ExportMarshaller> marshallers = getActiveMarshallers();

		if (marshallers.size() > 0) {

			// Get the resource belonging to the editor part
			final Diagram diagram = editorInput.getDiagram();

			// Get the progress service so we can have a progress monitor
			final IProgressService progressService = PlatformUI.getWorkbench()
					.getProgressService();

			try {
				final SaveRunnableWithProgress runnable = new SaveRunnableWithProgress(
						diagram, marshallers);
				progressService.busyCursorWhile(runnable);
			} catch (Exception e) {
				Logger.logError("Exception while performing save", e);
			}

		}

		super.doSave(monitor);
	}

	private void invokeExportMarshaller(
			final ExportMarshaller exportMarshaller, final Diagram diagram,
			final IProgressMonitor monitor) {

		ISafeRunnable runnable = new ISafeRunnable() {

			@Override
			public void handleException(Throwable exception) {
				System.out
						.println("An exception occurred while running ExportMarshaller "
								+ exportMarshaller.getMarshallerName()
								+ ": "
								+ exception.getMessage());
			}

			@Override
			public void run() throws Exception {
				exportMarshaller.marshallDiagram(diagram, monitor);
			}
		};
		SafeRunner.run(runnable);
	}

	private class SaveRunnableWithProgress implements IRunnableWithProgress {

		/**
		 * The number of work units allocated to a marshaller.
		 */
		private static final int WORK_UNITS_PER_MARSHALLER = 100;

		private Diagram diagram;
		private List<ExportMarshaller> marshallers;

		protected SaveRunnableWithProgress(final Diagram diagram,
				final List<ExportMarshaller> marshallers) {
			this.diagram = diagram;
			this.marshallers = marshallers;
		}

		public void run(IProgressMonitor monitor) {

			try {
				monitor.beginTask("Saving to additional export formats",
						marshallers.size() * WORK_UNITS_PER_MARSHALLER + 25);

				if (marshallers.size() > 0) {

					monitor.worked(25);

					for (final ExportMarshaller marshaller : marshallers) {
						final IProgressMonitor subMonitor = new SubProgressMonitor(
								monitor, WORK_UNITS_PER_MARSHALLER);
						try {
							monitor.subTask(String.format(
									"Saving diagram to %s format",
									marshaller.getFormatName()));
							invokeExportMarshaller(marshaller, diagram,
									subMonitor);
						} finally {
							// enforce calling of done() if the client hasn't
							// done so itself
							subMonitor.done();
						}
					}
				}
			} finally {
				monitor.done();
			}
		}
	}

	/**
	 * Gets a list of the currently active marshallers.
	 * 
	 * @return a list of marshallers or an empty list
	 */
	private List<ExportMarshaller> getActiveMarshallers() {

		final List<ExportMarshaller> result = new ArrayList<ExportMarshaller>();

		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						ActivitiPlugin.EXPORT_MARSHALLER_EXTENSIONPOINT_ID);

		for (IConfigurationElement e : config) {
			Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o instanceof ExportMarshaller) {

					final ExportMarshaller exportMarshaller = (ExportMarshaller) o;

					final boolean invokeMarshaller = PreferencesUtil
							.getBooleanPreference(PreferencesUtil
									.getPreferenceId(exportMarshaller));

					if (invokeMarshaller
							&& exportMarshaller.getFilenamePattern() != null) {
						result.add(exportMarshaller);
					}
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		return result;
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
