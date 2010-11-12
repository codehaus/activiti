package org.activiti.designer.property.extension;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.eclipse.swt.widgets.Control;

public class FieldWrapper {

	private final Control control;

	private final PropertyType propertyType;

	private Object marshaller;

	private Object unmarshaller;

	/**
	 * @param control
	 * @param propertyType
	 */
	public FieldWrapper(Control control, PropertyType propertyType) {
		super();
		this.control = control;
		this.propertyType = propertyType;
	}

	/**
	 * @return the marshaller
	 */
	public Object getMarshaller() {
		return marshaller;
	}

	/**
	 * @param marshaller
	 *            the marshaller to set
	 */
	public void setMarshaller(Object marshaller) {
		this.marshaller = marshaller;
	}

	/**
	 * @return the unmarshaller
	 */
	public Object getUnmarshaller() {
		return unmarshaller;
	}

	/**
	 * @param unmarshaller
	 *            the unmarshaller to set
	 */
	public void setUnmarshaller(Object unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	/**
	 * @return the control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * @return the propertyType
	 */
	public PropertyType getPropertyType() {
		return propertyType;
	}

}
