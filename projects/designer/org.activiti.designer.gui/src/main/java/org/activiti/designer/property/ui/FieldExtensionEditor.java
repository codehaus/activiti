package org.activiti.designer.property.ui;

import java.util.Iterator;
import java.util.List;

import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class FieldExtensionEditor extends TableFieldEditor {
	
	private Composite parent;
	public PictogramElement pictogramElement;
	public IDiagramEditor diagramEditor;
	public Diagram diagram;
	
	public FieldExtensionEditor(String key, Composite parent) {
		
        super(key, "", new String[] {"Field name", "String value / Expression"},
        		new int[] {200, 400}, parent);
        this.parent = parent;
	}
	
	public void initialize(List<FieldExtension> fieldList) {
		if(fieldList == null || fieldList.size() == 0) return;
		removeTableItems();
		for (FieldExtension fieldExtension : fieldList) {
			addTableItem(fieldExtension.getFieldname(), fieldExtension.getExpression());
		}
	}

	@Override
	protected String createList(String[][] items) {
		System.out.println("createList");
		return null;
	}

	@Override
	protected String[][] parseString(String string) {
		System.out.println("parseString");
		return null;
	}

	@Override
	protected String[] getNewInputObject() {
		FieldExtensionDialog dialog = new FieldExtensionDialog(parent.getShell(), getItems());
		dialog.open();
		String fieldNameInput = dialog.fieldNameInput;
		String fieldValueInput = dialog.fieldValueInput;
		if(fieldNameInput != null && fieldNameInput.length() > 0 &&
				fieldValueInput != null && fieldValueInput.length() > 0) {
			
			return new String[] { fieldNameInput, fieldValueInput };
		} else {
			return null;
		}
	}
	
	@Override
	protected void selectionChanged() {
		super.selectionChanged();
		saveFieldExtensions();
	}
	
	private void saveFieldExtensions() {
		if (pictogramElement != null && getNumberOfItems() > 0) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
			if (bo instanceof ServiceTask) {
				TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
				ActivitiUiUtil.runModelChange(new Runnable() {
					public void run() {
						Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
						if (bo == null) {
							return;
						}
						ServiceTask serviceTask = (ServiceTask)  bo;
						for (TableItem item : getItems()) {
							String fieldName = item.getText(0);
							String fieldExpression = item.getText(1);
							if(fieldName != null && fieldName.length() > 0 &&
									fieldExpression != null && fieldExpression.length() > 0) {
								
								FieldExtension fieldExtension = fieldExtensionExists(serviceTask, fieldName);
								if(fieldExtension != null) {
									fieldExtension.setExpression(fieldExpression);
								} else {
									FieldExtension newFieldExtension = Bpmn2Factory.eINSTANCE.createFieldExtension();
									newFieldExtension.setFieldname(fieldName);
									newFieldExtension.setExpression(fieldExpression);
									diagram.eResource().getContents().add(newFieldExtension);
									serviceTask.getFieldExtensions().add(newFieldExtension);
								}
							}
						}
						removeFieldExtensionsNotInList(getItems(), serviceTask);
					}
				}, editingDomain, "Model Update");
			}

		}
	}
	
	private FieldExtension fieldExtensionExists(ServiceTask serviceTask, String fieldName) {
		if(serviceTask.getFieldExtensions() == null) return null;
		for(FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
			if(fieldName.equalsIgnoreCase(fieldExtension.getFieldname())) {
				return fieldExtension;
			}
		}
		return null;
	}
	
	private void removeFieldExtensionsNotInList(TableItem[] items, ServiceTask serviceTask) {
		Iterator<FieldExtension> entryIterator = serviceTask.getFieldExtensions().iterator();
		while(entryIterator.hasNext()) {
			FieldExtension fieldExtension = entryIterator.next();
			boolean found = false;
			for (TableItem item : items) {
				if(item.getText(0).equals(fieldExtension.getFieldname())) {
					found = true;
					break;
				}
			}
			if(found == false) {
				diagram.eResource().getContents().remove(fieldExtension);
				entryIterator.remove();
			}
		}
	}
	
}
