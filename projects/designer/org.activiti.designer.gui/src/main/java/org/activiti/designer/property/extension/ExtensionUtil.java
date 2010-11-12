/**
 * 
 */
package org.activiti.designer.property.extension;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.util.ActivitiUiUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarEntryDirectory;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Provides utilities for Extensions to the Designer.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public final class ExtensionUtil {

	private static final String CUSTOM_IMPLEMENTATION_PREFIX = "custom";
	private static final String CUSTOM_IMPLEMENTATION_SEPARATOR = ":";
	private static final String CUSTOM_IMPLEMENTATION_PREFIX_QUALIFIED = CUSTOM_IMPLEMENTATION_PREFIX
			+ CUSTOM_IMPLEMENTATION_SEPARATOR;

	private ExtensionUtil() {

	}

	public static final String wrapCustomId(CustomServiceTask task) {
		return CUSTOM_IMPLEMENTATION_PREFIX_QUALIFIED + task.getId();
	}

	public static final String unwrapCustomId(final String customId) {
		return StringUtils.substringAfter(customId, CUSTOM_IMPLEMENTATION_SEPARATOR);
	}

	/**
	 * Determines whether the provided business object is a custom service task.
	 * 
	 * @param bo
	 *            the business object
	 * @return true if the business object is a custom service task, false
	 *         otherwise
	 */
	public static final boolean isCustomServiceTask(final EObject bo) {
		boolean result = false;
		if (bo instanceof ServiceTask) {
			final ServiceTask serviceTask = (ServiceTask) bo;
			if (serviceTask.getImplementation() != null
					&& serviceTask.getImplementation().startsWith(CUSTOM_IMPLEMENTATION_PREFIX_QUALIFIED)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Gets a list of {@link CustomServiceTask} objects based on the
	 * {@link TabbedPropertySheetPage} provided.
	 * 
	 * @param tabbedPropertySheetPage
	 *            the property sheet page linked to a diagram in a project that
	 *            has {@link CustomServiceTask}s defined
	 * @return a list of all {@link CustomServiceTask}s or an empty list if none
	 *         were found
	 */
	public static List<CustomServiceTask> getCustomServiceTasks(final TabbedPropertySheetPage tabbedPropertySheetPage) {

		// Determine the part the property sheet page is in
		final IWorkbenchPart part = tabbedPropertySheetPage.getSite().getWorkbenchWindow().getPartService()
				.getActivePart();

		// If the part is a diagram editor, get the project from the diagram
		if (part instanceof DiagramEditor) {
			final DiagramEditor editor = (DiagramEditor) part;
			final IProject project = ActivitiUiUtil.getProjectFromDiagram(editor.getDiagramTypeProvider().getDiagram());

			// Determine the custom service tasks using the project found
			return getCustomServiceTasks(project);
		}

		return null;
	}

	/**
	 * Gets a list of {@link CustomServiceTask} objects based on the
	 * {@link IProject} provided.
	 * 
	 * @param project
	 *            the project that has {@link CustomServiceTask}s defined
	 * @return a list of all {@link CustomServiceTask}s or an empty list if none
	 *         were found
	 */
	public static List<CustomServiceTask> getCustomServiceTasks(final IProject project) {

		List<CustomServiceTask> result = new ArrayList<CustomServiceTask>();

		// Determine the project
		IJavaProject javaProject = null;
		try {
			javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			// skip, not a Java project
		}

		if (javaProject != null) {

			// get the contexts first
			final List<CustomServiceTaskContext> cstContexts = getCustomServiceTaskContexts(project);

			// extract custom service tasks from the contexts
			for (final CustomServiceTaskContext customServiceTaskContext : cstContexts) {
				result.add(customServiceTaskContext.getServiceTask());
			}
		}
		return result;
	}

	/**
	 * Gets a list of {@link CustomServiceTaskContext} objects based on the
	 * {@link IProject} provided.
	 * 
	 * @param project
	 *            the project that has {@link CustomServiceTask}s defined
	 * @return a list containing the context of each {@link CustomServiceTask}
	 *         object found or an empty list if {@link CustomServiceTask}s were
	 *         found were found
	 */
	public static List<CustomServiceTaskContext> getCustomServiceTaskContexts(final IProject project) {

		List<CustomServiceTaskContext> result = new ArrayList<CustomServiceTaskContext>();

		IJavaProject javaProject = null;
		try {
			javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			// skip, not a Java project
		}

		if (javaProject != null) {

			try {

				// Get the container for the designer extensions. This is the
				// predefined user library linking to the extension libraries
				final IClasspathContainer userLibraryContainer = JavaCore.getClasspathContainer(new Path(
						ActivitiPlugin.DESIGNER_EXTENSIONS_USER_LIB_PATH), javaProject);

				// Get a list of the classpath entries in the container. Each of
				// these represents one jar containing zero or more designer
				// extensions
				final IClasspathEntry[] extensionJars = userLibraryContainer.getClasspathEntries();

				// If there are jars, inspect them; otherwise return because
				// there are no extensions
				if (extensionJars.length > 0) {

					for (final IClasspathEntry classpathEntry : extensionJars) {

						// Only check entries of the correct kind
						if (classpathEntry.getEntryKind() == 1 && classpathEntry.getContentKind() == 2) {

							final IPackageFragmentRoot packageFragmentRoot = javaProject
									.getPackageFragmentRoot(classpathEntry.getPath().toString());

							// Determine the name of the extension
							String extensionName = null;
							// Extract the manifest and look for the
							// CustomServiceTask.MANIFEST_EXTENSION_NAME
							// property
							final Manifest manifest = extractManifest(packageFragmentRoot);
							if (manifest != null) {
								extensionName = manifest.getMainAttributes().getValue(
										CustomServiceTask.MANIFEST_EXTENSION_NAME);
							}
							// If there is no manifest or the property wasn't
							// defined, use the jar's name as extension name
							// instead
							if (extensionName == null) {
								extensionName = classpathEntry.getPath().lastSegment();
							}

							// Create a JarClassLoader to load any classes we
							// find for this extension

							final JarClassLoader cl = new JarClassLoader(packageFragmentRoot.getPath()
									.toPortableString());

							// Inspect the jar by scanning its classpath and
							// looking for classes that implement
							// CustomServiceTask
							final IJavaElement[] javaElements = packageFragmentRoot.getChildren();
							for (final IJavaElement javaElement : javaElements) {
								if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
									IPackageFragment fragment = (IPackageFragment) javaElement;
									if (fragment.containsJavaResources()) {
										final IClassFile[] classFiles = fragment.getClassFiles();
										for (final IClassFile classFile : classFiles) {
											if (classFile.isClass()) {

												final IType type = classFile.getType();

												try {
													Class<CustomServiceTask> clazz = (Class<CustomServiceTask>) cl
															.loadClass(type.getFullyQualifiedName());

													// Filter if the class is
													// abstract: this probably
													// means it is extended by
													// concrete classes in the
													// extension and will have
													// any properties applied in
													// that way; we can't
													// instantiate the class
													// anyway
													if (!Modifier.isAbstract(clazz.getModifiers())
															&& CustomServiceTask.class.isAssignableFrom(clazz)) {
														try {
															CustomServiceTask customServiceTask = (CustomServiceTask) clazz
																	.newInstance();
															// Add this
															// CustomServiceTask
															// to
															// the result,
															// wrapped
															// in its context
															result.add(new CustomServiceTaskContextImpl(
																	customServiceTask, extensionName, classpathEntry
																			.getPath().toPortableString()));
														} catch (InstantiationException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														} catch (IllegalAccessException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}

													}
												} catch (ClassNotFoundException e) {
													e.printStackTrace();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (JavaModelException e) {
				// TODO: test when this exception occurs: if there is no user
				// lib for example?
				e.printStackTrace();
			}

		}

		return result;
	}

	/**
	 * @param packageFragmentRoot
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	@SuppressWarnings("restriction")
	private static Manifest extractManifest(IPackageFragmentRoot packageFragmentRoot) throws JavaModelException {

		Manifest result = null;
		final Object[] nonJavaResources = packageFragmentRoot.getNonJavaResources();

		for (Object obj : nonJavaResources) {
			if (obj instanceof JarEntryDirectory) {
				final JarEntryDirectory jarEntryDirectory = (JarEntryDirectory) obj;
				final IJarEntryResource[] jarEntryResources = jarEntryDirectory.getChildren();
				for (IJarEntryResource jarEntryResource : jarEntryResources) {
					if ("MANIFEST.MF".equals(jarEntryResource.getName())) {
						try {
							final InputStream stream = jarEntryResource.getContents();
							result = new Manifest(stream);
						} catch (Exception e) {
							// no manifest as result
						}
					}
				}
			}
		}
		return result;
	}

}
