package org.activiti.designer.eclipse.navigator.diagramtree;

import org.activiti.designer.bpmn2.model.TextAnnotation;

public class TextAnnotationDiagramTreeNode extends AbstractDiagramTreeNode<TextAnnotation> {

	protected TextAnnotationDiagramTreeNode(Object parent,
			TextAnnotation modelObject) {
		super(parent, modelObject, modelObject.getId());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void extractChildren() {
		// TODO Auto-generated method stub
		
	}

}
