package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.BoundaryEvent;
import org.activiti.designer.bpmn2.model.EventDefinition;
import org.activiti.designer.bpmn2.model.TimerEventDefinition;
import org.activiti.designer.util.property.ActivitiPropertyFilter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyBoundaryTimerFilter extends ActivitiPropertyFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof BoundaryEvent) {
		  if(((BoundaryEvent) bo).getEventDefinitions() != null) {
		    for(EventDefinition eventDefinition : ((BoundaryEvent) bo).getEventDefinitions()) {
		      if(eventDefinition instanceof TimerEventDefinition) {
		        return true;
		      }
		    }
		  }
		}
		return false;
	}

}
