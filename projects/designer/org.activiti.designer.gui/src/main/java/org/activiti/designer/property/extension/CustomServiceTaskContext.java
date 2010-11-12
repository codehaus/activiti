/**
 * 
 */
package org.activiti.designer.property.extension;

import java.io.InputStream;

import org.activiti.designer.integration.servicetask.CustomServiceTask;

/**
 * @author a139923
 * 
 */
public interface CustomServiceTaskContext {

	String getExtensionName();

	InputStream getSmallIconStream();

	InputStream getLargeIconStream();

	CustomServiceTask getServiceTask();

	String getImageKey();

}
