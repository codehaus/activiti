package org.activiti.designer.common.part;

import java.beans.PropertyChangeListener;

import org.activiti.designer.common.notation.AbstractNotationElement;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.IActionFilter;

public interface NotationElementGraphicalEditPart  
extends EditPart, PropertyChangeListener, IActionFilter {
	
	public AbstractNotationElement getNotationElement();

}
