package org.activiti.cdi.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class JSFUtility {
	
	public static void info(String message) {
		info(message, message);
	}

	public static void info(String shortMessage, String message) {
		growl(FacesMessage.SEVERITY_INFO, shortMessage, message);
	}

	public static void warn(String message) {
		growl(FacesMessage.SEVERITY_WARN, message, message);
	}

	public static void error(String message) {
		error(message, message);
	}

	public static void error(String shortMessage, String message) {
		growl(FacesMessage.SEVERITY_ERROR, shortMessage, message);
	}

	private static void growl(Severity severity, String shortMessage, String message) {
		if (shortMessage == null) {shortMessage = "???";}
		if (message == null) {message = "???";}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(severity, shortMessage, message));
	}

}
