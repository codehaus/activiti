package org.activiti.designer.eclipse.editor;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFilterMatcherDescriptor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ISynchronizer;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.WorkspaceLock;
import org.eclipse.core.resources.IWorkspace.ProjectOrder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class StringEditorInput implements IStorageEditorInput {



    private final String inputString;



    public StringEditorInput(String inputString) {

        this.inputString = inputString;

    }



    public boolean exists() {

        return false;

    }



    public ImageDescriptor getImageDescriptor() {

        return null;

    }



    public IPersistableElement getPersistable() {

        return null;

    }



    public Object getAdapter(Class adapter) {

        return null;

    }



    public String getName() {

        return inputString;

    }



    public String getToolTipText() {

        return "tool tip";

    }



    public IStorage getStorage() throws CoreException {

        return new IStorage() {



            public InputStream getContents() throws CoreException {

                return new StringBufferInputStream(inputString);

            }



            public IPath getFullPath() {

                return null;

            }



            public String getName() {

                return StringEditorInput.this.getName();

            }



            public boolean isReadOnly() {

                return false;

            }



            public Object getAdapter(Class adapter) {

                return null;

            }



        };

    }
}