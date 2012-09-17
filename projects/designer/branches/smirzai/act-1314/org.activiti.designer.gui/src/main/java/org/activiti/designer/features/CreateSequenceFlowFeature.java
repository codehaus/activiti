package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateSequenceFlowFeature extends AbstractCreateBPMNConnectionFeature {

  public static final String FEATURE_ID_KEY = "flow";

  public CreateSequenceFlowFeature(IFeatureProvider fp) {
    // provide name and description for the UI, e.g. the palette
    super(fp, "SequenceFlow", "Create SequenceFlow"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public boolean canCreate(ICreateConnectionContext context) {
    FlowNode source = getFlowNode(context.getSourceAnchor());
    FlowNode target = getFlowNode(context.getTargetAnchor());
    if (source != null && target != null && source != target) {
      if (source instanceof StartEvent && target instanceof StartEvent) {
        return false;
      } else if (source instanceof EndEvent) {
        // prevent adding outgoing connections from EndEvents
        return false;
      }
      return true;
    }
    return false;
  }

  public boolean canStartConnection(ICreateConnectionContext context) {
    // return true if source anchor isn't undefined
    if (getFlowNode(context.getSourceAnchor()) != null) {
      return true;
    }
    return false;
  }

  public Connection create(ICreateConnectionContext context) {
    Connection newConnection = null;

    FlowNode source = getFlowNode(context.getSourceAnchor());
    FlowNode target = getFlowNode(context.getTargetAnchor());

    if (source != null && target != null) {
      // create new business object
      SequenceFlow sequenceFlow = createSequenceFlow(source, target, context);

      // add connection for business object
      AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
      addContext.setNewObject(sequenceFlow);
      newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
    }
    return newConnection;
  }

  /**
   * Returns the FlowNode belonging to the anchor, or null if not available.
   */
  private FlowNode getFlowNode(Anchor anchor) {
    if (anchor != null) {
      Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
      if (obj instanceof FlowNode) {
        return (FlowNode) obj;
      }
    }
    return null;
  }

  /**
   * Creates a SequenceFlow between two BaseElements.
   */
  private SequenceFlow createSequenceFlow(FlowNode source, FlowNode target, ICreateConnectionContext context) {
    SequenceFlow sequenceFlow = new SequenceFlow();

    sequenceFlow.setId(getNextId());
    sequenceFlow.setSourceRef(source);
    sequenceFlow.setTargetRef(target);

    if (PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS)) {
      sequenceFlow.setName(String.format("to %s", target.getName()));
    } else {
      sequenceFlow.setName("");
    }

    ContainerShape targetContainer = (ContainerShape) context.getSourcePictogramElement();
    ContainerShape parentContainer = targetContainer.getContainer();
    if (parentContainer instanceof Diagram) {
      ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess().getFlowElements().add(sequenceFlow);

    } else {
      Object parentObject = getBusinessObjectForPictogramElement(parentContainer);
      if (parentObject instanceof SubProcess) {
        ((SubProcess) parentObject).getFlowElements().add(sequenceFlow);

      } else if (parentObject instanceof Lane) {
        Lane lane = (Lane) parentObject;
        lane.getParentProcess().getFlowElements().add(sequenceFlow);
      }
    }

    source.getOutgoing().add(sequenceFlow);
    target.getIncoming().add(sequenceFlow);
    return sequenceFlow;
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_EREFERENCE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }

  @Override
  protected Class< ? extends BaseElement> getFeatureClass() {
    return new SequenceFlow().getClass();
  }

}
