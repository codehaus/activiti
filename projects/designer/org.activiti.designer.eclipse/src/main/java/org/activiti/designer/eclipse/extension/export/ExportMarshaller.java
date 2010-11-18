/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;

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
	 * Gets a descriptive name for the format the marshaller produces.
	 * 
	 * @return the format's name
	 */
	String getFormatName();

	/**
	 * <p>
	 * Gets the pattern used to create filenames with when exporting with this
	 * marshaller. You can use a number of placeholder strings to fill certain
	 * parts of your result.
	 * 
	 * <p>
	 * This method must be implemented in such a way, that it cannot return a
	 * null value for the filename pattern. This would result in files with
	 * invalid file names and therefore results in the marshaller being ignored
	 * (i.e., skipped) when attempting to invoke the marshaller.
	 * 
	 * @see ExportMarshaller#PLACEHOLDER_ORIGINAL_FILENAME
	 * @see ExportMarshaller#PLACEHOLDER_ORIGINAL_FILE_EXTENSION
	 * @see ExportMarshaller#PLACEHOLDER_DATE_TIME
	 * 
	 * @return the filename pattern
	 * 
	 */
	String getFilenamePattern();

	/**
	 * Transforms content in the original diagram into this marshaller's own
	 * format.
	 * 
	 * <p>
	 * The {@link IProgressMonitor} provided should be used to indicate progress
	 * made in the marshaller and will be reported to the user.
	 * 
	 * @param originalDiagramStream
	 *            a stream for the content of the original diagram
	 * @param monitor
	 *            the monitor used to indicate progress of this marshaller
	 * 
	 * @return the transformed diagram as a byte[]
	 */
	byte[] marshallDiagram(InputStream originalDiagramStream, IProgressMonitor monitor);

}
