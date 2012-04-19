package org.activiti.designer.property;

import java.util.Arrays;
import java.util.List;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ErrorEventDefinition;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyBoundaryErrorSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private CCombo cancelActivityCombo;
	private List<String> cancelFormats = Arrays.asList("true", "false");
	private Text errorCodeText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		cancelActivityCombo = factory.createCCombo(composite, SWT.NONE);
		cancelActivityCombo.setItems((String[]) cancelFormats.toArray());
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		cancelActivityCombo.setLayoutData(data);
		cancelActivityCombo.addFocusListener(listener);
		
		createLabel(composite, "Cancel activity", cancelActivityCombo, factory); //$NON-NLS-1$

		errorCodeText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, -HSPACE);
		data.top = new FormAttachment(cancelActivityCombo, VSPACE);
		errorCodeText.setLayoutData(data);
		errorCodeText.addFocusListener(listener);

		createLabel(composite, "Error code", errorCodeText, factory); //$NON-NLS-1$
	}

	@Override
	public void refresh() {
		cancelActivityCombo.removeFocusListener(listener);
	  errorCodeText.removeFocusListener(listener);

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			if (bo == null)
				return;
			
			boolean cancelActivity = ((BoundaryEvent) bo).isCancelActivity();
			if(cancelActivity == false) {
				cancelActivityCombo.select(1);
			} else {
				cancelActivityCombo.select(0);
			}
			
			String errorCode = null;
			if(bo instanceof BoundaryEvent) {
  			BoundaryEvent boundaryEvent = (BoundaryEvent) bo;
  			if(boundaryEvent.getEventDefinitions().get(0) != null) {
  			  ErrorEventDefinition errorDefinition = (ErrorEventDefinition) boundaryEvent.getEventDefinitions().get(0);
          if(errorDefinition.getErrorCode() != null) {
            errorCode = errorDefinition.getErrorCode();
          }
  			}
			}
			errorCodeText.setText(errorCode == null ? "" : errorCode);
		}
		cancelActivityCombo.addFocusListener(listener);
		errorCodeText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof BoundaryEvent || bo instanceof EndEvent) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							
							String errorCode = errorCodeText.getText();
							if(bo instanceof BoundaryEvent) {
  							BoundaryEvent boundaryEvent = (BoundaryEvent) bo;
  							
  							int selection = cancelActivityCombo.getSelectionIndex();
  							if(selection == 0) {
  								boundaryEvent.setCancelActivity(true);
  							} else {
  								boundaryEvent.setCancelActivity(false);
  							}
  							
  						  ErrorEventDefinition errorDefinition = (ErrorEventDefinition) boundaryEvent.getEventDefinitions().get(0);
  						  errorDefinition.setErrorCode(errorCode);
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};
	
	private CLabel createLabel(Composite parent, String text, Control control, TabbedPropertySheetWidgetFactory factory) {
    CLabel label = factory.createCLabel(parent, text); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.TOP);
    label.setLayoutData(data);
    return label;
  }
}
