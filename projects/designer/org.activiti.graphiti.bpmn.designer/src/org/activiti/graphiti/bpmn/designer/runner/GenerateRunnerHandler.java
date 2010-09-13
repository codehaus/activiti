package org.activiti.graphiti.bpmn.designer.runner;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class GenerateRunnerHandler extends AbstractHandler {

	private static final String BPMN_FILENAME_SUFFIX = "bpmn20.xml";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object bpmnFile = null;
			for (@SuppressWarnings("unchecked")
			Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();){
				bpmnFile = iterator.next();
			}
			if(bpmnFile == null || !bpmnFile.toString().endsWith(BPMN_FILENAME_SUFFIX)){
				MessageDialog.openInformation(
						HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Generate Process Runner",
						"The process you want to generate a runner for should have a filename that ends with " + BPMN_FILENAME_SUFFIX);
			}else{
				try {
					TestRunnerClassGenerator.generateTestClass((IResource) bpmnFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
