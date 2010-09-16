package org.activiti.graphiti.bpmn.designer.property;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyServiceTaskSection extends GFPropertySection implements ITabbedPropertyConstants {

	private Text implementationNameText;
	private Button classSelectButton;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		implementationNameText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(70, 0);
		implementationNameText.setEnabled(false);
		implementationNameText.setLayoutData(data);

		classSelectButton = factory.createButton(composite, "Select class", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(implementationNameText, 0);
		data.right = new FormAttachment(78, 0);
		data.top = new FormAttachment(implementationNameText, -2, SWT.TOP);
		classSelectButton.setLayoutData(data);
		classSelectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				Shell shell = implementationNameText.getShell();
				try {
					SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell),
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false);

					if (dialog.open() == SelectionDialog.OK) {
						Object[] result = dialog.getResult();
						String className = ((IType) result[0]).getFullyQualifiedName();

						if (className != null) {
							implementationNameText.setText(className);
						}

						DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
						@SuppressWarnings("restriction")
						TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();

						ActivitiUiUtil.runModelChange(new Runnable() {
							public void run() {
								Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(
										getSelectedPictogramElement());
								if (bo == null) {
									return;
								}
								String implementationName = implementationNameText.getText();
								if (implementationName != null) {
									if (bo instanceof ServiceTask) {

										((ServiceTask) bo).setImplementation(implementationName);
									}
								}
							}
						}, editingDomain, "Model Update");
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		CLabel expressionLabel = factory.createCLabel(composite, "Service class:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(implementationNameText, -HSPACE);
		data.top = new FormAttachment(implementationNameText, 0, SWT.TOP);
		expressionLabel.setLayoutData(data);

	}

	@Override
	public void refresh() {

		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
			String implemenationName = ((ServiceTask) bo).getImplementation();
			implementationNameText.setText(implemenationName == null ? "" : implemenationName);
		}
	}
}