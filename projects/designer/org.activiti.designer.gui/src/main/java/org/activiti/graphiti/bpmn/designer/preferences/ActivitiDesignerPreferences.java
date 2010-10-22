/**
 * 
 */
package org.activiti.graphiti.bpmn.designer.preferences;

/**
 * Enumeration of preferences used in the designer.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public enum ActivitiDesignerPreferences {

	ECLIPSE_ACTIVITI_JAR_LOCATION("org.activiti.designer.preferences.eclipse.activitiJarLocation"), EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS(
			"org.activiti.designer.preferences.editor.addLabelsToNewSequenceFlows");

	private String preferenceId;

	private ActivitiDesignerPreferences(final String preferenceId) {
		this.preferenceId = preferenceId;
	}

	public String getPreferenceId() {
		return preferenceId;
	}

}
