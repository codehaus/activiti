/**
 * 
 */
package org.activiti.designer.integration.servicetask;

/**
 * Abstract base class for implementing CustomServiceTasks.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
public abstract class AbstractCustomServiceTask implements CustomServiceTask {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
	 * contributeToPaletteDrawer()
	 */
	@Override
	public String contributeToPaletteDrawer() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Custom Service Task ").append(this.getClass().getSimpleName()).append("\n\tProvider class:\t")
				.append(this.getClass().getCanonicalName()).append("\n\tPalette drawer:\t")
				.append(contributeToPaletteDrawer());

		return builder.toString();
	}

}
