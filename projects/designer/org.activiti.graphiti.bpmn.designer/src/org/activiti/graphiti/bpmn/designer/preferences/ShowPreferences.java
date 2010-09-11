package org.activiti.graphiti.bpmn.designer.preferences;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.Preferences;

public class ShowPreferences extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindowChecked(event)
				.getShell();
		Preferences preferences = new ConfigurationScope()
				.getNode("org.activiti.graphiti.bpmn.designer");
		Preferences sub1 = preferences.node("note1");
		Preferences sub2 = preferences.node("node2");
		MessageDialog.openInformation(shell, "Info", sub1.get("h1", "default"));
		MessageDialog.openInformation(shell, "Info", sub1.get("h2", "default"));
		MessageDialog.openInformation(shell, "Info", sub2.get("h1", "default"));
		return null;
	}
}

