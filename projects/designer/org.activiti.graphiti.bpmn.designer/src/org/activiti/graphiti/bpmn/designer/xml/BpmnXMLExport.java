package org.activiti.graphiti.bpmn.designer.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class BpmnXMLExport {
	
	private static final String BPMN2_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String ACTIVITI_NAMESPACE = "http://www.activiti.org/bpmn2.0";
	private static final String VALIDATION_TITLE = "BPMN 2.0 Validation";
	
	public static boolean validateBpmn(EList<EObject> contents) {
		int countStartEvents = 0;
		int countEndEvents = 0;
		for (EObject object : contents) {
			if (object instanceof StartEvent && !(object instanceof PictogramElement)) {
				countStartEvents++;
				
			} else if (object instanceof EndEvent && !(object instanceof PictogramElement)) {
				countEndEvents++;
				
			} else if(object instanceof UserTask) {
	        	UserTask userTask = (UserTask) object;
	        	boolean potentialOwnerIsSet = false;
	        	if(userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
	        		potentialOwnerIsSet = true;
	        	}
	        	if(userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
	        		potentialOwnerIsSet = true;
	        	}
	        	if(userTask.getCandidateGroups() != null || userTask.getCandidateGroups().size() > 0) {
	        		potentialOwnerIsSet = true;
	        	}
	        	if(potentialOwnerIsSet == false) {
	        		createErrorMessage("UserTask " + userTask.getName() + " has no assignee, candidate users, candidate groups set");
	        		return false;
	        	}
		        
	        } else if(object instanceof ScriptTask) {
	        	ScriptTask scriptTask = (ScriptTask) object;
	        	if(scriptTask.getScriptFormat() == null || scriptTask.getScriptFormat().length() == 0) {
	        		createErrorMessage("ScriptTask " + scriptTask.getName() + " has no format specified");
	        		return false;
	        	}
		        if(scriptTask.getScript() == null || scriptTask.getScript().length() == 0) {
		        	createErrorMessage("ScriptTask " + scriptTask.getName() + " has no script logic specified");
	        		return false;
		        }
		        
	        } else if(object instanceof ServiceTask) {
	        	ServiceTask serviceTask = (ServiceTask) object;
	        	if(serviceTask.getImplementation() == null || serviceTask.getImplementation().length() == 0) {
	        		createErrorMessage("ServiceTask " + serviceTask.getName() + " has no class specified");
	        		return false;
	        	}
	        } else if(object instanceof SequenceFlow) {
	        	SequenceFlow sequenceFlow = (SequenceFlow) object;
	        	if(sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null || sequenceFlow.getSourceRef().getId().length() == 0) {
	        		createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no source activity");
	        		return false;
	        	}
	        	if(sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null || sequenceFlow.getTargetRef().getId().length() == 0) {
	        		createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no target activity");
	        		return false;
	        	}
	        }
		}
		if(countStartEvents != 1) {
			createErrorMessage("Only 1 start event is allowed for BPMN 2.0 generation");
    		return false;
		}
		return true;
	}
	
	public static void createBpmnFile(URI fileURI, Diagram diagram) {
		try {
			
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IProject[] ps = ws.getRoot().getProjects();
			String strLocation = null;
			if(ps == null || ps.length == 0) return;
			
			IProject p = ps[0];
			IPath location = p.getLocation();
			strLocation = location.toFile().getAbsolutePath();
			strLocation = strLocation.substring(0, strLocation.lastIndexOf(File.separator));
			
			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(strLocation + fileURI.toPlatformString(true)), "UTF-8");
			XMLStreamWriter xtw = xof.createXMLStreamWriter(out);
			boolean succes = createBpmnXML(xtw, diagram);
			xtw.flush();
			xtw.close();
			if(!succes) {
				System.out.println("BPMN 2.0 XML export did not succeed");
				new File(strLocation + fileURI.toPlatformString(true)).delete();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean createBpmnXML(XMLStreamWriter xtw, Diagram diagram) {
		try {
			EList<EObject> contents = diagram.eResource().getContents();
	        xtw.writeStartDocument("UTF-8", "1.0");
	        
	        // start definitions root element
	        xtw.writeStartElement("definitions");
	        xtw.setDefaultNamespace(BPMN2_NAMESPACE);
	        xtw.writeDefaultNamespace(BPMN2_NAMESPACE);
	        xtw.writeAttribute("targetNamespace", ACTIVITI_NAMESPACE);
	        
	        org.eclipse.bpmn2.Process process = ActivitiUiUtil.getProcessObject(diagram);
	        // start process element
	        xtw.writeStartElement("process");
	        xtw.writeAttribute("id", process.getId());
	        xtw.writeAttribute("name", process.getName());
	        if(process.getDocumentation() != null && process.getDocumentation().size() > 0) {
	        	xtw.writeAttribute("documentation", process.getDocumentation().get(0).getText());
	        }
	        
	        // start StartEvent element
	        xtw.writeStartElement("startEvent");
	        StartEvent startEvent = getStartEvent(contents);
	        if(startEvent == null) return false;
	        xtw.writeAttribute("id", startEvent.getId());
	        xtw.writeAttribute("name", startEvent.getName());
	        
	        // end StartEvent element
	        xtw.writeEndElement();
	        
	        for (EObject object : contents) {
	        	createXML(object, xtw);
	        }
	        
	        // start EndEvent element
	        xtw.writeStartElement("endEvent");
	        EndEvent endEvent = getEndEvent(contents);
	        if(endEvent == null) return false;
	        xtw.writeAttribute("id", endEvent.getId());
	        xtw.writeAttribute("name", endEvent.getName());
	        
	        // end EndEvent element
	        xtw.writeEndElement();
	        
	        // end process element
	        xtw.writeEndElement();
	        
	        // end definitions root element
	        xtw.writeEndElement();
	        xtw.writeEndDocument();
	        
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static void createXML(EObject object, XMLStreamWriter xtw) throws Exception {
		if(object instanceof SequenceFlow) {
			SequenceFlow sequenceFlow = (SequenceFlow) object;
	        // start SequenceFlow element
	        xtw.writeStartElement("sequenceFlow");
	        xtw.writeAttribute("id", sequenceFlow.getId());
	        xtw.writeAttribute("name", sequenceFlow.getName());
	        xtw.writeAttribute("sourceRef", sequenceFlow.getSourceRef().getId());
	        xtw.writeAttribute("targetRef", sequenceFlow.getTargetRef().getId());
	        
	        // end SequenceFlow element
	        xtw.writeEndElement();
			
		} else if(object instanceof UserTask) {
        	UserTask userTask = (UserTask) object;
        	if((userTask.getAssignee() != null && userTask.getAssignee().length() > 0) ||
        			(userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) ||
        			(userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0))
        	// start UserTask element
	        xtw.writeStartElement("userTask");
	        xtw.writeAttribute("id", userTask.getId());
	        xtw.writeAttribute("name", userTask.getName());
	        xtw.writeAttribute("activiti", ACTIVITI_NAMESPACE, "assignee", userTask.getAssignee());
	        
	        // end UserTask element
	        xtw.writeEndElement();
	        
        } else if(object instanceof ScriptTask) {
        	ScriptTask scriptTask = (ScriptTask) object;
        	// start ScriptTask element
	        xtw.writeStartElement("scriptTask");
	        xtw.writeAttribute("id", scriptTask.getId());
	        xtw.writeAttribute("name", scriptTask.getName());
	        xtw.writeAttribute("scriptFormat", scriptTask.getScriptFormat());
	        xtw.writeStartElement("script");
	        xtw.writeCData(scriptTask.getScript());
	        xtw.writeEndElement();
	        
	        // end ScriptTask element
	        xtw.writeEndElement();
        
        } else if(object instanceof ServiceTask) {
        	ServiceTask serviceTask = (ServiceTask) object;
        	// start ServiceTask element
	        xtw.writeStartElement("serviceTask");
	        xtw.writeAttribute("id", serviceTask.getId());
	        xtw.writeAttribute("name", serviceTask.getName());
	        xtw.writeAttribute("activiti", ACTIVITI_NAMESPACE, "class", serviceTask.getImplementation());
	        
	        // end ServiceTask element
	        xtw.writeEndElement();
	        
        } else if(object instanceof ParallelGateway) {
        	ParallelGateway parallelGateway = (ParallelGateway) object;
        	// start ParallelGateway element
	        xtw.writeStartElement("parallelGateway");
	        xtw.writeAttribute("id", parallelGateway.getId());
	        xtw.writeAttribute("name", parallelGateway.getName());
	        
	        // end ParallelGateway element
	        xtw.writeEndElement();
        }
	}
	
	private static StartEvent getStartEvent(EList<EObject> contents) {
		for (EObject object : contents) {
			if (object instanceof StartEvent && !(object instanceof PictogramElement)) {
				return (StartEvent) object;
			}
		}
		return null;
	}
	
	private static EndEvent getEndEvent(EList<EObject> contents) {
		for (EObject object : contents) {
			if (object instanceof EndEvent && !(object instanceof PictogramElement)) {
				return (EndEvent) object;
			}
		}
		return null;
	}
	
	private static void createErrorMessage(String message) {
		MessageDialog.openError(
				PlatformUI.getWorkbench().getDisplay().getActiveShell(), VALIDATION_TITLE, message);
	}
}
