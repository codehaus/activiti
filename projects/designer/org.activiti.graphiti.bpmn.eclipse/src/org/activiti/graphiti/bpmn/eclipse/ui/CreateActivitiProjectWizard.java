package org.activiti.graphiti.bpmn.eclipse.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.activiti.graphiti.bpmn.eclipse.common.ActivitiProjectNature;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class CreateActivitiProjectWizard extends BasicNewProjectResourceWizard {

	private static final String JAVACORE_NATURE_ID = JavaCore.NATURE_ID;
	private static final String ACTIVITI_NATURE_ID = ActivitiProjectNature.NATURE_ID;

	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		IProject newProject = getNewProject();

		try {

			IProjectDescription description = newProject.getDescription();
			String[] newNatures = new String[2];
			newNatures[0] = JAVACORE_NATURE_ID;
			newNatures[1] = ACTIVITI_NATURE_ID;
			description.setNatureIds(newNatures);
			newProject.setDescription(description, null);

			IJavaProject javaProject = JavaCore.create(newProject);

			createSourceFolders(newProject);

			IClasspathEntry[] entries = createClasspathEntries(javaProject);

			javaProject.setRawClasspath(entries, null);
		} catch (CoreException e) {
			return false;
		}

		return true;
	}

	private IClasspathEntry[] createClasspathEntries(IJavaProject javaProject) {

		IPath srcPath1 = javaProject.getPath().append("src/main/java");
		IPath srcPath2 = javaProject.getPath().append("src/main/resources");
		IPath srcPath3 = javaProject.getPath().append("src/test/java");
		IPath srcPath4 = javaProject.getPath().append("src/test/resources");

		IClasspathEntry[] entries = { JavaCore.newSourceEntry(srcPath1), JavaCore.newSourceEntry(srcPath2),
				JavaCore.newSourceEntry(srcPath3), JavaCore.newSourceEntry(srcPath4) };

		return entries;
	}

	private void createSourceFolders(IProject project) throws CoreException {

		List<String> sourceFolders = Collections.synchronizedList(new LinkedList<String>());

		sourceFolders.add("src");
		sourceFolders.add("src/main");
		sourceFolders.add("src/main/java");
		sourceFolders.add("src/main/resources/");
		sourceFolders.add("src/test/");
		sourceFolders.add("src/test/java/");
		sourceFolders.add("src/test/resources");

		for (String folder : sourceFolders) {
			IFolder sourceFolder = project.getFolder(folder);
			sourceFolder.create(false, true, null);
		}
	}
}
