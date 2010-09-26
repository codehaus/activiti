package org.activiti.graphiti.bpmn.designer.features;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.activiti.graphiti.bpmn.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.graphiti.bpmn.eclipse.common.FileService;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractDrillDownFeature;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

public class ExpandCollapseSubProcessFeature extends AbstractDrillDownFeature {

	private String subprocessId = null;
	private String subprocessName = null;

	public ExpandCollapseSubProcessFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Expand/Collapse Sub Process"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Expand or collapse the sub process"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return ActivitiUiUtil.contextPertainsToBusinessObject(context, SubProcessImpl.class);
	}

	public void execute(ICustomContext context) {
		try {
			SubProcess subprocess = (SubProcess) ActivitiUiUtil.getBusinessObjectFromContext(context,
					SubProcessImpl.class);
			this.subprocessId = subprocess.getId();
			this.subprocessName = subprocess.getName();
			System.out.println(String.format("Expanding or Collapsing subprocess '%s'", subprocessId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.execute(context);
	}

	@Override
	protected Collection<Diagram> getLinkedDiagrams(PictogramElement pe) {
		return getDiagrams();
	}

	@Override
	protected Collection<Diagram> getDiagrams() {

		Collection<Diagram> result = new ArrayList<Diagram>();
		Resource resource = getDiagram().eResource();

		URI uri = resource.getURI();
		URI uriTrimmed = uri.trimFragment();

		if (uriTrimmed.isPlatformResource()) {

			String platformString = uriTrimmed.toPlatformString(true);

			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

			if (fileResource != null) {
				IProject project = fileResource.getProject();
				final String parentDiagramName = uriTrimmed.trimFileExtension().lastSegment();
				IFile targetFile = project.getFile(String.format(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER + "%s.%s"
						+ ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION, parentDiagramName, subprocessId));
				if (targetFile.exists()) {
					System.out
							.println(String
									.format("Sub process '%s' already exists for diagram '%s'. Opening diagram editor for existing sub process",
											subprocessId, parentDiagramName));
					result.add(getExistingDiagram(project, targetFile));
				} else {
					System.out
							.println(String
									.format("Sub process '%s' doesn't exist for diagram '%s'. Creating new diagram file and opening editor",
											subprocessId, parentDiagramName));
					result.add(getNewDiagram(project, targetFile));
				}
			}
		}

		return result;
	}

	private Diagram getNewDiagram(final IProject project, final IFile targetFile) {
		Diagram diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", subprocessName, true);
		URI uri = URI.createPlatformResourceURI(targetFile.getFullPath().toString(), true);
		TransactionalEditingDomain domain = FileService.createEmfFileForDiagram(uri, diagram);
		return diagram;
	}

	private Diagram getExistingDiagram(final IProject project, final IFile targetFile) {
		final ResourceSet rSet = new ResourceSetImpl();
		Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(targetFile, rSet);
		return diagram;
	}
}
