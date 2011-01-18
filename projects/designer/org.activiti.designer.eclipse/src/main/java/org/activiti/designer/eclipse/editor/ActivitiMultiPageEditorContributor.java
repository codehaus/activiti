package org.activiti.designer.eclipse.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class ActivitiMultiPageEditorContributor extends
		MultiPageEditorActionBarContributor {

	/**
	 * Creates a multi-page contributor.
	 */
	@Override
	public void setActivePage(IEditorPart arg0) {
		// TODO Auto-generated method stub

	}

}
