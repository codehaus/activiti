/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class ActivitiSavePreferencesPageInitializer extends AbstractPreferenceInitializer {

	public ActivitiSavePreferencesPageInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferencesUtil.getActivitiDesignerPreferenceStore();

		// BPMN 2 Marshaller
		store.setDefault(
				PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME),
				true);

		// Image Marshaller
		store.setDefault(
				PreferencesUtil.getExportMarshallerPreferenceId(ActivitiBPMNDiagramConstants.IMAGE_MARSHALLER_NAME),
				true);
	}
}
