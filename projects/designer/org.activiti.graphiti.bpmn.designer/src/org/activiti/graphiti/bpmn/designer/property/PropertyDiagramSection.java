/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.graphiti.bpmn.designer.property;

import org.activiti.graphiti.bpmn.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
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

public class PropertyDiagramSection extends GFPropertySection implements ITabbedPropertyConstants {

	private Text idText;
	private Text nameText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		idText = factory.createText(composite, "", SWT.READ_ONLY); //$NON-NLS-1$
		idText.setEnabled(false);
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		idText.setLayoutData(data);

		CLabel idLabel = factory.createCLabel(composite, "Id:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(idText, -HSPACE);
		data.top = new FormAttachment(idText, 0, SWT.CENTER);
		idLabel.setLayoutData(data);

		nameText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 120);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(idText, VSPACE);
		nameText.setLayoutData(data);
		nameText.addFocusListener(listener);

		CLabel valueLabel = factory.createCLabel(composite, "Name:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(nameText, -HSPACE);
		data.top = new FormAttachment(nameText, 0, SWT.CENTER);
		valueLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		nameText.removeFocusListener(listener);
		org.eclipse.bpmn2.Process process = ActivitiUiUtil.getProcessObject(getDiagram());
		if(process == null) {
			DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
			@SuppressWarnings("restriction")
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					org.eclipse.bpmn2.Process process = Bpmn2Factory.eINSTANCE.createProcess();
					process.setId("helloworld");
					process.setName("helloworld");
					
					getDiagram().eResource().getContents().add(process);
					idText.setText(process.getId());
					nameText.setText(process.getName());
				}
			}, editingDomain, "Model Update");
		} else {
			idText.setText(process.getId());
			nameText.setText(process.getName());
			nameText.addFocusListener(listener);
		}
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
			@SuppressWarnings("restriction")
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					org.eclipse.bpmn2.Process process = ActivitiUiUtil.getProcessObject(getDiagram());
					if (process == null) {
						return;
					}
					String name = nameText.getText();
					if (name != null) {
						process.setName(name);
						idText.setText(name);
						process.setId(name);
					}
				}
			}, editingDomain, "Model Update");
		}
	};

}
