package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.designer.model.FieldExtensionModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public class ExecutionListenerDialog extends Dialog implements ITabbedPropertyConstants {
	
	public String implementationType;
	public String implementation;
	public String eventName;
	public List<FieldExtensionModel> fieldExtensionList;
	
	private String savedImplementationType;
  private String savedImplementation;
  private String savedEventName;
	
	private TableItem[] fieldList;
	
	private Combo eventDropDown;
	private Button classTypeButton;
	private Button expressionTypeButton;
	private Text classNameText;
  private Button classSelectButton;
  private CLabel classSelectLabel;
  private Text expressionText;
  private CLabel expressionLabel;
  private FieldExtensionEditor fieldEditor;
	
	private final static String CLASS_TYPE = "classType";
  private final static String EXPRESSION_TYPE = "expressionType";

	public ExecutionListenerDialog(Shell parent, TableItem[] fieldList) {
		// Pass the default styles here
		this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, String savedImplementationType, 
	        String savedImplementation, String savedEventName) {
    // Pass the default styles here
    this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.savedImplementationType = savedImplementationType;
    this.savedImplementation = savedImplementation;
    this.savedEventName = savedEventName;
  }

	public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, int style) {
		// Let users override the default styles
		super(parent, style);
		this.fieldList = fieldList;
		setText("Execution listener");
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		shell.setText(getText());
		shell.setSize(700, 400);
		Point location = getParent().getShell().getLocation();
		Point size = getParent().getShell().getSize();
		shell.setLocation((location.x + size.x - 300) / 2, (location.y + size.y - 150) / 2);
		createContents(shell);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
	  FormLayout layout = new FormLayout();
	  layout.marginHeight = 5;
	  layout.marginWidth = 5;
	  shell.setLayout(layout);
	  FormData data;
	  
	  eventDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
	  eventDropDown.add("Start");
	  eventDropDown.add("End");
	  
	  if(savedEventName != null) {
	    eventDropDown.setText(savedEventName);
	  } else {
	    eventDropDown.setText("Start");
	  }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    eventDropDown.setLayoutData(data);
    
    CLabel eventLabel = new CLabel(shell, SWT.NONE);
    eventLabel.setText("Event");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(eventDropDown, -HSPACE);
    data.top = new FormAttachment(eventDropDown, 0, SWT.TOP);
    eventLabel.setLayoutData(data);
    eventLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		Composite radioTypeComposite = new Composite(shell, SWT.NULL);
    radioTypeComposite.setBackground(shell.getBackground());
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(eventDropDown, VSPACE);
    radioTypeComposite.setLayoutData(data);
    radioTypeComposite.setLayout(new RowLayout());
      
    classTypeButton = new Button(radioTypeComposite, SWT.RADIO);
    classTypeButton.setText("Java class");
    classTypeButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        setVisibleClassType(true);
        setVisibleExpressionType(false);
        setImplementationType(CLASS_TYPE);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
      
    });
    expressionTypeButton = new Button(radioTypeComposite, SWT.RADIO);
    expressionTypeButton.setText("Expression");
    expressionTypeButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        setVisibleClassType(false);
        setVisibleExpressionType(true);
        setImplementationType(EXPRESSION_TYPE);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
      
    });
    
    Label typeLabel = new Label(shell, SWT.NONE);
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(radioTypeComposite, -HSPACE);
    data.top = new FormAttachment(radioTypeComposite, 0, SWT.TOP);
    typeLabel.setText("Type");
    typeLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

    classNameText = new Text(shell, SWT.BORDER);
    if(CLASS_TYPE.equals(savedImplementationType)) {
      classNameText.setText(savedImplementation);
    } else {
      classNameText.setText("");
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(70, 0);
    data.top = new FormAttachment(radioTypeComposite, 10);
    classNameText.setEnabled(false);
    classNameText.setLayoutData(data);

    classSelectButton = new Button(shell, SWT.PUSH);
    classSelectButton.setText("Select class");
    data = new FormData();
    data.left = new FormAttachment(classNameText, 0);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(classNameText, -4, SWT.TOP);
    classSelectButton.setLayoutData(data);
    classSelectButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = classNameText.getShell();
        try {
          SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell),
              SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false);

          if (dialog.open() == SelectionDialog.OK) {
            Object[] result = dialog.getResult();
            String className = ((IType) result[0]).getFullyQualifiedName();
            IJavaProject containerProject = ((IType) result[0]).getJavaProject();

            if (className != null) {
              classNameText.setText(className);
            }

            /*DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
            TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();

            ActivitiUiUtil.runModelChange(new Runnable() {
              public void run() {
                Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(
                    getSelectedPictogramElement());
                if (bo == null) {
                  return;
                }
                String implementationName = classNameText.getText();
                if (implementationName != null) {
                  if (bo instanceof ServiceTask) {

                    ((ServiceTask) bo).setImplementation(implementationName);
                  }
                }
              }
            }, editingDomain, "Model Update");

            IProject currentProject = ActivitiUiUtil.getProjectFromDiagram(getDiagram());

            ActivitiUiUtil.doProjectReferenceChange(currentProject, containerProject, className);*/
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    
    classSelectLabel = new CLabel(shell, SWT.NONE);
    classSelectLabel.setText("Service class:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(classNameText, -HSPACE);
    data.top = new FormAttachment(classNameText, -2, SWT.TOP);
    classSelectLabel.setLayoutData(data);
    classSelectLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    expressionText = new Text(shell, SWT.BORDER);
    if(EXPRESSION_TYPE.equals(savedImplementationType)) {
      expressionText.setText(savedImplementation);
    } else {
      expressionText.setText("");
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(radioTypeComposite, VSPACE);
    expressionText.setLayoutData(data);
    
    expressionLabel = new CLabel(shell, SWT.NONE);
    expressionLabel.setText("Expression");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(expressionText, -HSPACE);
    data.top = new FormAttachment(expressionText, 0, SWT.TOP);
    expressionLabel.setLayoutData(data);
    expressionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    Composite extensionsComposite = new Composite(shell, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(expressionText, 15);
    extensionsComposite.setLayoutData(data);
    GridLayout fieldLayout = new GridLayout();
    fieldLayout.marginTop = 0;
    fieldLayout.numColumns = 1;
    extensionsComposite.setLayout(fieldLayout);
    fieldEditor = new FieldExtensionEditor("fieldEditor", extensionsComposite);
    fieldEditor.getLabelControl(extensionsComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel extensionLabel = new CLabel(shell, SWT.NONE);
    extensionLabel.setText("Fields");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(extensionsComposite, -HSPACE);
    data.top = new FormAttachment(extensionsComposite, 0, SWT.TOP);
    extensionLabel.setLayoutData(data);
    extensionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    // Create the cancel button and add a handler
    // so that pressing it will set input to null
    Button cancel = new Button(shell, SWT.PUSH);
    cancel.setText("Cancel");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(extensionsComposite, 20);
    cancel.setLayoutData(data);
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(cancel, -HSPACE);
    data.top = new FormAttachment(cancel, 0, SWT.TOP);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if(CLASS_TYPE.equals(implementationType) && (classNameText.getText() == null || classNameText.getText().length() == 0)) {
					MessageDialog.openError(shell, "Validation error", "Class name must be filled.");
					return;
				}
				if(EXPRESSION_TYPE.equals(implementationType) && (expressionText.getText() == null || expressionText.getText().length() == 0)) {
          MessageDialog.openError(shell, "Validation error", "Expression must be filled.");
          return;
        }
				eventName = eventDropDown.getText();
				if(CLASS_TYPE.equals(implementationType)) {
				  implementation = classNameText.getText();
				} else {
				  implementation = expressionText.getText();
				}
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
		
		if(savedImplementationType == null || CLASS_TYPE.equals(savedImplementationType)) {
      classTypeButton.setSelection(true);
      setImplementationType(CLASS_TYPE);
      setVisibleClassType(true);
      setVisibleExpressionType(false);
    } else {
      expressionTypeButton.setSelection(true);
      setImplementationType(EXPRESSION_TYPE);
      setVisibleClassType(false);
      setVisibleExpressionType(true);
    }
	}
	
	private void setImplementationType(final String type) {
    implementationType = type;
  }
	
	private void setVisibleClassType(boolean visible) {
    classTypeButton.setSelection(visible);
    classNameText.setVisible(visible);
    classSelectButton.setVisible(visible);
    classSelectLabel.setVisible(visible);
  }
  
  private void setVisibleExpressionType(boolean visible) {
    expressionTypeButton.setSelection(visible);
    expressionText.setVisible(visible);
    expressionLabel.setVisible(visible);
  }
}
