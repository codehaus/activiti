/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Handles file saving for diagrams and forking to {@link ExportMarshaller}s
 * during save actions.
 * <p>
 * Also updates sequenceflow objects that are out of sync in the businessmodel.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
public class SaveHandler extends AbstractHandler implements IHandler {

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd-HH-mm-ss";

	private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_TIME_PATTERN);

	private static final String REGEX_DATE_TIME = "\\" + ExportMarshaller.PLACEHOLDER_DATE_TIME + "";
	private static final String REGEX_FILENAME = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME + "";
	private static final String REGEX_EXTENSION = "\\" + ExportMarshaller.PLACEHOLDER_ORIGINAL_FILE_EXTENSION + "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get a reference to the editor part
		final IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();

		final DiagramEditorInput editorInput = (DiagramEditorInput) editorPart.getEditorInput();

		SequenceFlowSynchronizer.synchronize(editorInput.getDiagram().getConnections(), (DiagramEditor) editorPart);

		// Perform the regular save of the diagram without progress monitoring
		editorPart.doSave(null);
		
		// TODO temporary to save bpmn20.xml
		
		URI uri = editorInput.getDiagram().eResource().getURI();
		URI bpmnUri = uri.trimFragment();
		bpmnUri = bpmnUri.trimFileExtension();
		bpmnUri = bpmnUri.appendFileExtension("bpmn20.xml");
		
		IProject project = null;
		String parentDiagramName = null;
		if (bpmnUri.isPlatformResource()) {
			String platformString = bpmnUri.toPlatformString(true);
			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
			if(fileResource != null) {
				project = fileResource.getProject();
				parentDiagramName = uri.trimFragment().trimFileExtension().lastSegment();
			}
		}
		BpmnXMLExport.createBpmnFile(bpmnUri, editorInput.getDiagram(), project, parentDiagramName);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(bpmnUri.toPlatformString(false));
		IFile file = workspace.getRoot().getFile(location);
		try {
			file.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// Determine list of ExportMarshallers to invoke after regular save
		final List<ExportMarshaller> marshallers = getActiveMarshallers();

		if (marshallers.size() > 0) {

			// Get the resource belonging to the editor part
			final Resource resource = editorInput.getDiagram().eResource();

			// Get the progress service so we can have a progress monitor
			final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();

			try {
				final SaveRunnableWithProgress runnable = new SaveRunnableWithProgress(resource, marshallers);
				progressService.busyCursorWhile(runnable);
			} catch (InvocationTargetException e) {
				throw new ExecutionException("Exception while invoking ExportMarshaller ", e);
			} catch (InterruptedException e) {
				throw new ExecutionException("Exception while invoking ExportMarshaller ", e);
			}

		}

		return null;
	}

	private void invokeExportMarshaller(final ExportMarshaller exportMarshaller,
			final Resource originalDiagramResource, final InputStream originalDiagramStream,
			final IProgressMonitor monitor) {

		ISafeRunnable runnable = new ISafeRunnable() {

			@Override
			public void handleException(Throwable exception) {
				System.out.println("An exception occurred while running ExportMarshaller "
						+ exportMarshaller.getMarshallerName() + ": " + exception.getMessage());
			}

			@Override
			public void run() throws Exception {

				final String filenamePattern = exportMarshaller.getFilenamePattern();

				if (filenamePattern != null) {

					final URI baseURI = originalDiagramResource.getURI().trimSegments(1);

					byte[] newBytes = exportMarshaller.marshallDiagram(originalDiagramStream, monitor);
					final ByteArrayInputStream bais = new ByteArrayInputStream(newBytes);

					// Parse any replacement variables in the filename
					final Calendar now = Calendar.getInstance();

					String newFilename = filenamePattern.replaceAll(REGEX_DATE_TIME, SDF.format(now.getTime()));
					newFilename = newFilename.replaceAll(REGEX_EXTENSION, originalDiagramResource.getURI()
							.fileExtension());
					newFilename = newFilename.replaceAll(
							REGEX_FILENAME,
							originalDiagramResource.getURI().segment(
									originalDiagramResource.getURI().segments().length - 1));

					// Determine the URI to the new file name
					final URI newURI = baseURI.appendSegment(newFilename);
					final IWorkspace workspace = ResourcesPlugin.getWorkspace();

					final IFile file = workspace.getRoot().getFile(new Path(newURI.toPlatformString(true)));
					if (file.exists()) {
						// delete first
						monitor.beginTask("test", 10);
						file.delete(IResource.FORCE, monitor);
					}
					monitor.beginTask("test2", 10);
					file.create(bais, IResource.FORCE, monitor);
				}
			}
		};
		SafeRunner.run(runnable);
	}

	private class SaveRunnableWithProgress implements IRunnableWithProgress {

		/**
		 * The number of work units allocated to a marshaller.
		 */
		private static final int WORK_UNITS_PER_MARSHALLER = 100;

		private Resource resource;
		private List<ExportMarshaller> marshallers;

		protected SaveRunnableWithProgress(final Resource resource, final List<ExportMarshaller> marshallers) {
			this.resource = resource;
			this.marshallers = marshallers;
		}

		public void run(IProgressMonitor monitor) {

			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(new Path(resource.getURI().toPlatformString(true)));

			InputStream originalDiagramStream = null;

			try {
				originalDiagramStream = file.getContents();

				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//
				// byte[] b = new byte[IO_BUFFER_SIZE];
				// int read;
				// while ((read = stream.read(b)) != -1) {
				// baos.write(b, 0, read);
				// }
				//
				// originalBytes = baos.toByteArray();

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (originalDiagramStream != null) {
				try {
					monitor.beginTask("Saving to additional export formats", marshallers.size()
							* WORK_UNITS_PER_MARSHALLER + 25);

					if (marshallers.size() > 0) {

						monitor.worked(25);

						for (final ExportMarshaller marshaller : marshallers) {
							final IProgressMonitor subMonitor = new SubProgressMonitor(monitor,
									WORK_UNITS_PER_MARSHALLER);
							try {
								monitor.subTask(String.format("Saving diagram to %s format", marshaller.getFormatName()));
								invokeExportMarshaller(marshaller, resource, originalDiagramStream, subMonitor);
							} finally {
								// enforce calling of done() if the client
								// hasn't
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
	}

	/**
	 * Gets a list of the currently active marshallers.
	 * 
	 * @return a list of marshallers or an empty list
	 */
	private List<ExportMarshaller> getActiveMarshallers() {

		final List<ExportMarshaller> result = new ArrayList<ExportMarshaller>();

		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				ActivitiPlugin.EXPORT_MARSHALLER_EXTENSIONPOINT_ID);

		for (IConfigurationElement e : config) {
			Object o;
			try {
				o = e.createExecutableExtension("class");
				if (o instanceof ExportMarshaller) {

					final ExportMarshaller exportMarshaller = (ExportMarshaller) o;

					final boolean invokeMarshaller = PreferencesUtil.getBooleanPreference(PreferencesUtil
							.getPreferenceId(exportMarshaller));

					if (invokeMarshaller && exportMarshaller.getFilenamePattern() != null) {
						result.add(exportMarshaller);
					}
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

}
