/**
 * 
 */
package org.activiti.designer.export.bpmn20.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.util.Util;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class BPMN20ExportMarshaller extends AbstractExportMarshaller {

	private static final String FILENAME_PATTERN = ExportMarshaller.PLACEHOLDER_ORIGINAL_FILENAME + ".bpmn20.xml";

	private static final String BPMN2_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String XPATH_NAMESPACE = "http://www.w3.org/1999/XPath";
	private static final String PROCESS_NAMESPACE = "http://www.activiti.org/test";
	private static final String ACTIVITI_EXTENSIONS_NAMESPACE = "http://activiti.org/bpmn";
	private static final String VALIDATION_TITLE = "BPMN 2.0 Validation";

	private IProgressMonitor monitor;
	private Diagram diagram;

	/**
	 * 
	 */
	public BPMN20ExportMarshaller() {
	}

	@Override
	public String getMarshallerName() {
		return ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME;
	}

	@Override
	public String getFormatName() {
		return "Activiti BPMN 2.0";
	}

	@Override
	public String getFilenamePattern() {
		return FILENAME_PATTERN;
	}

	@Override
	public void marshallDiagram(Diagram diagram, IProgressMonitor monitor) {
		System.out
				.println("Marshalling to " + getFormatName() + " format using " + getMarshallerName() + " marshaller");

		this.monitor = monitor;
		this.diagram = diagram;

		monitor.beginTask("", 100);

		// Clear problems for this diagram first
		clearProblems(getResource(diagram.eResource().getURI()));

		URI diagramURI = getURIForDiagram(diagram);
		// TODO: marshall parent instead
		if (!diagramURI.toPlatformString(true).contains("subprocess")) {
			boolean validBpmn = validateDiagram(getResourceForDiagram(diagram).getContents());

			if (validBpmn) {

				monitor.worked(40);

				marshallBPMNDiagram();

				monitor.worked(60);
			}
		}
		monitor.done();
	}

	private boolean validateDiagram(EList<EObject> contents) {

		int countStartEvents = 0;
		int countEndEvents = 0;
		for (EObject object : contents) {
			if (object instanceof StartEvent && !(object instanceof PictogramElement)) {
				countStartEvents++;

			} else if (object instanceof EndEvent && !(object instanceof PictogramElement)) {
				countEndEvents++;

			} else if (object instanceof UserTask) {
				UserTask userTask = (UserTask) object;
				boolean potentialOwnerIsSet = false;
				if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
					potentialOwnerIsSet = true;
				}
				if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
					potentialOwnerIsSet = true;
				}
				if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
					potentialOwnerIsSet = true;
				}
				if (potentialOwnerIsSet == false) {
					createErrorMessage("UserTask " + userTask.getName()
							+ " has no assignee, candidate users, candidate groups set");
					return false;
				}

			} else if (object instanceof ScriptTask) {
				ScriptTask scriptTask = (ScriptTask) object;
				if (scriptTask.getScriptFormat() == null || scriptTask.getScriptFormat().length() == 0) {
					createErrorMessage("ScriptTask " + scriptTask.getName() + " has no format specified");
					return false;
				}
				if (scriptTask.getScript() == null || scriptTask.getScript().length() == 0) {
					createErrorMessage("ScriptTask " + scriptTask.getName() + " has no script logic specified");
					return false;
				}

			} else if (object instanceof ServiceTask) {
				ServiceTask serviceTask = (ServiceTask) object;
				if (serviceTask.getImplementation() == null || serviceTask.getImplementation().length() == 0) {
					createErrorMessage("ServiceTask " + serviceTask.getName() + " has no class specified");
					return false;
				}
			} else if (object instanceof SequenceFlow) {
				SequenceFlow sequenceFlow = (SequenceFlow) object;
				if (sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null
						|| sequenceFlow.getSourceRef().getId().length() == 0) {
					createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no source activity");
					return false;
				}
				if (sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null
						|| sequenceFlow.getTargetRef().getId().length() == 0) {
					createErrorMessage("SequenceFlow " + sequenceFlow.getName() + " has no target activity");
					return false;
				}
			} else if (object instanceof SubProcess) {
				SubProcess subProcess = (SubProcess) object;

				final URI subprocessURI = Util.getSubProcessURI(diagram, subProcess.getId());

				// TODO
				// Clear problems for this sub diagram first
				// clearProblems(getResource(subprocessURI));

				if (!resourceExists(subprocessURI)) {
					createErrorMessage("SubProcess " + subProcess.getName()
							+ " has no diagram yet. Double click to create a sub process diagram.");
					return false;
				}
			}
		}
		if (countStartEvents != 1) {
			createErrorMessage("Only 1 start event is allowed for BPMN 2.0 generation");
			return false;
		}
		return true;
	}

	private void marshallBPMNDiagram() {
		try {

			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(baos, "UTF-8");

			XMLStreamWriter xtw = xof.createXMLStreamWriter(out);

			final EList<EObject> contents = diagram.eResource().getContents();

			Process process = null;

			for (final EObject eObject : contents) {
				if (eObject instanceof Process) {
					process = (Process) eObject;
					break;
				}
			}

			if (process == null) {
				addProblemToDiagram(diagram, "Process cannot be null", null);
			}

			xtw.writeStartDocument("UTF-8", "1.0");

			// start definitions root element
			xtw.writeStartElement("definitions");
			xtw.setDefaultNamespace(BPMN2_NAMESPACE);
			xtw.writeDefaultNamespace(BPMN2_NAMESPACE);
			xtw.writeNamespace("xsi", XSI_NAMESPACE);
			xtw.writeNamespace("activiti", ACTIVITI_EXTENSIONS_NAMESPACE);
			xtw.writeAttribute("typeLanguage", SCHEMA_NAMESPACE);
			xtw.writeAttribute("expressionLanguage", XPATH_NAMESPACE);
			xtw.writeAttribute("targetNamespace", PROCESS_NAMESPACE);

			// start process element
			xtw.writeStartElement("process");
			xtw.writeAttribute("id", process.getId());
			xtw.writeAttribute("name", process.getName());
			if (process.getDocumentation() != null && process.getDocumentation().size() > 0
					&& process.getDocumentation().get(0) != null && process.getDocumentation().get(0).getText() != null
					&& process.getDocumentation().get(0).getText().length() > 0) {

				xtw.writeStartElement("documentation");
				xtw.writeCharacters(process.getDocumentation().get(0).getText());
				xtw.writeEndElement();
			}

			// start StartEvent element
			xtw.writeStartElement("startEvent");
			StartEvent startEvent = getStartEvent(contents);

			if (startEvent == null)
				addProblemToDiagram(diagram, "start event cannot be null", null);
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

			if (endEvent == null)
				addProblemToDiagram(diagram, "end event cannot be null", null);
			xtw.writeAttribute("id", endEvent.getId());
			xtw.writeAttribute("name", endEvent.getName());

			// end EndEvent element
			xtw.writeEndElement();

			// end process element
			xtw.writeEndElement();

			// end definitions root element
			xtw.writeEndElement();
			xtw.writeEndDocument();

			xtw.flush();

			final byte[] bytes = baos.toByteArray();
			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			saveResource(getRelativeURIForDiagram(diagram, getFilenamePattern()), bais, this.monitor);

			xtw.close();
		} catch (Exception e) {
			addProblemToDiagram(diagram, "An exception occurred while creating the BPMN 2.0 XML: " + e.getMessage(),
					null);
		}
	}

	private void createXML(EObject object, XMLStreamWriter xtw) throws Exception {
		if (object instanceof SequenceFlow) {
			SequenceFlow sequenceFlow = (SequenceFlow) object;
			// start SequenceFlow element
			xtw.writeStartElement("sequenceFlow");
			xtw.writeAttribute("id", sequenceFlow.getId());
			xtw.writeAttribute("name", sequenceFlow.getName());
			xtw.writeAttribute("sourceRef", sequenceFlow.getSourceRef().getId());
			xtw.writeAttribute("targetRef", sequenceFlow.getTargetRef().getId());
			if (sequenceFlow.getConditionExpression() != null
					&& sequenceFlow.getConditionExpression().getBody() != null
					&& sequenceFlow.getConditionExpression().getBody().length() > 0) {

				String condition = sequenceFlow.getConditionExpression().getBody();
				// start conditionExpression element
				xtw.writeStartElement("conditionExpression");
				xtw.writeAttribute("xsi", SCHEMA_NAMESPACE, "type", "tFormalExpression");
				xtw.writeCData(condition);

				// end conditionExpression element
				xtw.writeEndElement();
			}

			// end SequenceFlow element
			xtw.writeEndElement();

		} else if (object instanceof UserTask) {
			UserTask userTask = (UserTask) object;
			if ((userTask.getAssignee() != null && userTask.getAssignee().length() > 0)
					|| (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0)
					|| (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0)) {

				// start UserTask element
				xtw.writeStartElement("userTask");
				xtw.writeAttribute("id", userTask.getId());
				xtw.writeAttribute("name", userTask.getName());

				// TODO revisit once the designer supports mixing these
				// configurations as they are now exclusive
				if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "assignee", userTask.getAssignee());
				} else if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
					Iterator<CandidateUser> candidateUserIterator = userTask.getCandidateUsers().iterator();
					String candidateUsers = candidateUserIterator.next().getUser();
					while (candidateUserIterator.hasNext()) {
						candidateUsers += ", " + candidateUserIterator.next().getUser();
					}
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers", candidateUsers);
				} else {
					Iterator<CandidateGroup> candidateGroupIterator = userTask.getCandidateGroups().iterator();
					String candidateGroups = candidateGroupIterator.next().getGroup();
					while (candidateGroupIterator.hasNext()) {
						candidateGroups += ", " + candidateGroupIterator.next().getGroup();
					}
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups", candidateGroups);
				}

				if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", userTask.getFormKey());
				}

				// end UserTask element
				xtw.writeEndElement();
			}

		} else if (object instanceof ScriptTask) {
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

		} else if (object instanceof ServiceTask) {
			ServiceTask serviceTask = (ServiceTask) object;
			// start ServiceTask element
			xtw.writeStartElement("serviceTask");
			xtw.writeAttribute("id", serviceTask.getId());
			xtw.writeAttribute("name", serviceTask.getName());

			if (serviceTask.getImplementationType() == null || serviceTask.getImplementationType().length() == 0
					|| "classType".equals(serviceTask.getImplementationType())) {

				if (serviceTask.getImplementation() != null && serviceTask.getImplementation().length() > 0) {
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "class",
							serviceTask.getImplementation());
				}
			} else {
				if (serviceTask.getImplementation() != null && serviceTask.getImplementation().length() > 0) {
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "expression",
							serviceTask.getImplementation());
				}
			}

			// end ServiceTask element
			xtw.writeEndElement();

		} else if (object instanceof MailTask) {
			MailTask mailTask = (MailTask) object;
			// start MailTask element
			xtw.writeStartElement("serviceTask");
			xtw.writeAttribute("id", mailTask.getId());
			xtw.writeAttribute("name", mailTask.getName());
			xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "type", "mail");

			xtw.writeStartElement("extensionElements");

			if (mailTask.getTo() != null && mailTask.getTo().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "to");
				xtw.writeAttribute("expression", mailTask.getTo());
				xtw.writeEndElement();
			}
			if (mailTask.getFrom() != null && mailTask.getFrom().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "from");
				xtw.writeAttribute("expression", mailTask.getFrom());
				xtw.writeEndElement();
			}
			if (mailTask.getSubject() != null && mailTask.getSubject().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "subject");
				xtw.writeAttribute("expression", mailTask.getSubject());
				xtw.writeEndElement();
			}
			if (mailTask.getCc() != null && mailTask.getCc().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "cc");
				xtw.writeAttribute("expression", mailTask.getCc());
				xtw.writeEndElement();
			}
			if (mailTask.getBcc() != null && mailTask.getBcc().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "bcc");
				xtw.writeAttribute("expression", mailTask.getBcc());
				xtw.writeEndElement();
			}
			if (mailTask.getHtml() != null && mailTask.getHtml().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "html");
				xtw.writeStartElement("activiti", "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeCData(mailTask.getHtml());
				xtw.writeEndElement();
				xtw.writeEndElement();
			}
			if (mailTask.getText() != null && mailTask.getText().length() > 0) {
				xtw.writeStartElement("activiti", "field", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeAttribute("name", "text");
				xtw.writeStartElement("activiti", "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
				xtw.writeCData(mailTask.getText());
				xtw.writeEndElement();
				xtw.writeEndElement();
			}
			xtw.writeEndElement();

			// end MailTask element
			xtw.writeEndElement();

		} else if (object instanceof ManualTask) {
			ManualTask manualTask = (ManualTask) object;
			// start ManualTask element
			xtw.writeStartElement("manualTask");
			xtw.writeAttribute("id", manualTask.getId());
			xtw.writeAttribute("name", manualTask.getName());
			// end ManualTask element
			xtw.writeEndElement();

		} else if (object instanceof ParallelGateway) {
			ParallelGateway parallelGateway = (ParallelGateway) object;
			// start ParallelGateway element
			xtw.writeStartElement("parallelGateway");
			xtw.writeAttribute("id", parallelGateway.getId());
			xtw.writeAttribute("name", parallelGateway.getName());

			// end ParallelGateway element
			xtw.writeEndElement();

		} else if (object instanceof ExclusiveGateway) {
			ExclusiveGateway exclusiveGateway = (ExclusiveGateway) object;
			// start ExclusiveGateway element
			xtw.writeStartElement("exclusiveGateway");
			xtw.writeAttribute("id", exclusiveGateway.getId());
			xtw.writeAttribute("name", exclusiveGateway.getName());

			// end ExclusiveGateway element
			xtw.writeEndElement();

		} else if (object instanceof SubProcess) {
			SubProcess subProcess = (SubProcess) object;
			// start SubProcess element
			xtw.writeStartElement("subProcess");
			xtw.writeAttribute("id", subProcess.getId());
			xtw.writeAttribute("name", subProcess.getName());

			this.createSubProcessXML(xtw, subProcess.getId());

			// end SubProcess element
			xtw.writeEndElement();
		}
	}

	private void createSubProcessXML(XMLStreamWriter xtw, String subProcessId) throws Exception {

		IResource resource = getResource(Util.getSubProcessURI(diagram, subProcessId));

		ResourceSet set = new ResourceSetImpl();
		final Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile((IFile) resource, set);

		EList<EObject> contents = diagram.eResource().getContents();

		// start StartEvent element
		xtw.writeStartElement("startEvent");
		StartEvent startEvent = getStartEvent(contents);
		if (startEvent == null)
			addProblemToDiagram(diagram, "Start event for sub process cannot be null", null);
		xtw.writeAttribute("id", subProcessId + "_" + startEvent.getId());
		xtw.writeAttribute("name", startEvent.getName());

		// end StartEvent element
		xtw.writeEndElement();

		for (EObject object : contents) {

			if (object instanceof SequenceFlow) {
				SequenceFlow sequenceFlow = (SequenceFlow) object; // start SequenceFlow element
				xtw.writeStartElement("sequenceFlow");
				xtw.writeAttribute("id", subProcessId + "_" + sequenceFlow.getId());
				xtw.writeAttribute("name", sequenceFlow.getName());
				xtw.writeAttribute("sourceRef", subProcessId + "_" + sequenceFlow.getSourceRef().getId());
				xtw.writeAttribute("targetRef", subProcessId + "_" + sequenceFlow.getTargetRef().getId());
				if (sequenceFlow.getConditionExpression() != null
						&& sequenceFlow.getConditionExpression().getBody() != null
						&& sequenceFlow.getConditionExpression().getBody().length() > 0) {

					String condition = sequenceFlow.getConditionExpression().getBody();
					// start conditionExpression element
					xtw.writeStartElement("conditionExpression");
					xtw.writeAttribute("xsi", SCHEMA_NAMESPACE, "type", "tFormalExpression");
					xtw.writeCData(condition);

					// end conditionExpression element
					xtw.writeEndElement();
				}

				// end SequenceFlow element
				xtw.writeEndElement();

			} else if (object instanceof UserTask) {
				UserTask userTask = (UserTask) object;
				if ((userTask.getAssignee() != null && userTask.getAssignee().length() > 0)
						|| (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0)
						|| (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0)) {

					// start UserTask element
					xtw.writeStartElement("userTask");
					xtw.writeAttribute("id", subProcessId + "_" + userTask.getId());
					xtw.writeAttribute("name", userTask.getName());
					xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "assignee", userTask.getAssignee());

					// end UserTask element
					xtw.writeEndElement();
				}

			} else if (object instanceof ScriptTask) {
				ScriptTask scriptTask = (ScriptTask) object; // start ScriptTask element
				xtw.writeStartElement("scriptTask");
				xtw.writeAttribute("id", subProcessId + "_" + scriptTask.getId());
				xtw.writeAttribute("name", scriptTask.getName());
				xtw.writeAttribute("scriptFormat", scriptTask.getScriptFormat());
				xtw.writeStartElement("script");
				xtw.writeCData(scriptTask.getScript());
				xtw.writeEndElement();

				// end ScriptTask element
				xtw.writeEndElement();

			} else if (object instanceof ServiceTask) {
				ServiceTask serviceTask = (ServiceTask) object; // start ServiceTask element
				xtw.writeStartElement("serviceTask");
				xtw.writeAttribute("id", subProcessId + "_" + serviceTask.getId());
				xtw.writeAttribute("name", serviceTask.getName());
				xtw.writeAttribute("activiti", ACTIVITI_EXTENSIONS_NAMESPACE, "class", serviceTask.getImplementation());

				// end ServiceTask element
				xtw.writeEndElement();

			} else if (object instanceof ParallelGateway) {
				ParallelGateway parallelGateway = (ParallelGateway) object; // start ParallelGateway element
				xtw.writeStartElement("parallelGateway");
				xtw.writeAttribute("id", subProcessId + "_" + parallelGateway.getId());
				xtw.writeAttribute("name", parallelGateway.getName());

				// end ParallelGateway element
				xtw.writeEndElement();

			} else if (object instanceof ExclusiveGateway) {
				ExclusiveGateway exclusiveGateway = (ExclusiveGateway) object;
				// start ExclusiveGateway element
				xtw.writeStartElement("exclusiveGateway");
				xtw.writeAttribute("id", subProcessId + "_" + exclusiveGateway.getId());
				xtw.writeAttribute("name", exclusiveGateway.getName());

				// end ExclusiveGateway element
				xtw.writeEndElement();
			}
		}

		// start EndEvent element
		xtw.writeStartElement("endEvent");
		EndEvent endEvent = getEndEvent(contents);
		if (endEvent == null)
			addProblemToDiagram(diagram, "End event for subprocess cannot be null", null);
		xtw.writeAttribute("id", subProcessId + "_" + endEvent.getId());
		xtw.writeAttribute("name", endEvent.getName());

		// end EndEvent element
		xtw.writeEndElement();
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

	@Deprecated
	private void createErrorMessage(String message) {
		addProblemToDiagram(diagram, message, null);
	}
}
