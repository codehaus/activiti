package org.activiti.designer.runner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class TestRunnerClassGenerator {
	
	private String processId;
	private String processName;

	public void generateTestClass(IResource bpmnResource) throws Exception {
		IProject project = bpmnResource.getProject();
		IFolder sourceFolder = project.getFolder("src").getFolder("test").getFolder("java");
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);

		IPackageFragment pack = srcRoot.createPackageFragment(
				"org.activiti.designer.test", false, null);

		ICompilationUnit cu = pack.createCompilationUnit("ProcessTest.java",
				createTestClass(bpmnResource, pack), false, null);
		
		IFolder testResourceFolder = project.getFolder("src").getFolder("test").getFolder("resources");
		IFile propertiesFile = testResourceFolder.getFile("activiti.cfg.xml");
		InputStream source = new ByteArrayInputStream(createConfigFile().getBytes()); 
		propertiesFile.create(source, true, null);
		source.close();
	}
	
	private String createTestClass(IResource bpmnResource, IPackageFragment pack) {
		parseBpmnXML(bpmnResource.getRawLocation().toOSString());
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n\n");
		buffer.append("import static org.junit.Assert.*;\n\n");
		buffer.append("import java.util.HashMap;\n");
		buffer.append("import java.util.Map;\n\n");
		buffer.append("import org.activiti.engine.ProcessEngine;\n");
		buffer.append("import org.activiti.engine.ProcessEngineConfiguration;;\n");
		buffer.append("import org.activiti.engine.RepositoryService;\n");
		buffer.append("import org.activiti.engine.RuntimeService;\n");
		buffer.append("import org.activiti.engine.runtime.ProcessInstance;\n");
		buffer.append("import org.junit.Test;\n\n");
		buffer.append("public class ProcessTest {\n\n");
		buffer.append("\t@Test\n");
		buffer.append("\tpublic void testProcess() {\n");
		buffer.append("\t\tProcessEngine processEngine = ProcessEngineConfiguration\n");
		buffer.append("\t\t\t\t.createStandaloneInMemProcessEngineConfiguration()\n");
		buffer.append("\t\t\t\t.buildProcessEngine();\n");
		buffer.append("\t\tRuntimeService runtimeService = processEngine.getRuntimeService();\n");
		buffer.append("\t\tRepositoryService repositoryService = processEngine.getRepositoryService();\n");
		buffer.append("\t\trepositoryService.createDeployment()\n");
		buffer.append("\t\t\t\t.addClasspathResource(\"diagrams/" + bpmnResource.getName() + "\")\n");
		buffer.append("\t\t\t\t.deploy();\n");
		buffer.append("\t\tMap<String, Object> variableMap = new HashMap<String, Object>();\n");
		buffer.append("\t\tvariableMap.put(\"name\", \"Activiti\");\n");
		buffer.append("\t\tProcessInstance processInstance = runtimeService.startProcessInstanceByKey(\"" + processId + "\", variableMap);\n");
		buffer.append("\t\tassertNotNull(processInstance.getId());\n");
		buffer.append("\t\tSystem.out.println(\"id \" + processInstance.getId() + \" \"\n");
		buffer.append("\t\t\t\t+ processInstance.getProcessDefinitionId());\n");
		buffer.append("\t}\n");
		buffer.append("}");
		return buffer.toString();
	}
	
	private String createConfigFile() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buffer.append("<activiti-cfg>\n");
		buffer.append("  <database type=\"h2\" schema-strategy=\"create-if-necessary\">\n");
		buffer.append("    <jdbc url=\"jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000\"\n");
		buffer.append("          driver=\"org.h2.Driver\"\n");
		buffer.append("          username=\"sa\"\n");
		buffer.append("          password=\"\" />\n");
		buffer.append("  </database>\n");
		buffer.append("  <job-executor activate=\"off\" />\n");
		buffer.append("</activiti-cfg>");
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
			InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
			XMLStreamReader xtr = xif.createXMLStreamReader(in);
			while(xtr.hasNext()) {
				xtr.next();
				if(xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
					processId = xtr.getAttributeValue(null, "id");
					processName = xtr.getAttributeValue(null, "name");
				}
			}
			xtr.close();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
