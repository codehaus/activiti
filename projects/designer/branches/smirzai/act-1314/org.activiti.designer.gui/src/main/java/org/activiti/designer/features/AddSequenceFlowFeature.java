package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.FlowNode;
import org.activiti.designer.bpmn2.model.Gateway;
import org.activiti.designer.bpmn2.model.SequenceFlow;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.editor.GraphicInfo;
import org.activiti.designer.util.platform.OSEnum;
import org.activiti.designer.util.platform.OSUtil;
import org.activiti.designer.util.style.StyleUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class AddSequenceFlowFeature extends AbstractAddFeature {

	public AddSequenceFlowFeature(IFeatureProvider fp) {
		super(fp);
	}

	@SuppressWarnings("unchecked")
  public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		SequenceFlow addedSequenceFlow = (SequenceFlow) context.getNewObject();
		
		Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
		if(addConContext.getSourceAnchor() == null) {
      EList<Shape> shapeList = getDiagram().getChildren();
      for (Shape shape : shapeList) {
        FlowNode flowNode = (FlowNode) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
        if(flowNode == null || flowNode.getId() == null || addedSequenceFlow.getSourceRef() == null ||
                addedSequenceFlow.getTargetRef() == null) continue;
        if(flowNode.getId().equals(addedSequenceFlow.getSourceRef().getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
              sourceAnchor = anchor;
              break;
            }
          }
        }
        
        if(flowNode.getId().equals(addedSequenceFlow.getTargetRef().getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
              targetAnchor = anchor;
              break;
            }
          }
        }
      }
		} else {
		  sourceAnchor = addConContext.getSourceAnchor();
		  targetAnchor = addConContext.getTargetAnchor();
		}
		
		if(sourceAnchor == null || targetAnchor == null) {
		  return null;
		}

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		// CONNECTION WITH POLYLINE
		FreeFormConnection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(sourceAnchor);
		connection.setEnd(targetAnchor);
		sourceAnchor.getOutgoingConnections().add(connection);
		targetAnchor.getIncomingConnections().add(connection);

		GraphicsAlgorithm sourceGraphics = getPictogramElement(
				addedSequenceFlow.getSourceRef()).getGraphicsAlgorithm();
		GraphicsAlgorithm targetGraphics = getPictogramElement(
				addedSequenceFlow.getTargetRef()).getGraphicsAlgorithm();
		
		List<GraphicInfo> bendpointList = null;
		if(addConContext.getProperty("org.activiti.designer.bendpoints") != null) {
			bendpointList = (List<GraphicInfo>) addConContext.getProperty("org.activiti.designer.bendpoints");
		}
		
		if(bendpointList != null && bendpointList.size() >= 0) {
			for (GraphicInfo graphicInfo : bendpointList) {
				Point bendPoint = StylesFactory.eINSTANCE.createPoint();
				bendPoint.setX(graphicInfo.x);
        bendPoint.setY(graphicInfo.y);
				connection.getBendpoints().add(bendPoint);
      }
			
		} else {
			
		  Shape sourceShape = (Shape) getPictogramElement(addedSequenceFlow.getSourceRef());
		  ILocation sourceShapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(sourceShape);
		  int sourceX = sourceShapeLocation.getX();
      int sourceY = sourceShapeLocation.getY();
		  
      Shape targetShape = (Shape) getPictogramElement(addedSequenceFlow.getTargetRef());
      ILocation targetShapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(targetShape);
		  int targetX = targetShapeLocation.getX();
      int targetY = targetShapeLocation.getY();
      
			if (addedSequenceFlow.getSourceRef() instanceof Gateway && addedSequenceFlow.getTargetRef() instanceof Gateway == false) {
				if (((sourceGraphics.getY() + 10) < targetGraphics.getY()
						|| (sourceGraphics.getY() - 10) > targetGraphics.getY())  && 
						(sourceGraphics.getX() + (sourceGraphics.getWidth() / 2)) < targetGraphics.getX()) {
					
					boolean subProcessWithBendPoint = false;
					if(addedSequenceFlow.getTargetRef() instanceof SubProcess) {
						int middleSub = targetGraphics.getY() + (targetGraphics.getHeight() / 2);
						if((sourceGraphics.getY() + 20) < middleSub || (sourceGraphics.getY() - 20) > middleSub) {
							subProcessWithBendPoint = true;
						}
					}
					
					if(addedSequenceFlow.getTargetRef() instanceof SubProcess == false || subProcessWithBendPoint == true) {
						Point bendPoint = StylesFactory.eINSTANCE.createPoint();
						bendPoint.setX(sourceX + 20);
		        bendPoint.setY(targetY + (targetGraphics.getHeight() / 2));
						connection.getBendpoints().add(bendPoint);
					}
				}
			} else if (addedSequenceFlow.getTargetRef() instanceof Gateway) {
				if (((sourceGraphics.getY() + 10) < targetGraphics.getY()
						|| (sourceGraphics.getY() - 10) > targetGraphics.getY()) && 
						(sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX()) {
					
					boolean subProcessWithBendPoint = false;
					if(addedSequenceFlow.getSourceRef() instanceof SubProcess) {
						int middleSub = sourceGraphics.getY() + (sourceGraphics.getHeight() / 2);
						if((middleSub + 20) < targetGraphics.getY() || (middleSub - 20) > targetGraphics.getY()) {
							subProcessWithBendPoint = true;
						}
					}
					
					if(addedSequenceFlow.getSourceRef() instanceof SubProcess == false || subProcessWithBendPoint == true) {
						Point bendPoint = StylesFactory.eINSTANCE.createPoint();
						bendPoint.setX(targetX + 20);
		        bendPoint.setY(sourceY + (sourceGraphics.getHeight() / 2));
						connection.getBendpoints().add(bendPoint);
					}
				}
			} else if (addedSequenceFlow.getTargetRef() instanceof EndEvent) {
				int middleSource = sourceGraphics.getY() + (sourceGraphics.getHeight() / 2);
				int middleTarget = targetGraphics.getY() + (targetGraphics.getHeight() / 2);
				if (((middleSource + 10) < middleTarget && 
						(sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX()) ||
						
						((middleSource - 10) > middleTarget && 
						(sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX())) {
					
					Point bendPoint = StylesFactory.eINSTANCE.createPoint();
					bendPoint.setX(targetX + (targetGraphics.getWidth() / 2));
	        bendPoint.setY(sourceY + (sourceGraphics.getHeight() / 2));
					connection.getBendpoints().add(bendPoint);
				}
			}
		}

		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineStyle(LineStyle.SOLID);
		polyline.setForeground(Graphiti.getGaService().manageColor(getDiagram(), IColorConstant.BLACK));

		// create link and wire it
		link(connection, addedSequenceFlow);

		// add dynamic text decorator for the reference name
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		MultiText text = gaService.createDefaultMultiText(getDiagram(), textDecorator);
		text.setStyle(StyleUtil.getStyleForTask((getDiagram())));
		text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
    text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
    if (OSUtil.getOperatingSystem() == OSEnum.Mac) {
      text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 11));
    }
    
    if(addConContext.getProperty("org.activiti.designer.connectionlabel") != null) {
      GraphicInfo labelLocation = (GraphicInfo) addConContext.getProperty("org.activiti.designer.connectionlabel");
      gaService.setLocation(text, labelLocation.x, labelLocation.y);
    } else {
      gaService.setLocation(text, 10, 0);
    }
		
		if (StringUtils.isNotEmpty(addedSequenceFlow.getName())) {
		  TextUtil.setTextSize(addedSequenceFlow.getName(), text);
		}

		// set reference name in the text decorator
		text.setValue(addedSequenceFlow.getName());

		// add static graphical decorators (composition and navigable)
		ConnectionDecorator cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd);

		return connection;
	}

	public boolean canAdd(IAddContext context) {
		// return true if given business object is an EReference
		// note, that the context must be an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof SequenceFlow) {
			return true;
		}
		return false;
	}

	private Polygon createArrow(GraphicsAlgorithmContainer gaContainer) {
		int xy[] = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };
		int beforeAfter[] = new int[] { 3, 3, 0, 0, 3, 3, 3, 3 };
		Polygon polyline = Graphiti.getGaCreateService().createPolygon(gaContainer, xy, beforeAfter);
		polyline.setStyle(StyleUtil.getStyleForPolygon(getDiagram()));
		return polyline;
	}

	private PictogramElement getPictogramElement(Object businessObject) {
		return getFeatureProvider().getPictogramElementForBusinessObject(businessObject);
	}
}
