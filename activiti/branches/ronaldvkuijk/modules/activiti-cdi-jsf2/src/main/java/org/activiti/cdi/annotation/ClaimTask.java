package org.activiti.cdi.annotation;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.*;
	
/**
 * Claims a task for the currentUser if the method finished without exception
 */
@ActivitiInterceptorAnnotation
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
@InterceptorBinding
public @interface ClaimTask {
    String messageTitle() default "#{msgs['task.claim.title']}";
    String successMessage() default "#{msgs['task.claim.success']}";  
    String errorMessage() default "#{msgs['task.claim.error']}";
}
