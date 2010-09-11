package org.activiti.graphiti.bpmn.designer.preferences;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class SetPreferences extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// This would be using instance scope
		// Preferences preferences = new InstanceScope()
		// .getNode("de.vogella.preferences.test");
		// This is using configuration scope
		Preferences preferences = new ConfigurationScope()
				.getNode("org.activiti.graphiti.bpmn.designer");
		// This would be using default n scope
		// Preferences preferences = new DefaultScope()
		// .getNode(Application.PLUGIN_ID);
		Preferences sub1 = preferences.node("note1");
		Preferences sub2 = preferences.node("node2");
		sub1.put("h1", "Hello");
		sub1.put("h2", "Hello again");
		sub2.put("h1", "Moin");

		try {
			// Forces the application to save the preferences
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
