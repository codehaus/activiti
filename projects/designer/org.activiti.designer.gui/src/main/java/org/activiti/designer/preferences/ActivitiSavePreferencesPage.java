package org.activiti.designer.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiSavePreferencesPage() {
		super(GRID);
	}

	public void createFieldEditors() {

		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				ActivitiPlugin.EXPORT_MARSHALLER_EXTENSIONPOINT_ID);
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof ExportMarshaller) {

					final ExportMarshaller exportMarshaller = (ExportMarshaller) o;

					addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(exportMarshaller),
							"&Automatically save to " + exportMarshaller.getFormatName()
									+ " format when saving diagrams", getFieldEditorParent()));
				}
			}
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Set preferences used while saving Activiti Diagrams");
		setTitle("Activiti Designer Save Preferences");
	}

}
