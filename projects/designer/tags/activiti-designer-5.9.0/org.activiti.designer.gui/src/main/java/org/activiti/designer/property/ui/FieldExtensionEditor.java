package org.activiti.designer.property.ui;

import java.util.Iterator;
import java.util.List;

import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
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
	  removeTableItems();
		if(fieldList == null || fieldList.size() == 0) return;
		for (FieldExtension fieldExtension : fieldList) {
			addTableItem(fieldExtension.getFieldName(), fieldExtension.getExpression());
		}
	}
	
	public void initializeModel(List<FieldExtension> fieldList) {
	  removeTableItems();
    if(fieldList == null || fieldList.size() == 0) return;
    for (FieldExtension fieldExtension : fieldList) {
      addTableItem(fieldExtension.getFieldName(), fieldExtension.getExpression());
    }
	}

	@Override
	protected String createList(String[][] items) {
		return null;
	}

	@Override
	protected String[][] parseString(String string) {
		return null;
	}
	
	protected void addTableItem(String name, String value) {
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, name);
      tableItem.setText(1, value);
    }
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
  protected String[] getChangedInputObject(TableItem tableItem) {
    FieldExtensionDialog dialog = new FieldExtensionDialog(parent.getShell(), getItems(), 
            tableItem.getText(0), tableItem.getText(1));
    dialog.open();
    String fieldNameInput = dialog.fieldNameInput;
    String fieldValueInput = dialog.fieldValueInput;
    if(fieldNameInput != null && fieldNameInput.length() > 0 &&
        fieldValueInput != null && fieldValueInput.length() > 0) {
      
      if(tableItem.getText(0).equals(fieldNameInput) && tableItem.getText(1).equals(fieldValueInput)) {
        return null;
      }
      
      return new String[] { fieldNameInput, fieldValueInput };
    } else {
      return null;
    }
  }
	
	@Override
  protected void removedItem(int index) {
	  // TODO Auto-generated method stub 
  }
	
	@Override
	protected void selectionChanged() {
		super.selectionChanged();
		saveFieldExtensions();
	}
	
	private void saveFieldExtensions() {
		if (pictogramElement != null) {
			final Object bo = ModelHandler.getModel(EcoreUtil.getURI(diagram)).getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof ServiceTask) {
				TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
				ActivitiUiUtil.runModelChange(new Runnable() {
					public void run() {
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
									FieldExtension newFieldExtension = new FieldExtension();
									newFieldExtension.setFieldName(fieldName);
									newFieldExtension.setExpression(fieldExpression);
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
			if(fieldName.equalsIgnoreCase(fieldExtension.getFieldName())) {
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
				if(item.getText(0).equals(fieldExtension.getFieldName())) {
					found = true;
					break;
				}
			}
			if(found == false) {
				entryIterator.remove();
			}
		}
	}
	
}
