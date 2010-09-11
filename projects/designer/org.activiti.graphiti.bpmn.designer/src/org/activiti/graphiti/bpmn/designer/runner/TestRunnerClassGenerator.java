package org.activiti.graphiti.bpmn.designer.runner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class TestRunnerClassGenerator {

	public static void generateTestClass(IResource bpmnResource)
			throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("ActivitiProcessRunner");
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime
				.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(
					element.getSystemLibraryPath(), null, null));
		}
		// add libs to project class path
		javaProject.setRawClasspath(
				entries.toArray(new IClasspathEntry[entries.size()]), null);

		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);

		IPackageFragmentRoot srcRoot = javaProject
				.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(srcRoot
				.getPath());
		javaProject.setRawClasspath(newEntries, null);

		IPackageFragment pack = srcRoot.createPackageFragment(
				"org.activiti.graphiti.bpmn.designer.runner", false, null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append("public class ProcessRunner{");
		buffer.append("\n");
		buffer.append("}");

		ICompilationUnit cu = pack.createCompilationUnit("ProcessRunner.java",
				buffer.toString(), false, null);
	}

}
