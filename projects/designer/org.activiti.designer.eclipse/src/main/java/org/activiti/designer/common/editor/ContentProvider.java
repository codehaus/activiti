package org.activiti.designer.common.editor;

import org.activiti.designer.common.notation.RootContainer;
import org.eclipse.ui.IEditorInput;

public interface ContentProvider {
	
	boolean saveToInput(IEditorInput input, RootContainer rootContainer);
	void addNotationInfo(RootContainer rootContainer, IEditorInput input);
	
	String getNotationInfoFileName(String semanticInfoFileName);
	String getDiagramImageFileName(String semanticInfoFileName);
	
}
