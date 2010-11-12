package org.activiti.designer.property.extension;

import org.activiti.designer.integration.servicetask.validator.FieldValidator;
import org.activiti.designer.integration.servicetask.validator.ValidationException;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class FieldValidatorListener implements FocusListener {

	private FieldValidator validator;
	private Control control;
	private Color originalColor;

	public FieldValidatorListener(Control control, Class<? extends FieldValidator> clazz) {
		this.control = control;
		originalColor = control.getBackground();
		try {
			this.validator = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// do nothing
	}

	@Override
	public void focusLost(FocusEvent e) {
		try {
			this.validator.validate(control);
			handleRemoveExceptionForControl();
		} catch (ValidationException e1) {
			handleAddExceptionForControl(e1);
		}
		control.getParent().layout(true, true);
	}

	// TODO: support more controls
	private void handleAddExceptionForControl(ValidationException e) {
		if (control instanceof Text) {
			Text text = (Text) control;
			text.setBackground(new Color(control.getDisplay(), new RGB(255, 220, 220)));
			text.setToolTipText(e.getMessage());
		}
	}

	private void handleRemoveExceptionForControl() {
		if (control instanceof Text) {
			Text text = (Text) control;
			text.setBackground(originalColor);
			text.setToolTipText(null);
		}
	}
}
