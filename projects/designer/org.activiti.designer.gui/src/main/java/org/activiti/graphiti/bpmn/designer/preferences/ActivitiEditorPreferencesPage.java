package org.activiti.graphiti.bpmn.designer.preferences;

import org.activiti.graphiti.bpmn.designer.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiEditorPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiEditorPreferencesPage() {
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(
				ActivitiDesignerPreferences.EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS.getPreferenceId(),
				"&Automatically create a label when adding a new sequence flow", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Set preferences used while editing Activiti Diagrams");
		setTitle("Activiti Designer Editor Preferences");
	}
}
