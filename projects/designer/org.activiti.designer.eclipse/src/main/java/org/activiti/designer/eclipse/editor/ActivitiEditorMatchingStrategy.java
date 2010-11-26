package org.activiti.designer.eclipse.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;

public class ActivitiEditorMatchingStrategy implements IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorRef, IEditorInput input) {
        IFile inputFile = ResourceUtil.getFile(input);
        if (inputFile == null || !(input instanceof IFileEditorInput)) return false; 
    	String inputFilePath = inputFile.getFullPath().removeLastSegments(1).toString();
        String inputFileName = inputFile.getFullPath().lastSegment();
        try {
            IFile editorFile = ResourceUtil.getFile(editorRef.getEditorInput());
            if (editorFile == null) return false;
            String editorFilePath = editorFile.getFullPath().removeLastSegments(1).toString();
            if (!editorFilePath.equals(inputFilePath)) return false;
            return false;
        } catch (PartInitException e) {
            return false;
        }
    }
}
