/**
 * 
 */
package org.activiti.designer.eclipse.preferences;

/**
 * Enumeration of preferences used in the designer.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public enum Preferences {

	ECLIPSE_ACTIVITI_JAR_LOCATION("org.activiti.designer.preferences.eclipse.activitiJarLocation"), EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS(
			"org.activiti.designer.preferences.editor.addLabelsToNewSequenceFlows"), EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS(
			"org.activiti.designer.preferences.editor.addDefaultContentToDiagrams"), SAVE_TO_FORMAT(
			"org.activiti.designer.preferences.save.saveToFormat");

	private String preferenceId;

	private Preferences(final String preferenceId) {
		this.preferenceId = preferenceId;
	}

	public String getPreferenceId() {
		return preferenceId;
	}

}
