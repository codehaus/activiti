/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public interface ExportMarshaller {

	/**
	 * Placeholder parsed when creating a filename that is substituted with the
	 * filename of the original file.
	 */
	public static final String PLACEHOLDER_ORIGINAL_FILENAME = "$originalFile";
	/**
	 * Placeholder parsed when creating a filename that is substituted with the
	 * date and time at the moment of creation.
	 */
	public static final String PLACEHOLDER_DATE_TIME = "$dateTime";
	/**
	 * Placeholder parsed when creating a filename that is substituted with the
	 * file extension of the original file.
	 */
	public static final String PLACEHOLDER_ORIGINAL_FILE_EXTENSION = "$originalExtension";

	/**
	 * Gets a descriptive name for the marshaller.
	 * 
	 * @return the marshaller's name
	 */
	String getMarshallerName();

	/**
	 * Gets the pattern used to create filenames with when exporting with this
	 * marshaller. You can use a number of placeholder strings to fill certain
	 * parts of your result.
	 * 
	 * @see ExportMarshaller#PLACEHOLDER_ORIGINAL_FILENAME
	 * @see ExportMarshaller#PLACEHOLDER_ORIGINAL_FILE_EXTENSION
	 * @see ExportMarshaller#PLACEHOLDER_DATE_TIME
	 * 
	 * 
	 * @return the filename pattern
	 */
	String getFilenamePattern();

	void marshallDiagram(byte[] bytes);

}
