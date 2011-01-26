package org.activiti.cdi.annotation;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

import javax.interceptor.InterceptorBinding;
	
/**
 * Completes a task for the currentUser if the method finished without exception
 */
@InterceptorBinding
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface ActivitiInterceptorAnnotation {
}
