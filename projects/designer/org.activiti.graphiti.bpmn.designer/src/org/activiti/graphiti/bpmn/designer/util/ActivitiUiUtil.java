package org.activiti.graphiti.bpmn.designer.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICustomContext;

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

}
