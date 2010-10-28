/**
 * 
 */
package org.activiti.designer.integration.servicetask;

/**
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
public interface CustomServiceTask {

	public static final String MANIFEST_EXTENSION_PROVIDERS = "ActivitiDesigner-Extension-Providers";

	// TODO: shape usage, palette contribution, execution context, icon
	void execute() throws Exception;

	/**
	 * Determines to which palette drawer this service task contributes. Provide
	 * an empty string to prevent this service task from showing up in any
	 * palette drawer.
	 * 
	 * @return the name of the palette drawer
	 */
	String contributeToPaletteDrawer();

}
