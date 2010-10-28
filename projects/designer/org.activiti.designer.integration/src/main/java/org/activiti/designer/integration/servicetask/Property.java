/**
 * 
 */
package org.activiti.designer.integration.servicetask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

	public abstract PropertyType type();

}
