package org.activiti.graphiti.bpmn.designer.features;

import org.activiti.graphiti.bpmn.designer.util.StyleUtil;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddGatewayFeature extends AbstractAddShapeFeature {

	// the additional size of the invisible rectangle at the right border
	// (this also equals the half width of the anchor to paint there)
	public static final int INVISIBLE_SQUARE_MIDDLE = 6;

	public AddGatewayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		final Gateway addedGateway = (Gateway) context.getNewObject();
		final Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH CIRCLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		final int width = context.getWidth() <= 0 ? 60 : context.getWidth();
		final int height = context.getHeight() <= 0 ? 60 : context.getHeight();

		final IGaService gaService = Graphiti.getGaService();

		Polygon polygon;
		{
			int xy[] = new int[] { 0, 30, 30, 0, 60, 30, 30, 60, 0, 30 };
			final Polygon invisiblePolygon = gaService.createPolygon(containerShape, xy);
			invisiblePolygon.setFilled(false);
			invisiblePolygon.setLineVisible(false);
			gaService.setLocationAndSize(invisiblePolygon, context.getX(), context.getY(), width, height + INVISIBLE_SQUARE_MIDDLE);

			// create and set visible circle inside invisible circle
			polygon = gaService.createPolygon(invisiblePolygon, xy);
			polygon.setParentGraphicsAlgorithm(invisiblePolygon);
			polygon.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
			gaService.setLocationAndSize(polygon, 0, 0, width, height);

			// if addedClass has no resource we add it to the resource of the
			// diagram. In a real scenario the business model would have its own
			// resource
			if (addedGateway.eResource() == null) {
				getDiagram().eResource().getContents().add(addedGateway);
			}

			// create link and wire it
			link(containerShape, addedGateway);
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			
			final Polyline polyline = gaService.createPolyline(shape, new int[] { 10, 28, width - 10, 28 });
			polyline.setLineWidth(8);
			polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		}
		
		{
			final Shape shape = peCreateService.createShape(containerShape, false);
			
			final Polyline polyline = gaService.createPolyline(shape, new int[] { 27, 10, 27, height - 10 });
			polyline.setLineWidth(8);
			polyline.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		}

		{
			// add a chopbox anchor to the shape
			peCreateService.createChopboxAnchor(containerShape);
			// create an additional box relative anchor at middle-right
			final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
			boxAnchor.setRelativeWidth(0.51);
			boxAnchor.setRelativeHeight(0.10); // Use golden section
			// anchor references visible rectangle instead of invisible
			// rectangle
			boxAnchor.setReferencedGraphicsAlgorithm(polygon);
			// assign a graphics algorithm for the box relative anchor
			final Ellipse ellipse = gaService.createEllipse(boxAnchor);
			ellipse.setFilled(true);
			final int w = INVISIBLE_SQUARE_MIDDLE;
			gaService.setLocationAndSize(ellipse, -w, -w, 2 * w, 2 * w);
			ellipse.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		}

		{
			// add a another chopbox anchor to the shape
			peCreateService.createChopboxAnchor(containerShape);
			// create an additional box relative anchor at middle-right
			final BoxRelativeAnchor boxAnchor2 = peCreateService.createBoxRelativeAnchor(containerShape);
			boxAnchor2.setRelativeWidth(0.51);
			boxAnchor2.setRelativeHeight(0.93); // Use golden section
			// anchor references visible rectangle instead of invisible
			// rectangle
			boxAnchor2.setReferencedGraphicsAlgorithm(polygon);
			// assign a graphics algorithm for the box relative anchor
			final Ellipse ellipse2 = gaService.createEllipse(boxAnchor2);
			ellipse2.setFilled(true);
			final int w2 = INVISIBLE_SQUARE_MIDDLE;
			gaService.setLocationAndSize(ellipse2, -w2, -w2, 2 * w2, 2 * w2);
			ellipse2.setStyle(StyleUtil.getStyleForEClass(getDiagram()));
		}

		// call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof Gateway) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

}
