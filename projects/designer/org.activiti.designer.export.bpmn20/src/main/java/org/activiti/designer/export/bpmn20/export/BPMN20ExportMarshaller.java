/**
 * 
 */
package org.activiti.designer.export.bpmn20.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.ExtensionConstants;
import org.activiti.designer.eclipse.extension.export.AbstractExportMarshaller;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.Preferences;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.Util;
import org.eclipse.bpmn2.CandidateGroup;
import org.eclipse.bpmn2.CandidateUser;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
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
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
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
  private static final String ACTIVITI_EXTENSIONS_PREFIX = "activiti";
  private static final String BPMNDI_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/DI";
  private static final String BPMNDI_PREFIX = "bpmndi";
  private static final String OMGDC_NAMESPACE = "http://www.omg.org/spec/DD/20100524/DC";
  private static final String OMGDC_PREFIX = "omgdc";
  private static final String OMGDI_NAMESPACE = "http://www.omg.org/spec/DD/20100524/DI";
  private static final String OMGDI_PREFIX = "omgdi";

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
  public void marshallDiagram(Diagram diagram, IProgressMonitor monitor) {
    this.monitor = monitor;
    this.diagram = diagram;

    monitor.beginTask("", 100);

    // Clear problems for this diagram first
    clearMarkers(getResource(diagram.eResource().getURI()));

    URI diagramURI = getURIForDiagram(diagram);

    boolean performMarshalling = true;

    if (!diagramURI.toPlatformString(true).contains("subprocess")) {

      // Validate if the BPMN validator is checked in the preferences
      if (PreferencesUtil.getBooleanPreference(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT)) {

        boolean validBpmn = invokeValidator(ActivitiBPMNDiagramConstants.BPMN_VALIDATOR_ID, diagram, monitor);

        if (!validBpmn) {

          // Get the behavior required
          final String behavior = PreferencesUtil.getStringPreference(Preferences.SKIP_BPMN_MARSHALLER_ON_VALIDATION_FAILURE);

          // Flag marshalling to be skipped if the behavior is to skip or not
          // defined (mirrors default behavior)
          if (behavior == null || ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_SKIP.equals(behavior)) {
            performMarshalling = false;
            // add additional marker for user
            addProblemToDiagram(diagram, "Marshalling to " + getFormatName() + " format was skipped because validation of the diagram failed.", null);
          }
        }
      }

    } else {
      // TODO: marshall parent instead
      performMarshalling = false;
    }

    if (performMarshalling) {
      monitor.worked(40);
      marshallBPMNDiagram();
      monitor.worked(60);
    }
    monitor.done();
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
      xtw.writeNamespace(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE);
      xtw.writeNamespace(BPMNDI_PREFIX, BPMNDI_NAMESPACE);
      xtw.writeNamespace(OMGDC_PREFIX, OMGDC_NAMESPACE);
      xtw.writeNamespace(OMGDI_PREFIX, OMGDI_NAMESPACE);
      xtw.writeAttribute("typeLanguage", SCHEMA_NAMESPACE);
      xtw.writeAttribute("expressionLanguage", XPATH_NAMESPACE);
      xtw.writeAttribute("targetNamespace", PROCESS_NAMESPACE);

      // start process element
      xtw.writeStartElement("process");
      xtw.writeAttribute("id", process.getId());
      xtw.writeAttribute("name", process.getName());
      if (process.getDocumentation() != null && process.getDocumentation().size() > 0 && process.getDocumentation().get(0) != null
              && process.getDocumentation().get(0).getText() != null && process.getDocumentation().get(0).getText().length() > 0) {

        xtw.writeStartElement("documentation");
        xtw.writeCharacters(process.getDocumentation().get(0).getText());
        xtw.writeEndElement();
      }

      for (EObject object : contents) {
        createXML(object, xtw, "");
      }

      // end process element
      xtw.writeEndElement();
      
      createDIXML(diagram, xtw, process);

      // end definitions root element
      xtw.writeEndElement();
      xtw.writeEndDocument();

      xtw.flush();

      final byte[] bytes = baos.toByteArray();
      final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      saveResource(getRelativeURIForDiagram(diagram, FILENAME_PATTERN), bais, this.monitor);

      xtw.close();
    } catch (Exception e) {
      e.printStackTrace();
      addProblemToDiagram(diagram, "An exception occurred while creating the BPMN 2.0 XML: " + e.getMessage(), null);
    }
  }

  private void createXML(EObject object, XMLStreamWriter xtw, String subProcessId) throws Exception {
    if (object instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) object;
      // start StartEvent element
      xtw.writeStartElement("startEvent");
      xtw.writeAttribute("id", startEvent.getId());
      xtw.writeAttribute("name", startEvent.getName());

      // end StartEvent element
      xtw.writeEndElement();

    } else if(object instanceof EndEvent) {
      EndEvent endEvent = (EndEvent) object;
      // start EndEvent element
      xtw.writeStartElement("endEvent");
      xtw.writeAttribute("id", endEvent.getId());
      xtw.writeAttribute("name", endEvent.getName());

      // end EndEvent element
      xtw.writeEndElement();
      
    } else if (object instanceof SequenceFlow) {
      SequenceFlow sequenceFlow = (SequenceFlow) object;
      // start SequenceFlow element
      xtw.writeStartElement("sequenceFlow");
      xtw.writeAttribute("id", subProcessId + sequenceFlow.getId());
      if(sequenceFlow.getName() == null) {
        xtw.writeAttribute("name", "");
      } else {
        xtw.writeAttribute("name", sequenceFlow.getName());
      }
      xtw.writeAttribute("sourceRef", subProcessId + sequenceFlow.getSourceRef().getId());
      xtw.writeAttribute("targetRef", subProcessId + sequenceFlow.getTargetRef().getId());
      if (sequenceFlow.getConditionExpression() != null && sequenceFlow.getConditionExpression().getBody() != null
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
        xtw.writeAttribute("id", subProcessId + userTask.getId());
        xtw.writeAttribute("name", userTask.getName());

        // TODO revisit once the designer supports mixing these
        // configurations as they are now exclusive
        if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "assignee", userTask.getAssignee());
        } else if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
          Iterator<CandidateUser> candidateUserIterator = userTask.getCandidateUsers().iterator();
          String candidateUsers = candidateUserIterator.next().getUser();
          while (candidateUserIterator.hasNext()) {
            candidateUsers += ", " + candidateUserIterator.next().getUser();
          }
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateUsers", candidateUsers);
        } else {
          Iterator<CandidateGroup> candidateGroupIterator = userTask.getCandidateGroups().iterator();
          String candidateGroups = candidateGroupIterator.next().getGroup();
          while (candidateGroupIterator.hasNext()) {
            candidateGroups += ", " + candidateGroupIterator.next().getGroup();
          }
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "candidateGroups", candidateGroups);
        }

        if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "formKey", userTask.getFormKey());
        }

        if (userTask.getDocumentation() != null && userTask.getDocumentation().size() > 0) {

          final Documentation documentation = userTask.getDocumentation().get(0);

          if (documentation.getText() != null && !"".equals(documentation.getText())) {

            // start documentation element
            xtw.writeStartElement("documentation");

            if (documentation.getId() != null) {
              xtw.writeAttribute("id", documentation.getId());
            }
            xtw.writeAttribute("textFormat", "text/plain");

            xtw.writeCharacters(documentation.getText());

            // end documentation element
            xtw.writeEndElement();
          }

        }

        // end UserTask element
        xtw.writeEndElement();
      }

    } else if (object instanceof ScriptTask) {
      ScriptTask scriptTask = (ScriptTask) object;
      // start ScriptTask element
      xtw.writeStartElement("scriptTask");
      xtw.writeAttribute("id", subProcessId + scriptTask.getId());
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
      xtw.writeAttribute("id", subProcessId + serviceTask.getId());
      xtw.writeAttribute("name", serviceTask.getName());

      if (serviceTask.getImplementationType() == null || serviceTask.getImplementationType().length() == 0
              || "classType".equals(serviceTask.getImplementationType())) {

        if (serviceTask.getImplementation() != null && serviceTask.getImplementation().length() > 0) {
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "class", serviceTask.getImplementation());
        }
      } else {
        if (serviceTask.getImplementation() != null && serviceTask.getImplementation().length() > 0) {
          xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "expression", serviceTask.getImplementation());
        }
      }

      if (serviceTask.getFieldExtensions() != null && serviceTask.getFieldExtensions().size() > 0) {
        xtw.writeStartElement("extensionElements");
        for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
          xtw.writeAttribute("name", fieldExtension.getFieldname());
          if (fieldExtension.getExpression().contains("${")) {
            xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
          } else {
            xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
          }
          xtw.writeCharacters(fieldExtension.getExpression());
          xtw.writeEndElement();
          xtw.writeEndElement();
        }
        xtw.writeEndElement();
      }

      if (serviceTask.getCustomProperties() != null && serviceTask.getCustomProperties().size() > 0) {
        boolean firstCustomProperty = true;
        for (CustomProperty customProperty : serviceTask.getCustomProperties()) {
          if (ExtensionConstants.PROPERTY_ID_CUSTOM_SERVICE_TASK.equals(customProperty.getName())) {
            continue;
          }
          if (firstCustomProperty == true) {
            xtw.writeStartElement("extensionElements");
            firstCustomProperty = false;
          }
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
          xtw.writeAttribute("name", customProperty.getName());
          xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
          xtw.writeCharacters(customProperty.getSimpleValue());
          xtw.writeEndElement();
          xtw.writeEndElement();
        }
        if (firstCustomProperty == false) {
          xtw.writeEndElement();
        }
      }

      // end ServiceTask element
      xtw.writeEndElement();

    } else if (object instanceof MailTask) {
      MailTask mailTask = (MailTask) object;
      // start MailTask element
      xtw.writeStartElement("serviceTask");
      xtw.writeAttribute("id", subProcessId + mailTask.getId());
      xtw.writeAttribute("name", mailTask.getName());
      xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "type", "mail");

      xtw.writeStartElement("extensionElements");

      if (mailTask.getTo() != null && mailTask.getTo().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "to");
        xtw.writeAttribute("expression", mailTask.getTo());
        xtw.writeEndElement();
      }
      if (mailTask.getFrom() != null && mailTask.getFrom().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "from");
        xtw.writeAttribute("expression", mailTask.getFrom());
        xtw.writeEndElement();
      }
      if (mailTask.getSubject() != null && mailTask.getSubject().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "subject");
        xtw.writeAttribute("expression", mailTask.getSubject());
        xtw.writeEndElement();
      }
      if (mailTask.getCc() != null && mailTask.getCc().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "cc");
        xtw.writeAttribute("expression", mailTask.getCc());
        xtw.writeEndElement();
      }
      if (mailTask.getBcc() != null && mailTask.getBcc().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "bcc");
        xtw.writeAttribute("expression", mailTask.getBcc());
        xtw.writeEndElement();
      }
      if (mailTask.getHtml() != null && mailTask.getHtml().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "html");
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeCData(mailTask.getHtml());
        xtw.writeEndElement();
        xtw.writeEndElement();
      }
      if (mailTask.getText() != null && mailTask.getText().length() > 0) {
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
        xtw.writeAttribute("name", "text");
        xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
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
      xtw.writeAttribute("id", subProcessId + manualTask.getId());
      xtw.writeAttribute("name", manualTask.getName());
      // end ManualTask element
      xtw.writeEndElement();
    
    } else if (object instanceof ReceiveTask) {
      ReceiveTask receiveTask = (ReceiveTask) object;
      // start ReceiveTask element
      xtw.writeStartElement("receiveTask");
      xtw.writeAttribute("id", subProcessId + receiveTask.getId());
      xtw.writeAttribute("name", receiveTask.getName());
      // end ReceiveTask element
      xtw.writeEndElement();

    } else if (object instanceof ParallelGateway) {
      ParallelGateway parallelGateway = (ParallelGateway) object;
      // start ParallelGateway element
      xtw.writeStartElement("parallelGateway");
      xtw.writeAttribute("id", subProcessId + parallelGateway.getId());
      xtw.writeAttribute("name", parallelGateway.getName());

      // end ParallelGateway element
      xtw.writeEndElement();

    } else if (object instanceof ExclusiveGateway) {
      ExclusiveGateway exclusiveGateway = (ExclusiveGateway) object;
      // start ExclusiveGateway element
      xtw.writeStartElement("exclusiveGateway");
      xtw.writeAttribute("id", subProcessId + exclusiveGateway.getId());
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

    for (EObject object : contents) {
      createXML(object, xtw, subProcessId + "_");
    }
  }
  
  private void createDIXML(Diagram diagram, XMLStreamWriter xtw, Process process) throws Exception {
    ILinkService linkService = Graphiti.getLinkService();
    EList<EObject> contents = diagram.eResource().getContents();
    EList<Shape> shapes = diagram.getChildren();
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNDiagram", BPMNDI_NAMESPACE);
    xtw.writeAttribute("id", "BPMNDiagram_" + process.getId());
    
    xtw.writeStartElement(BPMNDI_PREFIX, "BPMNPlane", BPMNDI_NAMESPACE);
    xtw.writeAttribute("bpmnElement", process.getId());
    xtw.writeAttribute("id", "BPMNPlane_" + process.getId());
    
    Map<String, GraphicsAlgorithm> flowNodeMap = new HashMap<String, GraphicsAlgorithm>();
    
    for (EObject bpmnObject : contents) {
      
      if(bpmnObject instanceof FlowNode) {
        FlowNode flowNode = (FlowNode) bpmnObject;
        xtw.writeStartElement(BPMNDI_PREFIX, "BPMNShape", BPMNDI_NAMESPACE);
        xtw.writeAttribute("bpmnElement", flowNode.getId());
        xtw.writeAttribute("id", "BPMNShape_" + flowNode.getId());
        
        xtw.writeStartElement(OMGDC_PREFIX, "Bounds", OMGDC_NAMESPACE);
        for (Shape shape : shapes) {
          ContainerShape containerShape = (ContainerShape) shape;
          EObject shapeBO = linkService.getBusinessObjectForLinkedPictogramElement(containerShape.getGraphicsAlgorithm().getPictogramElement());
          if(shapeBO instanceof FlowNode) {
            FlowNode shapeFlowNode = (FlowNode) shapeBO;
            if(shapeFlowNode.getId().equals(flowNode.getId())) {
              flowNodeMap.put(flowNode.getId(), containerShape.getGraphicsAlgorithm());
              xtw.writeAttribute("height", "" + containerShape.getGraphicsAlgorithm().getHeight());
              xtw.writeAttribute("width", "" + containerShape.getGraphicsAlgorithm().getWidth());
              xtw.writeAttribute("x", "" + containerShape.getGraphicsAlgorithm().getX());
              xtw.writeAttribute("y", "" + containerShape.getGraphicsAlgorithm().getY());
            }
          }
        }
        
        xtw.writeEndElement();
        xtw.writeEndElement();
        
      }
    }
    
    for (EObject bpmnObject : contents) {
      if(bpmnObject instanceof SequenceFlow) {
        SequenceFlow sequenceFlow = (SequenceFlow) bpmnObject;
        xtw.writeStartElement(BPMNDI_PREFIX, "BPMNEdge", BPMNDI_NAMESPACE);
        xtw.writeAttribute("bpmnElement", sequenceFlow.getId());
        xtw.writeAttribute("id", "BPMNEdge_" + sequenceFlow.getId());
        if(flowNodeMap.containsKey(sequenceFlow.getSourceRef().getId()) && flowNodeMap.containsKey(sequenceFlow.getTargetRef().getId())) {
          GraphicsAlgorithm sourceConnection = flowNodeMap.get(sequenceFlow.getSourceRef().getId());
          GraphicsAlgorithm targetConnection = flowNodeMap.get(sequenceFlow.getTargetRef().getId());
          xtw.writeStartElement(OMGDI_PREFIX, "waypoint", OMGDI_NAMESPACE);
          xtw.writeAttribute("x", "" + (sourceConnection.getX() + sourceConnection.getWidth()));
          xtw.writeAttribute("y", "" + (sourceConnection.getY() + (sourceConnection.getHeight() / 2)));
          xtw.writeEndElement();
          xtw.writeStartElement(OMGDI_PREFIX, "waypoint", OMGDI_NAMESPACE);
          xtw.writeAttribute("x", "" + targetConnection.getX());
          xtw.writeAttribute("y", "" + (targetConnection.getY() + (targetConnection.getHeight() / 2)));
          xtw.writeEndElement();
        }
        xtw.writeEndElement();
      }
    }
    
    xtw.writeEndElement();
    
    xtw.writeEndElement();
  }

  @Deprecated
  private void createErrorMessage(String message) {
    addProblemToDiagram(diagram, message, null);
  }
}
