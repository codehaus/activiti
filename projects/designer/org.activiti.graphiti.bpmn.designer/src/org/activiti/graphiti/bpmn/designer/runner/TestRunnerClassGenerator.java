package org.activiti.graphiti.bpmn.designer.runner;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.junit.buildpath.BuildPathSupport;

@SuppressWarnings("restriction")
public class TestRunnerClassGenerator {
	
	private static final Path ACTIVITI_JARS = new Path("org.eclipse.jdt.USER_LIBRARY/ACTIVITI_LIB");

	public static void generateTestClass(IResource bpmnResource) throws Exception {
		IProject project = bpmnResource.getProject();
		IFolder sourceFolder = project.getFolder("src").getFolder("test").getFolder("java");
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		
		boolean junitAdded = false;
		boolean activitiLibAdded = false;
		for(IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
			if(cpEntry.getPath().equals(BuildPathSupport.getJUnit4ClasspathEntry())) {
				junitAdded = true;
			} else if(cpEntry.equals(ACTIVITI_JARS)) {
				activitiLibAdded = true;
			}
		}
		if(junitAdded == false || activitiLibAdded == false) {
			int countClasspathEntries = 0;
			if(junitAdded == false) {
				countClasspathEntries++;
			}
			if(activitiLibAdded == false) {
				countClasspathEntries++;
			}
			IClasspathEntry[] entryList = new IClasspathEntry[countClasspathEntries];
			int count = 0;
			if(junitAdded == false) {
				entryList[count++] = BuildPathSupport.getJUnit4ClasspathEntry();
			}
			if(activitiLibAdded == false) {
				entryList[count++] = JavaCore.newContainerEntry(ACTIVITI_JARS);
			}
			IClasspathEntry[] newEntries = 
				(IClasspathEntry[]) ArrayUtils.addAll(javaProject.getRawClasspath(), entryList);
			javaProject.setRawClasspath(newEntries, null);
		}

		IPackageFragment pack = srcRoot.createPackageFragment(
				"org.activiti.graphiti.bpmn.designer.runner", false, null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n\n");
		buffer.append("import static org.junit.Assert.*;\n");
		buffer.append("import org.activiti.engine.ProcessEngine;\n");
		buffer.append("import org.activiti.engine.ProcessEngineBuilder;\n");
		buffer.append("import org.activiti.engine.RepositoryService;\n");
		buffer.append("import org.activiti.engine.RuntimeService;\n");
		buffer.append("import org.activiti.engine.runtime.ProcessInstance;\n");
		buffer.append("import org.junit.Test;\n\n");
		buffer.append("public class ProcessRunner{\n\n");
		buffer.append("\t@Test\n");
		buffer.append("\tpublic void testProcess(){\n");
		buffer.append("\t\tProcessEngine processEngine = new ProcessEngineBuilder()\n"); 
		buffer.append("\t\t\t\t.configureFromPropertiesResource(\"activiti.properties\")\n"); 
		buffer.append("\t\t\t\t.buildProcessEngine();\n");
		buffer.append("\t\tRuntimeService runtimeService = processEngine.getRuntimeService();\n");
		buffer.append("\t\tRepositoryService repositoryService = processEngine.getRepositoryService();\n");
		buffer.append("\t\trepositoryService.createDeployment()\n");
		buffer.append("\t\t\t\t.addClasspathResource(\"" + bpmnResource.getName() + "\")\n");
		buffer.append("\t\t\t\t.deploy();\n");
		buffer.append("\t\tProcessInstance processInstance = runtimeService.startProcessInstanceByKey(\"helloworld\");\n");
		buffer.append("\t\tassertNotNull(processInstance.getId());\n");
		buffer.append("\t\tSystem.out.println(\"id \" + processInstance.getId() + \" \"\n");
		buffer.append("\t\t\t\t+ processInstance.getProcessDefinitionId());\n");
		buffer.append("\t}\n");
		buffer.append("}");

		ICompilationUnit cu = pack.createCompilationUnit("ProcessRunner.java",
				buffer.toString(), false, null);
	}

}
