package org.activiti.graphiti.bpmn.designer.runner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
	
	private String processId;
	private String processName;

	public void generateTestClass(IResource bpmnResource) throws Exception {
		IProject project = bpmnResource.getProject();
		IFolder sourceFolder = project.getFolder("src").getFolder("test").getFolder("java");
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);
		
		boolean junitAdded = false;
		boolean activitiLibAdded = false;
		for(IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
			if(cpEntry.equals(BuildPathSupport.getJUnit4ClasspathEntry())) {
				junitAdded = true;
			} else if(cpEntry.getPath().equals(ACTIVITI_JARS)) {
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

		ICompilationUnit cu = pack.createCompilationUnit("ProcessRunner.java",
				createTestClass(bpmnResource, pack), false, null);
		
		IFolder testResourceFolder = project.getFolder("src").getFolder("test").getFolder("resources");
		IFile propertiesFile = testResourceFolder.getFile("activiti.properties");
		InputStream source = new ByteArrayInputStream(createPropertyFile().getBytes()); 
		propertiesFile.create(source, true, null);
	}
	
	private String createTestClass(IResource bpmnResource, IPackageFragment pack) {
		parseBpmnXML(bpmnResource.getLocationURI().toString());
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
		buffer.append("\t\t\t\t.addClasspathResource(\"diagrams/" + bpmnResource.getName() + "\")\n");
		buffer.append("\t\t\t\t.deploy();\n");
		buffer.append("\t\tProcessInstance processInstance = runtimeService.startProcessInstanceByKey(\"" + processId + "\");\n");
		buffer.append("\t\tassertNotNull(processInstance.getId());\n");
		buffer.append("\t\tSystem.out.println(\"id \" + processInstance.getId() + \" \"\n");
		buffer.append("\t\t\t\t+ processInstance.getProcessDefinitionId());\n");
		buffer.append("\t}\n");
		buffer.append("}");
		return buffer.toString();
	}
	
	private String createPropertyFile() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("database=h2\n");
		buffer.append("jdbc.driver=org.h2.Driver\n");
		buffer.append("jdbc.url=jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000\n");
		buffer.append("jdbc.username=sa\n");
		buffer.append("jdbc.password=\n");
		buffer.append("db.schema.strategy=create-drop\n");
		buffer.append("jdbc.password=\n");
		buffer.append("job.executor.auto.activate=off\n");
		return buffer.toString();
	}
	
	private void parseBpmnXML(String filePath) {
		try {
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IProject[] ps = ws.getRoot().getProjects();
			String strLocation = null;
			if(ps == null || ps.length == 0) return;
			
			IProject p = ps[0];
			IPath location = p.getLocation();
			strLocation = location.toFile().getAbsolutePath();
			strLocation = strLocation.substring(0, strLocation.lastIndexOf(File.separator));
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStreamReader in = new InputStreamReader(new FileInputStream(strLocation + filePath), "UTF-8");
			XMLStreamReader xtr = xif.createXMLStreamReader(in);
			while(xtr.hasNext()) {
				xtr.next();
				if("process".equalsIgnoreCase(xtr.getLocalName())) {
					processId = xtr.getAttributeValue(null, "id");
					processName = xtr.getAttributeValue(null, "name");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
