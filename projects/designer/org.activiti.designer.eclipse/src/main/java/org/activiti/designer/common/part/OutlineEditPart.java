package org.activiti.designer.common.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.activiti.designer.common.model.SemanticElement;
import org.activiti.designer.common.util.SharedImages;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IActionFilter;

public abstract class OutlineEditPart extends AbstractTreeEditPart implements
PropertyChangeListener, IActionFilter{
	
	public OutlineEditPart(SemanticElement model) {
		setModel(model);
	}
	
	protected Image getImage() {
		return SharedImages.INSTANCE.getImage(((SemanticElement)getModel()).getIconDescriptor());
	}

	protected String getText() {
		String result = ((SemanticElement)getModel()).getLabel();
		return result == null ? super.getText() : ((SemanticElement)getModel()).getElementId();
	}
	
	private SemanticElement getActivitiElement() {
		return (SemanticElement)getModel();
	}

	public void activate() {
		if (!isActive()) {
			getActivitiElement().addPropertyChangeListener(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getActivitiElement().removePropertyChangeListener(this);
			super.deactivate();
		}
	}		

	protected void handleChildAdd(Object newValue) {
		refreshChildren();
		List children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			EditPart part = (EditPart)children.get(i);
			if (part.getModel() == newValue) {
				part.getViewer().select(part);
			}
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String eventName = evt.getPropertyName();
		if (eventName.equals("name")) {
			refreshVisuals();
		}
		this.getText();
	}

	public boolean testAttribute(Object target, String name, String value) {
		return false;
	}
		
}
