package org.activiti.graphiti.bpmn.designer.util;

import org.activiti.graphiti.bpmn.designer.Activator;
import org.activiti.graphiti.bpmn.designer.preferences.ActivitiDesignerPreferences;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;

public class ActivitiUiUtil {

	public static void runModelChange(final Runnable runnable, final TransactionalEditingDomain editingDomain,
			final String label) {

		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain, label) {
			protected void doExecute() {
				runnable.run();
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public static boolean contextPertainsToBusinessObject(final ICustomContext context, final Class businessClass) {
		boolean result = false;
		EList<EObject> businessObjects = context.getInnerPictogramElement().getLink().getBusinessObjects();
		for (final EObject eobj : businessObjects) {
			if (businessClass.equals(eobj.getClass())) {
				result = true;
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Object getBusinessObjectFromContext(final ICustomContext context, final Class businessClass) {
		Object result = null;
		EList<EObject> businessObjects = context.getInnerPictogramElement().getLink().getBusinessObjects();
		for (final EObject eobj : businessObjects) {
			if (businessClass.equals(eobj.getClass())) {
				result = eobj;
				break;
			}
		}
		return result;
	}

	public static Ellipse createInvisibleEllipse(GraphicsAlgorithmContainer gaContainer, IGaService gaService) {
		Ellipse ret = AlgorithmsFactory.eINSTANCE.createEllipse();
		ret.setX(0);
		ret.setY(0);
		ret.setWidth(0);
		ret.setHeight(0);
		ret.setFilled(false);
		ret.setLineVisible(false);
		if (gaContainer instanceof PictogramElement) {
			PictogramElement pe = (PictogramElement) gaContainer;
			pe.setGraphicsAlgorithm(ret);
		} else if (gaContainer instanceof GraphicsAlgorithm) {
			GraphicsAlgorithm parentGa = (GraphicsAlgorithm) gaContainer;
			parentGa.getGraphicsAlgorithmChildren().add(ret);
		}
		return ret;
	}

	public static org.eclipse.bpmn2.Process getProcessObject(Diagram diagram) {
		for (EObject eObject : diagram.eResource().getContents()) {
			if (eObject instanceof org.eclipse.bpmn2.Process) {
				return (org.eclipse.bpmn2.Process) eObject;
			}
		}
		return null;
	}

	public static void doProjectReferenceChange(IProject currentProject, IJavaProject containerProject, String className)
			throws CoreException {

		if (currentProject.equals(containerProject.getProject())) {
			System.out.println("Service class is within current project");
			return;
		}

		IProjectDescription desc = currentProject.getDescription();
		IProject[] refs = desc.getReferencedProjects();
		IProject[] newRefs = new IProject[refs.length + 1];
		System.arraycopy(refs, 0, newRefs, 0, refs.length);
		newRefs[refs.length] = containerProject.getProject();
		desc.setReferencedProjects(newRefs);
		currentProject.setDescription(desc, new NullProgressMonitor());

		IPath dependsOnPath = containerProject.getProject().getFullPath();

		IJavaProject javaProject = (IJavaProject) currentProject.getNature(JavaCore.NATURE_ID);
		IClasspathEntry prjEntry = JavaCore.newProjectEntry(dependsOnPath, true);

		boolean dependsOnPresent = false;
		for (IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
			if (cpEntry.equals(prjEntry)) {
				dependsOnPresent = true;
			}
		}

		if (!dependsOnPresent) {
			IClasspathEntry[] entryList = new IClasspathEntry[1];
			entryList[0] = prjEntry;
			IClasspathEntry[] newEntries = (IClasspathEntry[]) ArrayUtils.addAll(javaProject.getRawClasspath(),
					entryList);
			javaProject.setRawClasspath(newEntries, null);
		}

	}

	public static IProject getProjectFromDiagram(Diagram diagram) {

		IProject currentProject = null;
		Resource resource = diagram.eResource();

		URI uri = resource.getURI();
		URI uriTrimmed = uri.trimFragment();

		if (uriTrimmed.isPlatformResource()) {

			String platformString = uriTrimmed.toPlatformString(true);
			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

			if (fileResource != null) {
				currentProject = fileResource.getProject();
			}
		}
		return currentProject;
	}

	/**
	 * Gets a boolean preference's value from the plugin's preference store.
	 * 
	 * @param preference
	 *            the preference to get
	 * @return true if the preference is stored as true, otherwise false and
	 *         false if there is no preference applied
	 */
	public static final boolean getBooleanPreference(final ActivitiDesignerPreferences preference) {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(preference.getPreferenceId());
	}
}
