package org.activiti.graphiti.bpmn.designer.util;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public class ActivitiUiUtil {
	
	public static void runModelChange(final Runnable runnable,
            final TransactionalEditingDomain editingDomain, final String label) {
        editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain, label) {
            protected void doExecute() {
                runnable.run();
            }
        });
    }

}
