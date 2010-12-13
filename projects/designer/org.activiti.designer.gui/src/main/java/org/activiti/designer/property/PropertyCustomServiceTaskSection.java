package org.activiti.designer.property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.GridDataProperty;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.validator.FieldValidator;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.custom.PeriodDialog;
import org.activiti.designer.property.custom.PeriodPropertyElement;
import org.activiti.designer.property.extension.ExtensionPropertyUtil;
import org.activiti.designer.property.extension.ExtensionUtil;
import org.activiti.designer.property.extension.FieldValidatorListener;
import org.activiti.designer.property.extension.FieldWrapper;
import org.activiti.designer.property.extension.FormToolTip;
import org.activiti.designer.util.ActivitiUiUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyCustomServiceTaskSection extends GFPropertySection implements ITabbedPropertyConstants {

  private static Font boldFont;
  private static Font italicFont;

  private static final String PROPERTY_REQUIRED_DISPLAY = " (*)";

  private List<CustomServiceTask> customServiceTasks;

  private Map<String, FieldWrapper> fieldControls;

  private Composite parent;
  private Composite workParent;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);
    if (boldFont == null) {
      boldFont = new Font(parent.getDisplay(), new FontData("boldFontData", 10, SWT.BOLD));
    }
    if (italicFont == null) {
      italicFont = new Font(parent.getDisplay(), new FontData("italicFontData", 10, SWT.ITALIC));
    }

    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
   * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {

    super.setInput(part, selection);

    FormData data;

    fieldControls = new HashMap<String, FieldWrapper>();

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

    if (workParent != null) {
      workParent.dispose();
    }

    workParent = factory.createFlatFormComposite(parent);

    customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

    final ServiceTask serviceTask = getServiceTask();

    if (serviceTask != null) {

      CustomServiceTask targetTask = null;

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (customServiceTask.getId().equals(ExtensionUtil.getCustomServiceTaskId(serviceTask))) {
          targetTask = customServiceTask;
          break;
        }
      }

      if (targetTask != null) {

        final List<Class<CustomServiceTask>> classHierarchy = new ArrayList<Class<CustomServiceTask>>();
        final List<Field> fields = new ArrayList<Field>();

        Class clazz = targetTask.getClass();
        classHierarchy.add(clazz);

        boolean hierarchyOpen = true;
        while (hierarchyOpen) {
          clazz = clazz.getSuperclass();
          if (CustomServiceTask.class.isAssignableFrom(clazz)) {
            classHierarchy.add(clazz);
          } else {
            hierarchyOpen = false;
          }
        }

        for (final Class<CustomServiceTask> currentClass : classHierarchy) {
          for (final Field field : currentClass.getDeclaredFields()) {
            fields.add(field);
          }
        }

        Control previousAnchor = workParent;

        final CLabel labelNodeName = factory.createCLabel(workParent, targetTask.getName(), SWT.NONE);
        labelNodeName.setFont(boldFont);
        data = new FormData();
        data.top = new FormAttachment(previousAnchor, VSPACE);
        labelNodeName.setLayoutData(data);

        previousAnchor = labelNodeName;

        if (targetTask.getClass().isAnnotationPresent(Help.class)) {
          final Help helpAnnotation = targetTask.getClass().getAnnotation(Help.class);

          final CLabel labelShort = factory.createCLabel(workParent, helpAnnotation.displayHelpShort(), SWT.WRAP);

          data = new FormData();
          data.top = new FormAttachment(previousAnchor, VSPACE);
          labelShort.setLayoutData(data);
          labelShort.setFont(italicFont);

          previousAnchor = labelShort;

          final CLabel labelLong = factory.createCLabel(workParent, helpAnnotation.displayHelpLong(), SWT.WRAP);
          data = new FormData();
          data.top = new FormAttachment(previousAnchor);
          data.left = new FormAttachment(workParent, HSPACE, SWT.LEFT);
          data.right = new FormAttachment(100, HSPACE);
          labelLong.setLayoutData(data);

          previousAnchor = labelLong;
        }

        for (final Field field : fields) {
          final Annotation[] annotations = field.getAnnotations();
          for (final Annotation annotation : annotations) {
            if (annotation instanceof Property) {

              final Property property = (Property) annotation;

              Control createdControl = null;

              switch (property.type()) {

              case TEXT:
                createdControl = createTextProperty(factory, previousAnchor, property);
                break;

              case MULTILINE_TEXT:
                createdControl = createMultilineTextProperty(factory, previousAnchor, property);
                break;

              case PERIOD:
                createdControl = createPeriodProperty(factory, previousAnchor, property);
                break;

              case DATA_GRID:

                if (List.class.isAssignableFrom(field.getType())) {

                  final GridDataProperty listProperty = field.getAnnotation(GridDataProperty.class);
                  if (listProperty != null) {

                    final Composite listParent = factory.createFlatFormComposite(workParent);
                    data = new FormData();
                    data.top = new FormAttachment(previousAnchor, VSPACE);
                    data.left = new FormAttachment(0, 200);
                    data.right = new FormAttachment(80, 25);
                    listParent.setLayoutData(data);

                    Class< ? extends Object> itemClass = listProperty.itemClass();

                    Field[] itemClassDeclaredFields = itemClass.getDeclaredFields();

                    final List<Field> itemClassFields = new ArrayList<Field>();

                    for (final Field itemClassField : itemClassDeclaredFields) {
                      final Annotation[] itemClassFieldAnnotations = itemClassField.getAnnotations();
                      for (final Annotation itemClassFieldAnnotation : itemClassFieldAnnotations) {
                        if (itemClassFieldAnnotation instanceof Property) {
                          itemClassFields.add(itemClassField);
                          break;
                        }
                      }
                    }

                    // Set the layout for the contents of the listParent to a
                    // grid
                    final GridLayout layout = new GridLayout(itemClassFields.size() + 1, false);
                    listParent.setLayout(layout);

                    // Create a row with headers
                    for (final Field itemClassField : itemClassFields) {
                      final Annotation[] itemClassFieldAnnotations = itemClassField.getAnnotations();
                      for (final Annotation itemClassFieldAnnotation : itemClassFieldAnnotations) {
                        if (itemClassFieldAnnotation instanceof Property) {
                          final Property itemClassFieldProperty = (Property) itemClassFieldAnnotation;

                          Composite headerGroup = factory.createFlatFormComposite(listParent);

                          final GridLayout headerLayout = new GridLayout(2, false);
                          headerGroup.setLayout(headerLayout);

                          final CLabel headerLabel = factory.createCLabel(headerGroup, itemClassFieldProperty.displayName());
                          GridData headerLabelData = new GridData();
                          headerLabelData.grabExcessHorizontalSpace = true;
                          headerLabelData.horizontalAlignment = GridData.FILL;
                          headerLabel.setLayoutData(headerLabelData);
                          headerLabel.setFont(boldFont);

                          final Help help = getHelp(itemClassField);
                          if (help != null) {
                            final Button propertyHelp = factory.createButton(headerGroup, "", SWT.BUTTON1);
                            propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP));

                            GridData headerButtonData = new GridData();
                            headerButtonData.horizontalAlignment = SWT.END;
                            headerLabel.setLayoutData(headerButtonData);

                            // create a tooltip
                            final ToolTip tooltip = new FormToolTip(propertyHelp, String.format("Help for field %s", itemClassFieldProperty.displayName()
                                    .equals("") ? itemClassField.getName() : itemClassFieldProperty.displayName()), help.displayHelpShort(),
                                    help.displayHelpLong());
                            tooltip.setHideOnMouseDown(false);

                            propertyHelp.addMouseListener(new MouseListener() {

                              @Override
                              public void mouseUp(MouseEvent e) {
                              }

                              @Override
                              public void mouseDown(MouseEvent e) {
                                tooltip.show(new Point(0, 0));
                              }

                              @Override
                              public void mouseDoubleClick(MouseEvent e) {
                              }
                            });
                          }

                          final GridData gridData = new GridData();
                          gridData.grabExcessHorizontalSpace = true;
                          gridData.horizontalAlignment = GridData.FILL;
                          headerGroup.setLayoutData(gridData);

                          break;
                        }
                      }

                    }

                    // Empty label for the header of the final column
                    final CLabel label = factory.createCLabel(listParent, "", SWT.WRAP);

                    // Create rows with fields
                    for (int i = 0; i < 5; i++) {
                      for (final Field itemClassField : itemClassFields) {
                        final Annotation[] itemClassFieldAnnotations = itemClassField.getAnnotations();
                        for (final Annotation itemClassFieldAnnotation : itemClassFieldAnnotations) {
                          if (itemClassFieldAnnotation instanceof Property) {
                            final Property itemClassFieldProperty = (Property) itemClassFieldAnnotation;

                            switch (itemClassFieldProperty.type()) {
                            case TEXT:

                              final Text propertyText = factory.createText(listParent, "", SWT.BORDER_SOLID);
                              propertyText.setEnabled(true);

                              if (itemClassFieldProperty.required()) {
                                propertyText.addFocusListener(new FieldValidatorListener(propertyText, RequiredFieldValidator.class));
                              }

                              if (!itemClassFieldProperty.fieldValidator().equals(FieldValidator.class)) {
                                propertyText.addFocusListener(new FieldValidatorListener(propertyText, itemClassFieldProperty.fieldValidator()));
                              }

                              final GridData textGridData = new GridData();
                              textGridData.grabExcessHorizontalSpace = true;
                              textGridData.horizontalAlignment = GridData.FILL;
                              propertyText.setLayoutData(textGridData);
                              break;
                            case MULTILINE_TEXT:
                              break;
                            case PERIOD:
                              final Text periodControl = factory.createText(listParent, "", SWT.BORDER_SOLID);
                              periodControl.setToolTipText("Double-click this field to edit");
                              final String fieldName = itemClassFieldProperty.displayName();
                              periodControl.setEnabled(true);

                              final GridData periodGridData = new GridData();
                              periodGridData.grabExcessHorizontalSpace = true;
                              periodGridData.horizontalAlignment = GridData.FILL;
                              periodControl.setLayoutData(periodGridData);

                              periodControl.addMouseListener(new MouseListener() {

                                @Override
                                public void mouseDoubleClick(MouseEvent e) {
                                  System.out.println("Mouse click in field " + fieldName);
                                  PeriodDialog dialog = new PeriodDialog(parent.getDisplay().getActiveShell(), getHelp(itemClassField), periodControl.getText());
                                  // open dialog and wait for return status
                                  // code.
                                  if (dialog.open() == IStatus.OK) {
                                    // If user clicks ok display message box
                                    String value = dialog.getValue();
                                    periodControl.setText(value);
                                  } else {

                                  }
                                }

                                @Override
                                public void mouseDown(MouseEvent e) {
                                }

                                @Override
                                public void mouseUp(MouseEvent e) {
                                }

                              });

                              break;
                            }

                          }
                        }
                      }

                      final Button propertyHelp = factory.createButton(listParent, "", SWT.BUTTON1);
                      propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
                      propertyHelp.addMouseListener(new MouseListener() {

                        @Override
                        public void mouseUp(MouseEvent e) {
                        }

                        @Override
                        public void mouseDown(MouseEvent e) {
                          System.out.println("Delete command");
                        }

                        @Override
                        public void mouseDoubleClick(MouseEvent e) {
                        }
                      });

                    }

                    final Button addButton = factory.createButton(listParent, "Add item", SWT.BUTTON1);
                    addButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
                    final GridData gridData = new GridData();
                    gridData.horizontalSpan = itemClassFields.size() + 1;
                    addButton.setLayoutData(gridData);

                    createdControl = listParent;
                  }
                }

                break;

              }

              final FieldWrapper wrapper = new FieldWrapper(createdControl, property.type());
              fieldControls.put(field.getName(), wrapper);
              // Only add a focus listener if the created control isn't a
              // composite. Composites will have
              // set listeners for their own child controls
              if (!(createdControl instanceof Composite)) {
                createdControl.addFocusListener(listener);
              }

              previousAnchor = createdControl;

              String displayName = property.displayName();
              if (StringUtils.isBlank(property.displayName())) {
                displayName = field.getName();
              }

              if (property.required()) {
                displayName += PROPERTY_REQUIRED_DISPLAY;
              }

              displayName += ": ";

              final CLabel propertyLabel = factory.createCLabel(workParent, displayName); //$NON-NLS-1$
              data = new FormData();
              data.top = new FormAttachment(createdControl, 0, SWT.TOP);
              data.left = new FormAttachment(0, 0);
              data.right = new FormAttachment(createdControl, -HSPACE);
              propertyLabel.setLayoutData(data);

              final Help help = getHelp(field);
              if (help != null) {
                final Button propertyHelp = factory.createButton(workParent, "", SWT.BUTTON1);
                propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP));

                // create a tooltip
                final ToolTip tooltip = new FormToolTip(propertyHelp, String.format("Help for field %s", property.displayName().equals("") ? field.getName()
                        : property.displayName()), help.displayHelpShort(), help.displayHelpLong());
                tooltip.setHideOnMouseDown(false);

                data = new FormData();
                data.top = new FormAttachment(createdControl, 0, SWT.TOP);
                data.left = new FormAttachment(createdControl, 0);
                propertyHelp.setLayoutData(data);
                propertyHelp.addMouseListener(new MouseListener() {

                  @Override
                  public void mouseUp(MouseEvent e) {
                  }

                  @Override
                  public void mouseDown(MouseEvent e) {
                    tooltip.show(new Point(0, 0));
                  }

                  @Override
                  public void mouseDoubleClick(MouseEvent e) {
                  }
                });
              }
            }
          }
        }
      }
    }

    this.workParent.getParent().getParent().layout(true, true);
  }
  private Composite createPeriodProperty(TabbedPropertySheetWidgetFactory factory, Control previousAnchor, final Property property) {
    FormData data;
    final Composite parent = factory.createFlatFormComposite(workParent);
    data = new FormData();
    data.top = new FormAttachment(previousAnchor, VSPACE);
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(80, 25);
    parent.setLayoutData(data);

    Control previousGroupAnchor = previousAnchor;

    int i = 0;

    PeriodPropertyElement[] properties = PeriodPropertyElement.values();

    for (final PeriodPropertyElement element : properties) {

      final Spinner spinner = new Spinner(parent, SWT.BORDER);

      spinner.setData("PERIOD_KEY", element.getShortFormat());
      data = new FormData();
      data.top = new FormAttachment(previousAnchor, VSPACE);
      data.left = new FormAttachment(previousGroupAnchor);
      data.width = 30;
      spinner.setEnabled(true);
      spinner.setLayoutData(data);

      if (property.required()) {
        spinner.addFocusListener(new FieldValidatorListener(spinner, RequiredFieldValidator.class));
      }

      if (!property.fieldValidator().equals(FieldValidator.class)) {
        spinner.addFocusListener(new FieldValidatorListener(spinner, property.fieldValidator()));
      }

      String labelText = element.getShortFormat();
      if (i != properties.length - 1) {
        labelText += " ,  ";
      }

      CLabel labelShort = factory.createCLabel(parent, labelText, SWT.NONE);

      data = new FormData();
      data.left = new FormAttachment(spinner);
      data.top = new FormAttachment(spinner, 0, SWT.CENTER);
      labelShort.setLayoutData(data);
      labelShort.setToolTipText(element.getLongFormat());

      previousGroupAnchor = labelShort;

      spinner.addFocusListener(listener);

      i++;
    }
    return parent;
  }

  private Text createMultilineTextProperty(TabbedPropertySheetWidgetFactory factory, Control previousAnchor, final Property property) {
    FormData data;
    final Text propertyMultiText = factory.createText(workParent, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER_SOLID);
    data = new FormData();
    data.top = new FormAttachment(previousAnchor, VSPACE);
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(80, 25);
    data.height = 80;
    propertyMultiText.setEnabled(true);
    propertyMultiText.setLayoutData(data);

    if (property.required()) {
      propertyMultiText.addFocusListener(new FieldValidatorListener(propertyMultiText, RequiredFieldValidator.class));
    }

    if (!property.fieldValidator().equals(FieldValidator.class)) {
      propertyMultiText.addFocusListener(new FieldValidatorListener(propertyMultiText, property.fieldValidator()));
    }
    return propertyMultiText;
  }

  private Text createTextProperty(TabbedPropertySheetWidgetFactory factory, Control previousAnchor, final Property property) {
    FormData data;
    final Text propertyText = factory.createText(workParent, "", SWT.BORDER_SOLID);
    data = new FormData();
    data.top = new FormAttachment(previousAnchor, VSPACE);
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(80, 25);
    propertyText.setEnabled(true);
    propertyText.setLayoutData(data);

    if (property.required()) {
      propertyText.addFocusListener(new FieldValidatorListener(propertyText, RequiredFieldValidator.class));
    }

    if (!property.fieldValidator().equals(FieldValidator.class)) {
      propertyText.addFocusListener(new FieldValidatorListener(propertyText, property.fieldValidator()));
    }
    return propertyText;
  }

  private Help getHelp(Field field) {
    return field.getAnnotation(Help.class);
  }

  @Override
  public void refresh() {

    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
    }

    final ServiceTask serviceTask = getServiceTask();

    for (Entry<String, FieldWrapper> entry : fieldControls.entrySet()) {

      switch (entry.getValue().getPropertyType()) {
      case TEXT:
        if (entry.getValue().getControl() instanceof Text) {
          String value = "";
          if (ExtensionUtil.hasCustomProperty(serviceTask, entry.getKey())) {
            CustomProperty property = ExtensionUtil.getCustomProperty(serviceTask, entry.getKey());
            value = property.getSimpleValue();
          }

          ((Text) entry.getValue().getControl()).setText(value);

        }
        break;
      case MULTILINE_TEXT:
        if (entry.getValue().getControl() instanceof Text) {
          String value = "";
          if (ExtensionUtil.hasCustomProperty(serviceTask, entry.getKey())) {
            CustomProperty property = ExtensionUtil.getCustomProperty(serviceTask, entry.getKey());
            value = property.getSimpleValue();
          }

          ((Text) entry.getValue().getControl()).setText(value);

        }
        break;

      case PERIOD:

        if (entry.getValue().getControl() instanceof Composite) {

          Composite periodParent = (Composite) entry.getValue().getControl();

          String value = "";
          if (ExtensionUtil.hasCustomProperty(serviceTask, entry.getKey())) {
            CustomProperty property = ExtensionUtil.getCustomProperty(serviceTask, entry.getKey());
            value = property.getSimpleValue();

            if (StringUtils.isNotEmpty(value)) {

              for (final Control childControl : periodParent.getChildren()) {
                if (childControl instanceof Spinner) {
                  Spinner actualControl = (Spinner) childControl;
                  String periodKey = (String) childControl.getData("PERIOD_KEY");
                  PeriodPropertyElement element = PeriodPropertyElement.byShortFormat(periodKey);
                  if (element != null) {
                    actualControl.setSelection(ExtensionPropertyUtil.getPeriodPropertyElementFromValue(value, element));
                  }
                }
              }
            }
          }
        }
        break;
      }
    }
  }

  private ServiceTask getServiceTask() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo != null && bo instanceof ServiceTask) {
        return (ServiceTask) bo;
      }
    }
    return null;
  }

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo instanceof ServiceTask) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          @SuppressWarnings("restriction")
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {

            public void run() {
              Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
              if (bo == null) {
                return;
              }

              ServiceTask task = (ServiceTask) bo;

              for (Entry<String, FieldWrapper> entry : fieldControls.entrySet()) {

                switch (entry.getValue().getPropertyType()) {
                case TEXT:
                  if (entry.getValue().getControl() instanceof Text) {
                    String value = ((Text) entry.getValue().getControl()).getText();

                    CustomProperty property = null;

                    if (!ExtensionUtil.hasCustomProperty(task, entry.getKey())) {

                      property = Bpmn2Factory.eINSTANCE.createCustomProperty();
                      getDiagram().eResource().getContents().add(property);
                      task.getCustomProperties().add(property);

                    } else {
                      property = ExtensionUtil.getCustomProperty(task, entry.getKey());
                    }

                    property.setId(ExtensionUtil.wrapCustomPropertyId(task, entry.getKey()));
                    property.setName(entry.getKey());
                    property.setSimpleValue(value);

                  }
                  break;
                case MULTILINE_TEXT:
                  if (entry.getValue().getControl() instanceof Text) {
                    String value = ((Text) entry.getValue().getControl()).getText();

                    CustomProperty property = null;

                    if (!ExtensionUtil.hasCustomProperty(task, entry.getKey())) {

                      property = Bpmn2Factory.eINSTANCE.createCustomProperty();
                      getDiagram().eResource().getContents().add(property);
                      task.getCustomProperties().add(property);

                    } else {
                      property = ExtensionUtil.getCustomProperty(task, entry.getKey());
                    }

                    property.setId(ExtensionUtil.wrapCustomPropertyId(task, entry.getKey()));
                    property.setName(entry.getKey());
                    property.setSimpleValue(value);

                  }
                  break;

                case PERIOD:

                  if (entry.getValue().getControl() instanceof Composite) {

                    Composite periodParent = (Composite) entry.getValue().getControl();

                    String value = ExtensionPropertyUtil.getPeriodValueFromParent(periodParent);

                    CustomProperty property = null;

                    if (!ExtensionUtil.hasCustomProperty(task, entry.getKey())) {

                      property = Bpmn2Factory.eINSTANCE.createCustomProperty();
                      getDiagram().eResource().getContents().add(property);
                      task.getCustomProperties().add(property);

                    } else {
                      property = ExtensionUtil.getCustomProperty(task, entry.getKey());
                    }

                    property.setId(ExtensionUtil.wrapCustomPropertyId(task, entry.getKey()));
                    property.setName(entry.getKey());
                    property.setSimpleValue(value);

                  }

                  break;

                }

              }
            }
          }, editingDomain, "Model Update");
        }

      }
    }
  };

}