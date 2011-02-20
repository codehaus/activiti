package org.activiti.designer.features;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class DeleteSequenceFlowFeature extends AbstractCustomFeature {

  public DeleteSequenceFlowFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getName() {
    return "Delete sequence flow"; //$NON-NLS-1$
  }

  @Override
  public String getDescription() {
    return "Delete sequence flow"; //$NON-NLS-1$
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    if(context.getPictogramElements() == null) return false;
    for (PictogramElement pictogramElement : context.getPictogramElements()) {
      if(pictogramElement.getLink() == null) continue;
      Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == false) {
        return false;
      }
    }
    return true;
  }

  public void execute(ICustomContext context) {
    if(context.getPictogramElements() == null) return;
    for (final PictogramElement pictogramElement : context.getPictogramElements()) {
      if(pictogramElement.getLink() == null) continue;
      final Object boObject = getBusinessObjectForPictogramElement(pictogramElement);
      if(boObject instanceof SequenceFlow == true) {
        final SequenceFlow sequenceFlow = (SequenceFlow) boObject;
        for(Shape shape : getDiagram().getChildren()) {
          FlowNode flowNode = (FlowNode) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
          if(flowNode.getId().equals(sequenceFlow.getSourceRef().getId())) {
            EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
            for (Anchor anchor : anchorList) {
              if(anchor instanceof ChopboxAnchor) {
                anchor.getOutgoingConnections().remove(0);
              }
            }
          }
          if(flowNode.getId().equals(sequenceFlow.getTargetRef().getId())) {
            EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
            for (Anchor anchor : anchorList) {
              if(anchor instanceof ChopboxAnchor) {
                anchor.getIncomingConnections().remove(0);
              }
            }
          }
        }
        
        getDiagram().getPictogramLinks().remove(pictogramElement.getLink());
        getDiagram().getConnections().remove(pictogramElement);
        if(sequenceFlow.getSourceRef() != null) {
          sequenceFlow.getSourceRef().getOutgoing().remove(sequenceFlow);
        }
        if(sequenceFlow.getTargetRef() != null) {
          sequenceFlow.getTargetRef().getIncoming().remove(sequenceFlow);
        }
        getDiagram().eResource().getContents().remove(sequenceFlow);
      }
    }
  }
}
