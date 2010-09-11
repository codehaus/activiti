package org.activiti.graphiti.bpmn.designer.property;

import java.util.Arrays;
import java.util.List;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyUserTaskSection extends GFPropertySection implements ITabbedPropertyConstants {

	private CCombo performerTypeCombo;
	private List<String> performerTypes = Arrays.asList("Assignee", "Candidate users", "Candidate groups");
	private Text expressionText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		performerTypeCombo = factory.createCCombo(composite, SWT.NONE);
		performerTypeCombo.setItems((String[]) performerTypes.toArray());
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		performerTypeCombo.setLayoutData(data);
		performerTypeCombo.addFocusListener(listener);

		CLabel performerTypeLabel = factory.createCLabel(composite, "Performer type:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(performerTypeCombo, -HSPACE);
		data.top = new FormAttachment(performerTypeCombo, 0, SWT.CENTER);
		performerTypeLabel.setLayoutData(data);

		expressionText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(performerTypeCombo, VSPACE);
		expressionText.setLayoutData(data);
		expressionText.addFocusListener(listener);

		CLabel expressionLabel = factory.createCLabel(composite, "Expression:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(expressionText, -HSPACE);
		data.top = new FormAttachment(expressionText, 0, SWT.TOP);
		expressionLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {
		performerTypeCombo.removeFocusListener(listener);
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;
			
			int performerIndex = performerTypes.indexOf("Assignee");
			performerTypeCombo.select(performerIndex == -1 ? 0 : performerIndex);
			performerTypeCombo.addFocusListener(listener);
			expressionText.setText("");
			UserTask userTask = (UserTask) bo;
			if(userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
				expressionText.setText(userTask.getAssignee());
			}
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (bo instanceof UserTask) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					@SuppressWarnings("restriction")
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) {
								return;
							}
							String performerType = performerTypeCombo.getText();
							if (performerType != null) {
								if (bo instanceof UserTask) {

									// todo
								}
							}
							String expression = expressionText.getText();
							if (expression != null) {
								if (bo instanceof UserTask) {
									((UserTask) bo).setAssignee(expression);
								}
							}
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};

}
