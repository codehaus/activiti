package org.activiti.cdi.jsf;

import javax.el.PropertyNotFoundException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

@RequestScoped
public class MessageProvider {

	
	public String getValue(String key) {

		final FacesContext c = FacesContext.getCurrentInstance();
		final Application application = c.getApplication();

		String result = null;
		try {
			result = application.evaluateExpressionGet(c, key, String.class);
		} catch (PropertyNotFoundException e2) {
			result = "???" + key + "??? not found";
		}
		return result;
	}
}