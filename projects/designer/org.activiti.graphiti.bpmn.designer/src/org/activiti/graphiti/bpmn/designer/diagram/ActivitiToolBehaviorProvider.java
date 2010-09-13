package org.activiti.graphiti.bpmn.designer.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.graphiti.bpmn.designer.ActivitiImageProvider;
import org.activiti.graphiti.bpmn.designer.features.ExpandCollapseSubProcessFeature;
import org.activiti.graphiti.bpmn.designer.features.SaveBpmnModelFeature;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;

public class ActivitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public ActivitiToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}
	
	@Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
        ICustomFeature customFeature =
            new ExpandCollapseSubProcessFeature(getFeatureProvider());
        if (customFeature.canExecute(context)) {
            return customFeature;
        }
        return super.getDoubleClickFeature(context);
    }

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();

		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE | CONTEXT_BUTTON_UPDATE);

		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourcePictogramElement(pe);
		Anchor anchor = null;
		if (pe instanceof Anchor) {
			anchor = (Anchor) pe;
		} else if (pe instanceof AnchorContainer) {
			anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
		}
		ccc.setSourceAnchor(anchor);

		ContextButtonEntry button = new ContextButtonEntry(null, context);
		button.setText("Create connection"); //$NON-NLS-1$
		button.setIconId(ActivitiImageProvider.IMG_EREFERENCE);
		ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
		for (ICreateConnectionFeature feature : features) {
			if (feature.isAvailable(ccc) && feature.canStartConnection(ccc))
				button.addDragAndDropFeature(feature);
		}

		if (button.getDragAndDropFeatures().size() > 0) {
			data.getDomainSpecificContextButtons().add(button);
		}

		return data;
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		
		ContextMenuEntry subMenuExport = new ContextMenuEntry(
				new SaveBpmnModelFeature(getFeatureProvider()), context);
		subMenuExport.setText("Export to BPMN 2.0 XML"); //$NON-NLS-1$
		subMenuExport.setSubmenu(false);
		
		ContextMenuEntry subMenuExpandOrCollapse = new ContextMenuEntry(
				new ExpandCollapseSubProcessFeature(getFeatureProvider()), context);
		subMenuExpandOrCollapse.setText("Expand/Collapse Subprocess"); //$NON-NLS-1$
		subMenuExpandOrCollapse.setSubmenu(false);

		IContextMenuEntry ret[] = new IContextMenuEntry[] { subMenuExport };
		return ret;
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

		// add compartments from super class
		IPaletteCompartmentEntry[] superCompartments = super.getPalette();
		for (int i = 0; i < superCompartments.length; i++)
			ret.add(superCompartments[i]);

		return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
	}
	
	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		IFeatureProvider featureProvider = getFeatureProvider();
		Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof StartEvent) {
			StartEvent startEvent = (StartEvent) bo;
			if(startEvent.getOutgoing().size() != 1) {
				IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
				imageRenderingDecorator.setMessage("A start event should have exactly one outgoing sequence flow"); //$NON-NLS-1$
				return new IDecorator[] { imageRenderingDecorator };
			}
		}

		return super.getDecorators(pe);
	}
}
