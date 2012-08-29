package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
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

public class PropertyIntermediateCatchTimerSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text timeDurationText;
	private Text timeDateText;
	private Text timeCycleText;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		
		timeDurationText = createText(composite, factory, null);
		createLabel(composite, "Time duration", timeDurationText, factory); //$NON-NLS-1$
		
		timeDateText = createText(composite, factory, timeDurationText);
    createLabel(composite, "Time date (ISO 8601)", timeDateText, factory); //$NON-NLS-1$
    
    timeCycleText = createText(composite, factory, timeDateText);
    createLabel(composite, "Time cycle", timeCycleText, factory); //$NON-NLS-1$
	}

	@Override
	public void refresh() {
	  timeDurationText.removeFocusListener(listener);
	  timeDateText.removeFocusListener(listener);
	  timeCycleText.removeFocusListener(listener);
	  
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = getBusinessObject(pe);
			// the filter assured, that it is a EClass
			if (bo == null)
				return;
			
			IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) bo;
			
			if(catchEvent.getEventDefinitions().get(0) != null) {
			  TimerEventDefinition timerDefinition = (TimerEventDefinition) catchEvent.getEventDefinitions().get(0);
        if(StringUtils.isNotEmpty(timerDefinition.getTimeDuration())) {
          String timeDuration = timerDefinition.getTimeDuration();
          timeDurationText.setText(timeDuration == null ? "" : timeDuration);
          
        } else if(StringUtils.isNotEmpty(timerDefinition.getTimeDate())) {
          String timeDate = timerDefinition.getTimeDate();
          timeDateText.setText(timeDate == null ? "" : timeDate);
          
        } else if(StringUtils.isNotEmpty(timerDefinition.getTimeCycle())) {
          String timeCycle = timerDefinition.getTimeCycle();
          timeCycleText.setText(timeCycle == null ? "" : timeCycle);
        }
			}
		}
		timeDurationText.addFocusListener(listener);
		timeDateText.addFocusListener(listener);
		timeCycleText.addFocusListener(listener);
	}

	private FocusListener listener = new FocusListener() {

		public void focusGained(final FocusEvent e) {
		}

		public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof IntermediateCatchEvent) {
					DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
					TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
					ActivitiUiUtil.runModelChange(new Runnable() {
						public void run() {
							
							IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) bo;
							
              TimerEventDefinition timerDefinition = (TimerEventDefinition) catchEvent.getEventDefinitions().get(0);
              
							String timeDuration = timeDurationText.getText();
							if (timeDuration != null) {
							  timerDefinition.setTimeDuration(timeDuration);
							}
							
							String timeDate = timeDateText.getText();
              if (timeDate != null) {
                timerDefinition.setTimeDate(timeDate);
              }
              
              String timeCycle = timeCycleText.getText();
              if (timeCycle != null) {
                timerDefinition.setTimeCycle(timeCycle);
              }
						}
					}, editingDomain, "Model Update");
				}

			}
		}
	};
	
	private Text createText(Composite parent, TabbedPropertySheetWidgetFactory factory, Control top) {
    Text text = factory.createText(parent, ""); //$NON-NLS-1$
    FormData data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, -HSPACE);
    if(top == null) {
      data.top = new FormAttachment(0, VSPACE);
    } else {
      data.top = new FormAttachment(top, VSPACE);
    }
    text.setLayoutData(data);
    text.addFocusListener(listener);
    return text;
  }
  
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
