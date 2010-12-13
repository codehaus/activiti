/**
 * 
 */
package org.activiti.designer.integration.servicetask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.validator.FieldValidator;

/**
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

  abstract PropertyType type();

  String displayName() default "";

  Class< ? extends FieldValidator> fieldValidator() default FieldValidator.class;

  boolean required() default false;

}
