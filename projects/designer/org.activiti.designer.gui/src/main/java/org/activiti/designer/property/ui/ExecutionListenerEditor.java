package org.activiti.designer.property.ui;

import java.util.Iterator;
import java.util.List;

import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.ExecutionListener;
import org.eclipse.bpmn2.FieldExtension;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class ExecutionListenerEditor extends TableFieldEditor {
	
	private Composite parent;
	public PictogramElement pictogramElement;
	public IDiagramEditor diagramEditor;
	public Diagram diagram;
	
	public ExecutionListenerEditor(String key, Composite parent) {
		
        super(key, "", new String[] {"Listener implementation", "Event", "Fields"},
        		new int[] {200, 100, 300}, parent);
        this.parent = parent;
	}
	
	public void initialize(List<ExecutionListener> listenerList) {
		if(listenerList == null || listenerList.size() == 0) return;
		removeTableItems();
		for (ExecutionListener executionListener : listenerList) {
			addTableItem(executionListener.getImplementation(), executionListener.getEvent(), executionListener.getFieldExtensions());
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
	
	protected void addTableItem(String implementation, String event, List<FieldExtension> fieldExtensions) {
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      tableItem.setText(0, implementation);
      tableItem.setText(1, event);
      String fieldString = "";
      for (FieldExtension fieldExtension : fieldExtensions) {
        fieldString += fieldExtension.getFieldname() + ":" + fieldExtension.getExpression();
      }
      tableItem.setText(2, fieldString);
    }
  }

	@Override
	protected String[] getNewInputObject() {
		ExecutionListenerDialog dialog = new ExecutionListenerDialog(parent.getShell(), getItems());
		dialog.open();
		String event = dialog.eventName;
		if(event != null && event.length() > 0) {
			
			return new String[] { event };
		} else {
			return null;
		}
	}
	
	@Override
  protected String[] getChangedInputObject(TableItem item) {
    ExecutionListenerDialog dialog = new ExecutionListenerDialog(parent.getShell(), getItems());
    dialog.open();
    String event = dialog.eventName;
    if(event != null && event.length() > 0) {
      
      return new String[] { event };
    } else {
      return null;
    }
  }
	
	@Override
	protected void selectionChanged() {
		super.selectionChanged();
		saveExecutionListeners();
	}
	
	private void saveExecutionListeners() {
		if (pictogramElement != null && getNumberOfItems() > 0) {
		  Object bo = null;
      if(pictogramElement instanceof Diagram) {
        bo = ActivitiUiUtil.getProcessObject(diagram);
      } else {
        bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
      }
			TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
			ActivitiUiUtil.runModelChange(new Runnable() {
				public void run() {
					Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
					if (bo == null) {
						return;
					}
					ServiceTask serviceTask = (ServiceTask)  bo;
					for (TableItem item : getItems()) {
						String implementation = item.getText(0);
						String event = item.getText(1);
						String fields = item.getText(2);
						if(implementation != null && implementation.length() > 0 &&
						        event != null && event.length() > 0) {
							
							/*FieldExtension fieldExtension = executionListenerExists(serviceTask, fieldName);
							if(fieldExtension != null) {
								fieldExtension.setExpression(fieldExpression);
							} else {
								FieldExtension newFieldExtension = Bpmn2Factory.eINSTANCE.createFieldExtension();
								newFieldExtension.setFieldname(fieldName);
								newFieldExtension.setExpression(fieldExpression);
								diagram.eResource().getContents().add(newFieldExtension);
								serviceTask.getFieldExtensions().add(newFieldExtension);
							}*/
						}
					}
					removeFieldExtensionsNotInList(getItems(), serviceTask);
				}
			}, editingDomain, "Model Update");
		}
	}
	
	private FieldExtension executionListenerExists(ServiceTask serviceTask, String fieldName) {
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
