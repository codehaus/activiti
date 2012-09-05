package org.activiti.designer.property;

/**
 * @author Saeid Mirzaei
 */

import org.activiti.designer.bpmn2.model.Message;
import org.activiti.designer.bpmn2.model.MessageEventDefinition;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyMessageStartEventSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
	private CCombo messageCombo;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;
    
    messageCombo = getWidgetFactory().createCCombo(composite, SWT.NONE);
    data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    data.top = new FormAttachment(0, VSPACE);
    messageCombo.setLayoutData(data);
    messageCombo.addFocusListener(listener);

    createLabel(composite, "Message ref", messageCombo, factory); //$NON-NLS-1$


	}

	@Override
	public void refresh() {
	  
	  String messageRef="";
	  messageCombo.removeFocusListener(listener);
	  
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;

			final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model == null) {
        return;
      }
			
			
			StartEvent startEvent = (StartEvent) bo;
			if (startEvent.getEventDefinitions().isEmpty())
			  return;
			
			if(startEvent.getEventDefinitions().get(0) != null && 
			        (startEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition)) {
			  MessageEventDefinition messageDefinition = (MessageEventDefinition) startEvent.getEventDefinitions().get(0);
        if(StringUtils.isNotEmpty(messageDefinition.getMessageRef())) {
          messageRef = messageDefinition.getMessageRef();          
        }
			}
			
      String[] items = new String[model.getMessages().size() + 1];
      items[0] = "";
      int counter = 1;
      int selectedCounter = 0;
      for (Message message : model.getMessages()) {
        items[counter] = message.getId() + " / " + message.getName();
        if(message.getId().equals(messageRef)) {
          selectedCounter = counter;
        }
        counter++;
      }
      messageCombo.setItems(items);
      messageCombo.select(selectedCounter);
      
  			
		}
		 messageCombo.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
      final Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
      if (model == null) {
        return;
      }
      
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final Object bo = getBusinessObject(pe);
        if (bo instanceof StartEvent ) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {
            public void run() {
              if (((StartEvent) bo).getEventDefinitions().isEmpty() ||
                      !(((StartEvent) bo).getEventDefinitions().get(0) instanceof  MessageEventDefinition))
                      return;
              MessageEventDefinition messageDefinition = (MessageEventDefinition) ((StartEvent) bo).getEventDefinitions().get(0);
              if (messageDefinition == null)
                  return;
              if(messageCombo.getSelectionIndex() > 0) {
                messageDefinition.setMessageRef(model.getMessages().get(messageCombo.getSelectionIndex() - 1).getId());
              } else {
                messageDefinition.setMessageRef("");
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
