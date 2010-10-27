package org.activiti.designer.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.features.ExpandCollapseSubProcessFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
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
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

public class ActivitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public ActivitiToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature customFeature = new ExpandCollapseSubProcessFeature(getFeatureProvider());
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

		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof SubProcess) {

			CustomContext newContext = new CustomContext(new PictogramElement[] { pe });
			newContext.setInnerGraphicsAlgorithm(pe.getGraphicsAlgorithm());
			newContext.setInnerPictogramElement(pe);

			ExpandCollapseSubProcessFeature feature = new ExpandCollapseSubProcessFeature(getFeatureProvider());

			ContextButtonEntry drillDownButton = new ContextButtonEntry(feature, newContext);
			drillDownButton.setText("Expand subprocess"); //$NON-NLS-1$
			drillDownButton.setDescription("Drill down into this subprocess and edit its diagram"); //$NON-NLS-1$
			drillDownButton.setIconId(ActivitiImageProvider.IMG_ACTION_ZOOM);

			data.getDomainSpecificContextButtons().add(drillDownButton);
		}

		if (button.getDragAndDropFeatures().size() > 0) {
			data.getDomainSpecificContextButtons().add(button);
		}

		return data;
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {

		ContextMenuEntry subMenuExport = new ContextMenuEntry(new SaveBpmnModelFeature(getFeatureProvider()), context);
		subMenuExport.setText("Export to BPMN 2.0 XML"); //$NON-NLS-1$
		subMenuExport.setSubmenu(false);

		ContextMenuEntry subMenuExpandOrCollapse = new ContextMenuEntry(new ExpandCollapseSubProcessFeature(
				getFeatureProvider()), context);
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
			if (startEvent.getOutgoing().size() != 1) {
				IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
				imageRenderingDecorator.setMessage("A start event should have exactly one outgoing sequence flow"); //$NON-NLS-1$
				return new IDecorator[] { imageRenderingDecorator };
			}
		} else if (bo instanceof SubProcess) {
			SubProcess subProcess = (SubProcess) bo;

			if (!subProcessDiagramExists(subProcess)) {
				IDecorator imageRenderingDecorator = new ImageDecorator(
						IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
				imageRenderingDecorator.setMessage("This subprocess does not have a diagram model yet");//$NON-NLS-1$
				return new IDecorator[] { imageRenderingDecorator };
			}
		}
		return super.getDecorators(pe);
	}

	private boolean subProcessDiagramExists(SubProcess subProcess) {
		Resource resource = getDiagramTypeProvider().getDiagram().eResource();

		URI uri = resource.getURI();
		URI uriTrimmed = uri.trimFragment();

		if (uriTrimmed.isPlatformResource()) {

			String platformString = uriTrimmed.toPlatformString(true);

			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

			if (fileResource != null) {
				IProject project = fileResource.getProject();
				final String parentDiagramName = uriTrimmed.trimFileExtension().lastSegment();

				IFile file = project.getFile(String.format(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER + "%s.%s"
						+ ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION, parentDiagramName, subProcess.getId()));

				Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(file, new ResourceSetImpl());

				return diagram != null;
			}
		}
		// Safe default assumption
		return true;
	}
}
