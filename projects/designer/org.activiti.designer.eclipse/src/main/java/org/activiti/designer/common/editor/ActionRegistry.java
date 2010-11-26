package org.activiti.designer.common.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.StackAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;


public class ActionRegistry extends org.eclipse.gef.ui.actions.ActionRegistry {
	
	private IEditorPart editorPart;
	
	private List editPartActionIds;
	private List stackActionIds;
	private List editorActionIds;
	
	public ActionRegistry(IEditorPart editorPart) {
		initEditorPart(editorPart);
		initActionLists();
		initActions();
	}
	
	private void initEditorPart(IEditorPart part) {
		this.editorPart = part;
	}
	
	private void initActionLists() {
		editPartActionIds = new ArrayList();
		stackActionIds = new ArrayList();
		editorActionIds = new ArrayList();		
	}

	private void initActions() {
		addStackAction(new UndoAction(editorPart));
		addStackAction(new RedoAction(editorPart));	
		addEditPartAction(new DeleteAction((IWorkbenchPart)editorPart));
		addEditPartAction(new SaveAction(editorPart));
		registerAction(new PrintAction(editorPart));
	}
		
	private void addEditPartAction(WorkbenchPartAction action) {
		registerAction(action);
		editPartActionIds.add(action.getId());
	}
	
	private void addStackAction(StackAction action) {
		registerAction(action);
		
		stackActionIds.add(action.getId());
	}
	
	private void updateActions(List actionIds) {
		for(Iterator ids = actionIds.iterator(); ids.hasNext();) {
			IAction action = getAction(ids.next());
			if (null != action && action instanceof UpdateAction)
				((UpdateAction)action).update();
		}
	}
	
	public void updateStackActions() {
		updateActions(stackActionIds);
	}
	
	public void updateEditPartActions() {
		updateActions(editPartActionIds);
	}
	
	public void updateEditorActions() {
		updateActions(editorActionIds);
	}
	
}
