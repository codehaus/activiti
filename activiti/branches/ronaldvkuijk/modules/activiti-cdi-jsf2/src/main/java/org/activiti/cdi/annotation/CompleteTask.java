package org.activiti.cdi.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;
	
/**
 * Completes a task for the currentUser if the method finished without exception
 */
@ActivitiInterceptorAnnotation
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
@InterceptorBinding
public @interface CompleteTask {
}
