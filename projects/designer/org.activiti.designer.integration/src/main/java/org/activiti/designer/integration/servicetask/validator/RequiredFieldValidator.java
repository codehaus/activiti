package org.activiti.designer.integration.servicetask.validator;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * {@link FieldValidator} that validates required fields. Supports {@link Text}
 * controls.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class RequiredFieldValidator implements FieldValidator {

  public RequiredFieldValidator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.FieldValidator#validate
   * (java.lang.Object)
   */
  @Override
  public void validate(Control control) throws ValidationException {

    if (control.isDisposed()) {
      return;
    }

    String fieldValue = null;
    if (control instanceof Text) {
      fieldValue = ((Text) control).getText();
    } else if (control instanceof CCombo) {
      fieldValue = ((CCombo) control).getText();
    }

    if (fieldValue != null) {

      if ("".equals(fieldValue)) {
        throw new ValidationException("This field is required");
      }

    } else {
      throw new ValidationException("The value provided must be of type string from a supported control");
    }
  }

}