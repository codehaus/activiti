package org.activiti.designer.common.notation;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.common.model.SemanticElement;

public class Edge extends AbstractNotationElement {

	Node source;
	Node target;

	Label label;
	List bendPoints = new ArrayList();

	public Label getLabel() {
		if (label == null) {
			label = (Label) getFactory().create("org.activiti.designer.eclipse.label");
			addPropertyChangeListener(label);
		}
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setSource(Node newSource) {
		source = newSource;
	}

	public Node getSource() {
		return source;
	}

	public void setTarget(Node newTarget) {
		target = newTarget;
	}

	public Node getTarget() {
		return target;
	}

	public void setSemanticElement(SemanticElement semanticElement) {
		super.setSemanticElement(semanticElement);
		getLabel().setSemanticElement(semanticElement);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if ("name".equals(evt.getPropertyName())) {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		} else {
			super.propertyChange(evt);
		}
	}
	
}
