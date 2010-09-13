package org.activiti.graphiti.bpmn.designer.runner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.junit.buildpath.BuildPathSupport;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class TestRunnerClassGenerator {

	public static void generateTestClass(IResource bpmnResource) throws Exception {
		IProject project = bpmnResource.getProject();
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime
				.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
			entries.add(JavaCore.newLibraryEntry(
					element.getSystemLibraryPath(), null, null));
		}
		
		IFolder sourceFolder = project.getFolder("src").getFolder("test").getFolder("java");
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		
		@SuppressWarnings("restriction")
		IClasspathEntry[] newEntries = 
			(IClasspathEntry[]) ArrayUtils.add(javaProject.getRawClasspath(), BuildPathSupport.getJUnit4ClasspathEntry());
		javaProject.setRawClasspath(newEntries, null);

		IPackageFragment pack = srcRoot.createPackageFragment(
				"org.activiti.graphiti.bpmn.designer.runner", false, null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append("public class ProcessRunner{");
		buffer.append("\n");
		buffer.append("\t@Test\n");
		buffer.append("\tpublic void testProcess(){\n");
		buffer.append("\t\tProcessEngine processEngine = new ProcessEngineBuilder()\n"); 
		buffer.append("\t\t\t\t.configureFromPropertiesResource(\"activiti.properties\")\n"); 
		buffer.append("\t\t\t\t.buildProcessEngine();\n");
		buffer.append("\t}\n");
		buffer.append("}");

		ICompilationUnit cu = pack.createCompilationUnit("ProcessRunner.java",
				buffer.toString(), false, null);
	}

}
