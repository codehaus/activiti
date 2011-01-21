package org.activiti.designer.property.ui;

import java.util.Iterator;
import java.util.List;

import org.activiti.designer.model.FieldExtensionModel;
import org.activiti.designer.util.ActivitiUiUtil;
import org.activiti.designer.util.BpmnBOUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExecutionListener;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class ExecutionListenerEditor extends TableFieldEditor {
	
	private Composite parent;
	public PictogramElement pictogramElement;
	public IDiagramEditor diagramEditor;
	public Diagram diagram;
	
	public ExecutionListenerEditor(String key, Composite parent) {
		
        super(key, "", new String[] {"Listener implementation", "Type", "Event", "Fields"},
        		new int[] {200, 150, 100, 300}, parent);
        this.parent = parent;
	}
	
	public void initialize(List<ExecutionListener> listenerList) {
	  removeTableItems();
		if(listenerList == null || listenerList.size() == 0) return;
		for (ExecutionListener executionListener : listenerList) {
			addTableItem(executionListener.getImplementation(), executionListener.getImplementationType(),
			        executionListener.getEvent(), executionListener.getFieldExtensions());
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
	
	protected void addTableItem(String implementation, String implementationType, 
	        String event, List<FieldExtension> fieldExtensions) {
	  
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, implementation);
      tableItem.setText(1, implementationType);
      tableItem.setText(2, event);
      String fieldString = "";
      if(fieldExtensions != null) {
        for (FieldExtension fieldExtension : fieldExtensions) {
          if(fieldString.length() > 0) {
            fieldString += ", ";
          }
          fieldString += fieldExtension.getFieldname() + ":" + fieldExtension.getExpression();
        }
      }
      tableItem.setText(3, fieldString);
    }
  }

	@Override
	protected String[] getNewInputObject() {
		ExecutionListenerDialog dialog = new ExecutionListenerDialog(parent.getShell(), getItems());
		dialog.open();
		if(dialog.eventName != null && dialog.eventName.length() > 0 &&
		        dialog.implementation != null && dialog.implementation.length() > 0 &&
		        dialog.implementationType != null && dialog.implementationType.length() > 0) {
			
			return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
		} else {
			return null;
		}
	}
	
	@Override
  protected String[] getChangedInputObject(TableItem item) {
    ExecutionListenerDialog dialog = new ExecutionListenerDialog(parent.getShell(), getItems(), 
            item.getText(1), item.getText(0), item.getText(2), item.getText(3));
    dialog.open();
    if(dialog.eventName != null && dialog.eventName.length() > 0 &&
            dialog.implementation != null && dialog.implementation.length() > 0 &&
            dialog.implementationType != null && dialog.implementationType.length() > 0) {
      
      return new String[] { dialog.implementation, dialog.implementationType, dialog.eventName, getFieldString(dialog.fieldExtensionList) };
    } else {
      return null;
    }
  }
	
	private String getFieldString(List<FieldExtensionModel> fieldList) {
	  String fieldString = "";
    if(fieldList != null) {
      for (FieldExtensionModel fieldExtension : fieldList) {
        if(fieldString.length() > 0) {
          fieldString += ", ";
        }
        fieldString += fieldExtension.fieldName + ":" + fieldExtension.expression;
      }
    }
    return fieldString;
	}
	
	@Override
	protected void selectionChanged() {
		super.selectionChanged();
		saveExecutionListeners();
	}
	
	private void saveExecutionListeners() {
		if (pictogramElement != null && getNumberOfItems() > 0) {
		  final Object bo = BpmnBOUtil.getExecutionLisenerBO(pictogramElement, diagram);
		  if (bo == null) {
        return;
      }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					for (TableItem item : getItems()) {
						String implementation = item.getText(0);
						String implementationType = item.getText(1);
						String event = item.getText(2);
						String fields = item.getText(3);
						if(implementation != null && implementation.length() > 0 &&
						        event != null && event.length() > 0) {
							
						  ExecutionListener executionListener = executionListenerExists(bo, event, implementationType, implementation);
							if(executionListener != null) {
							  executionListener.setEvent(event);
							  executionListener.setImplementation(implementation);
							  executionListener.setImplementationType(implementationType);
							  setFieldsInListener(executionListener, fields);
							} else {
							  ExecutionListener newExecutionListener = Bpmn2Factory.eINSTANCE.createExecutionListener();
							  newExecutionListener.setEvent(event);
							  newExecutionListener.setImplementationType(implementationType);
							  newExecutionListener.setImplementation(implementation);
							  setFieldsInListener(executionListener, fields);
								BpmnBOUtil.addExecutionListener(bo, newExecutionListener);
							}
						}
					}
					removeExecutionListenersNotInList(getItems(), bo);
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private void setFieldsInListener(ExecutionListener executionListener, String fieldString) {
	  if(fieldString == null || fieldString.length() == 0) {
	    removeFieldExtensionsNotInList(executionListener.getFieldExtensions(), null);
	    return;
	  }
	  String[] fieldStringList = fieldString.split(", ");
	  for (String field : fieldStringList) {
	    String[] fieldExtensionStringList = field.split(":");
	    FieldExtension fieldExtension = fieldExtensionExists(executionListener.getFieldExtensions(), fieldExtensionStringList[0]);
	    if(fieldExtension == null) {
	      fieldExtension = Bpmn2Factory.eINSTANCE.createFieldExtension();
	      executionListener.getFieldExtensions().add(fieldExtension);
	    }
	    fieldExtension.setFieldname(fieldExtensionStringList[0]);
	    fieldExtension.setExpression(fieldExtensionStringList[1]);
    }
	  removeFieldExtensionsNotInList(executionListener.getFieldExtensions(), fieldStringList);
	}
	
	private FieldExtension fieldExtensionExists(List<FieldExtension> fieldList, String fieldname) {
	  if(fieldList == null) return null;
	  for(FieldExtension fieldExtension : fieldList) {
      if(fieldname.equalsIgnoreCase(fieldExtension.getFieldname())) {
        return fieldExtension;
      }
    }
    return null;
	}
	
	private void removeFieldExtensionsNotInList(List<FieldExtension> fieldList, String[] fieldStringList) {
	  Iterator<FieldExtension> entryIterator = fieldList.iterator();
    while(entryIterator.hasNext()) {
      FieldExtension fieldExtension = entryIterator.next();
      boolean found = false;
      if(fieldStringList != null && fieldStringList.length > 0) {
        for (String field : fieldStringList) {
          String[] fieldExtensionStringList = field.split(":");
          if(fieldExtensionStringList[0].equals(fieldExtension.getFieldname())) {
            found = true;
            break;
          }
        }
      }
      if(found == false) {
        diagram.eResource().getContents().remove(fieldExtension);
        entryIterator.remove();
      }
    }
	}
	
	private ExecutionListener executionListenerExists(Object bo, String event, String implementationType, String implementation) {
	  List<ExecutionListener> executionListenerList = BpmnBOUtil.getExecutionListeners(bo);
		if(executionListenerList == null) return null;
		for(ExecutionListener executionListener : executionListenerList) {
			if(event.equalsIgnoreCase(executionListener.getEvent()) &&
			        implementationType.equalsIgnoreCase(executionListener.getImplementationType()) &&
			        implementation.equalsIgnoreCase(executionListener.getImplementation())) {
			  
				return executionListener;
			}
		}
		return null;
	}
	
	private void removeExecutionListenersNotInList(TableItem[] items, Object bo) {
		Iterator<ExecutionListener> entryIterator = BpmnBOUtil.getExecutionListeners(bo).iterator();
		while(entryIterator.hasNext()) {
		  ExecutionListener executionListener = entryIterator.next();
			boolean found = false;
			for (TableItem item : items) {
				if(item.getText(0).equals(executionListener.getImplementation()) &&
				        item.getText(1).equals(executionListener.getImplementationType()) &&
				        item.getText(2).equals(executionListener.getEvent())) {
					found = true;
					break;
				}
			}
			if(found == false) {
				diagram.eResource().getContents().remove(executionListener);
				entryIterator.remove();
			}
		}
	}
	
}
