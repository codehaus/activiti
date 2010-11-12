/**
 * 
 */
package org.activiti.designer.integration.servicetask;

/**
 * Interface for customizations of the default ServiceTask from BPMN.
 * 
 * This interface should not be implemented directly by clients - an abstract
 * base class that implements this interface should be extended instead
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
public interface CustomServiceTask {

	public static final String MANIFEST_EXTENSION_NAME = "ActivitiDesigner-Extension-Name";
	public static final String MANIFEST_EXTENSION_PROVIDERS = "ActivitiDesigner-Extension-Providers";

	// TODO: shape usage, palette contribution, execution context, icon
	void execute() throws Exception;

	/**
	 * Gets the identifier for this custom service task. The qualified
	 * identifier uniquely identifies this service task.
	 * 
	 * @return the identifier
	 */
	String getId();

	/**
	 * Gets a descriptive name for the service task. This name is presented to
	 * the user in the designer.
	 * 
	 * @return the service task's name
	 */
	String getName();

	/**
	 * Determines to which palette drawer this service task contributes. Provide
	 * an empty string to prevent this service task from showing up in any
	 * palette drawer.
	 * 
	 * @return the name of the palette drawer
	 */
	String contributeToPaletteDrawer();

	/**
	 * Gets the path to the icon image file used for small icon display. Returns
	 * null if this {@link CustomServiceTask} has no image of its own.
	 * 
	 * @return the path to the icon file or null if there is none
	 */
	String getSmallIconPath();

}
