package org.activiti.graphiti.bpmn.designer.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsFactory;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.IGaService;

public class ActivitiUiUtil {
	
	public static void runModelChange(final Runnable runnable,
            final TransactionalEditingDomain editingDomain, final String label) {
		
        editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain, label) {
            protected void doExecute() {
                runnable.run();
            }
        });
    }
	
	@SuppressWarnings("rawtypes")
	public static boolean contextPertainsToBusinessObject(final ICustomContext context,  final Class businessClass) {
		boolean result = false;
		EList<EObject> businessObjects = context.getInnerPictogramElement().getLink().getBusinessObjects();
		for (final EObject eobj: businessObjects) {
			if (businessClass.equals(eobj.getClass())) {
				result = true;
				break;
			}
		}
		return result;
    }

	public static Ellipse createInvisibleEllipse(GraphicsAlgorithmContainer gaContainer, IGaService gaService) {
		Ellipse ret = AlgorithmsFactory.eINSTANCE.createEllipse();
		ret.setX(0);
		ret.setY(0);
		ret.setWidth(0);
		ret.setHeight(0);
		ret.setFilled(false);
		ret.setLineVisible(false);
		if (gaContainer instanceof PictogramElement) {
			PictogramElement pe = (PictogramElement) gaContainer;
			pe.setGraphicsAlgorithm(ret);
		} else if (gaContainer instanceof GraphicsAlgorithm) {
			GraphicsAlgorithm parentGa = (GraphicsAlgorithm) gaContainer;
			parentGa.getGraphicsAlgorithmChildren().add(ret);
		}
		return ret;
	}
	
	public static org.eclipse.bpmn2.Process getProcessObject(Diagram diagram) {
		for(EObject eObject: diagram.eResource().getContents()) {
			if(eObject instanceof org.eclipse.bpmn2.Process) {
				return (org.eclipse.bpmn2.Process) eObject;
			}
		}
		return null;
	}
}
