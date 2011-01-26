package org.activiti.cdi.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.activiti.cdi.Activiti;
import org.activiti.cdi.ProcessBean;
import org.activiti.cdi.TaskBean;
import org.activiti.cdi.jsf.JSFUtility;
import org.activiti.cdi.jsf.MessageProvider;

import org.activiti.engine.identity.User;

@ActivitiInterceptorAnnotation
@Interceptor
public class ActivitiInterceptor implements Serializable {

	private static final long serialVersionUID = 9077871878937632746L;

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Inject
	Activiti activiti;

	@Inject
	// @LoggedIn
	User currentUser;

	@Inject
	TaskBean taskBean;

	@Inject
	ProcessBean processBean;

	@Inject
	MessageProvider messageProvider;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocationContext)
			throws Exception {

		Method method = invocationContext.getMethod();
		Object returnVal = null;

		activiti.getIdentityService().setAuthenticatedUserId(
				currentUser.getId());

		if (method.isAnnotationPresent(CompleteTask.class)) {

			// TODO check for taskId being null or ""
			log.fine("Calling annotated method..." + method);
			returnVal = invocationContext.proceed();
			taskBean.complete();

		} else if (method.isAnnotationPresent(ClaimTask.class)) {

			ClaimTask claimTask = method.getAnnotation(ClaimTask.class);
			// TODO check for taskId being null or ""
			log.fine("Calling annotated method..." + method);

			try {
				returnVal = invocationContext.proceed();
				taskBean.claim();
				info(claimTask.messageTitle(), claimTask.successMessage());
			} catch (Exception e) {
				log.log(Level.SEVERE, claimTask.messageTitle(), e);
				error(claimTask.messageTitle(), claimTask.errorMessage());
			}

		} else if (method.isAnnotationPresent(StartProcess.class)) {

			log.fine("Calling annotated method..." + method);
			StartProcess startProcess = method
					.getAnnotation(StartProcess.class);

			try {

				returnVal = invocationContext.proceed();
				processBean.start(startProcess.procName());

			} catch (Exception e) {
				log.log(Level.SEVERE, startProcess.messageTitle(), e);
				error(startProcess.messageTitle(), startProcess.errorMessage());
			}

		} else {

			log.info("Intercepting non annotated method..."
					+ invocationContext.getMethod());
			returnVal = invocationContext.proceed();

		}

		return returnVal;
	}

	private void info(String title, String message) {
		JSFUtility.info(messageProvider.getValue(title),
				messageProvider.getValue(message));
	}

	private void error(String title, String message) {
		JSFUtility.error(messageProvider.getValue(title),
				messageProvider.getValue(message));
	}
}
