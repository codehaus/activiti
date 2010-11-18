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
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.validator.FieldValidator;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.extension.ExtensionUtil;
import org.activiti.designer.property.extension.FieldValidatorListener;
import org.activiti.designer.property.extension.FieldWrapper;
import org.activiti.designer.property.extension.FormToolTip;
import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.ServiceTask;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyCustomServiceTaskSection extends GFPropertySection implements ITabbedPropertyConstants {

	private List<CustomServiceTask> customServiceTasks;

	private Map<String, FieldWrapper> fieldControls;

	private Composite parent;
	private Composite workParent;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
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

		fieldControls = new HashMap<String, FieldWrapper>();

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

		if (workParent != null) {
			workParent.dispose();
		}

		workParent = factory.createFlatFormComposite(parent);
		FormData data;

		customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

		final ServiceTask serviceTask = getServiceTask();

		if (serviceTask != null) {

			CustomServiceTask targetTask = null;

			for (final CustomServiceTask customServiceTask : customServiceTasks) {
				if (ExtensionUtil.unwrapCustomId(serviceTask.getImplementation()).equals(customServiceTask.getId())) {
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

				CLabel labelNodeName = factory.createCLabel(workParent, targetTask.getName(), SWT.NONE);

				data = new FormData();
				data.top = new FormAttachment(previousAnchor, VSPACE);
				labelNodeName.setLayoutData(data);

				labelNodeName
						.setFont(new Font(labelNodeName.getFont().getDevice(), new FontData("name", 10, SWT.BOLD)));

				previousAnchor = labelNodeName;

				if (targetTask.getClass().isAnnotationPresent(Help.class)) {
					final Help helpAnnotation = targetTask.getClass().getAnnotation(Help.class);

					CLabel labelShort = factory.createCLabel(workParent, helpAnnotation.displayHelpShort(), SWT.NONE);

					data = new FormData();
					data.top = new FormAttachment(previousAnchor, VSPACE);
					labelShort.setLayoutData(data);

					labelShort.setFont(new Font(labelShort.getFont().getDevice(), new FontData("helpShort", 10,
							SWT.ITALIC)));

					previousAnchor = labelShort;

					CLabel labelLong = factory.createCLabel(workParent, helpAnnotation.displayHelpLong(), SWT.NONE);
					data = new FormData();
					data.top = new FormAttachment(previousAnchor);
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

							case STRING:
								final Text propertyText = factory.createText(workParent, "", SWT.BORDER_SOLID);
								data = new FormData();
								data.top = new FormAttachment(previousAnchor, VSPACE);
								data.left = new FormAttachment(0, 200);
								data.right = new FormAttachment(80, 25);
								propertyText.setEnabled(true);
								propertyText.setLayoutData(data);

								if (property.required()) {
									propertyText.addFocusListener(new FieldValidatorListener(propertyText,
											RequiredFieldValidator.class));
								}

								if (!property.fieldValidator().equals(FieldValidator.class)) {
									propertyText.addFocusListener(new FieldValidatorListener(propertyText, property
											.fieldValidator()));
								}

								createdControl = propertyText;

								break;

							case CALENDAR:

								final DateTime propertyCalendar = new DateTime(workParent, SWT.CALENDAR);
								data = new FormData();
								data.top = new FormAttachment(previousAnchor, VSPACE);
								data.left = new FormAttachment(0, 200);
								data.right = new FormAttachment(25, 0);
								propertyCalendar.setEnabled(true);
								propertyCalendar.setLayoutData(data);
								createdControl = propertyCalendar;

								break;

							}

							final FieldWrapper wrapper = new FieldWrapper(createdControl, property.type());
							fieldControls.put(field.getName(), wrapper);
							createdControl.addFocusListener(listener);

							previousAnchor = createdControl;

							final CLabel propertyLabel = factory.createCLabel(workParent, property.displayName()
									.equals("") ? field.getName() + ":" : property.displayName() + ":"); //$NON-NLS-1$
							data = new FormData();
							data.top = new FormAttachment(createdControl, 0, SWT.CENTER);
							data.left = new FormAttachment(0, 0);
							data.right = new FormAttachment(createdControl, -HSPACE);
							propertyLabel.setLayoutData(data);

							final Help help = getHelp(field);
							if (help != null) {
								final Button propertyHelp = factory.createButton(workParent, "", SWT.BUTTON1);
								propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages()
										.getImage(ISharedImages.IMG_LCL_LINKTO_HELP));

								// create a tooltip
								final ToolTip tooltip = new FormToolTip(propertyHelp, String.format(
										"Help for field %s", property.displayName().equals("") ? field.getName()
												: property.displayName()), help.displayHelpShort(),
										help.displayHelpLong());
								tooltip.setHideOnMouseDown(false);

								data = new FormData();
								data.top = new FormAttachment(createdControl, 0, SWT.CENTER);
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
			case STRING:
				if (entry.getValue().getControl() instanceof Text) {
					String value = "default value";
					if (hasCustomProperty(serviceTask, entry.getKey())) {
						CustomProperty property = getCustomProperty(serviceTask, entry.getKey());
						value = property.getSimpleValue();
					}

					((Text) entry.getValue().getControl()).setText(value);

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
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(
									getSelectedPictogramElement());
							if (bo == null) {
								return;
							}

							ServiceTask task = (ServiceTask) bo;

							for (Entry<String, FieldWrapper> entry : fieldControls.entrySet()) {

								switch (entry.getValue().getPropertyType()) {
								case STRING:
									if (entry.getValue().getControl() instanceof Text) {
										String value = ((Text) entry.getValue().getControl()).getText();

										CustomProperty property = null;

										if (!hasCustomProperty(task, entry.getKey())) {

											property = Bpmn2Factory.eINSTANCE.createCustomProperty();
											getDiagram().eResource().getContents().add(property);
											task.getCustomProperties().add(property);

										} else {
											property = getCustomProperty(task, entry.getKey());
										}

										property.setId(task.getId() + ":" + entry.getKey());
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

	/**
	 * Gets the {@link CustomProperty} identified by the provided name from the
	 * serviceTask.
	 * 
	 * @param serviceTask
	 *            the servicetask that holds the custom property
	 * @param propertyName
	 *            the name of the property
	 * @return the {@link CustomProperty} found or null if no property was found
	 */
	private CustomProperty getCustomProperty(final ServiceTask serviceTask, final String propertyName) {

		CustomProperty result = null;

		if (hasCustomProperty(serviceTask, propertyName)) {
			for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
				if (propertyName.equals(customProperty.getName())) {
					result = customProperty;
				}
			}
		}
		return result;
	}

	/**
	 * Determines whether the {@link CustomProperty} identified by the provided
	 * name is held by the serviceTask.
	 * 
	 * @param serviceTask
	 *            the servicetask that holds the custom property
	 * @param propertyName
	 *            the name of the property
	 * @return true if the serviceTask has the property, false otherwise
	 */
	private boolean hasCustomProperty(final ServiceTask serviceTask, final String propertyName) {
		boolean result = false;
		for (final CustomProperty customProperty : serviceTask.getCustomProperties()) {
			if (propertyName.equals(customProperty.getName())) {
				result = true;
			}
		}

		return result;
	}

}