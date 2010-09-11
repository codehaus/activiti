package org.activiti.graphiti.bpmn.designer.preferences;

import org.activiti.graphiti.bpmn.designer.Activator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ActivitiPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new DirectoryFieldEditor("PATH", "&Directory with Activiti jars:",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Set the location of your Activiti install base.");
	}
}

