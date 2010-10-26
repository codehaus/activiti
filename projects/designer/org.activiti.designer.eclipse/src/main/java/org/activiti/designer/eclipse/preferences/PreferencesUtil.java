/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

import org.activiti.graphiti.bpmn.eclipse.common.ActivitiPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Utilities for working with preferences.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
public final class PreferencesUtil {

	/**
	 * 
	 */
	private PreferencesUtil() {
	}

	/**
	 * Gets a boolean preference's value from the preference store.
	 * 
	 * @param preference
	 *            the {@link Preferences} to get
	 * @return true if the preference is stored as true, otherwise false and
	 *         false if there is no preference applied
	 */
	public static final boolean getBooleanPreference(final Preferences preference) {
		final IPreferenceStore store = ActivitiPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(preference.getPreferenceId());
	}

}
