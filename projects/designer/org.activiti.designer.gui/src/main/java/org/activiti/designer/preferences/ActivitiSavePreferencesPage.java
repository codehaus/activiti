package org.activiti.designer.preferences;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.Preferences;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ActivitiSavePreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    final IConfigurationElement[] marshallerConfiguration = Platform.getExtensionRegistry().getConfigurationElementsFor(
            ActivitiPlugin.EXPORT_MARSHALLER_EXTENSIONPOINT_ID);

    ExportMarshaller bpmnMarshaller = null;
    final List<ExportMarshaller> additionalMarshallers = new ArrayList<ExportMarshaller>();

    try {

      for (final IConfigurationElement e : marshallerConfiguration) {
        final Object o = e.createExecutableExtension("class");
        if (o instanceof ExportMarshaller) {
          final ExportMarshaller exportMarshaller = (ExportMarshaller) o;

          if (StringUtils.equals(exportMarshaller.getMarshallerName(), ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME)) {
            bpmnMarshaller = exportMarshaller;
          } else {
            additionalMarshallers.add(exportMarshaller);
          }
        }
      }
    } catch (CoreException ex) {
      ex.printStackTrace();
    }

    if (bpmnMarshaller != null) {
      Group group = new Group(getFieldEditorParent(), SWT.BORDER);
      group.setText("Activiti BPMN 2.0");
      group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

      addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(bpmnMarshaller), "Automatically save to Activiti &BPMN 2.0 format when saving diagrams",
              group));

      addField(new BooleanFieldEditor(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT.getPreferenceId(),
              "&Validate diagram before saving to Activiti BPMN 2.0 format", group));

      addField(new RadioGroupFieldEditor(Preferences.SKIP_BPMN_MARSHALLER_ON_VALIDATION_FAILURE.getPreferenceId(), "Handling validation failures", 1,
              new String[][] { { "Skip saving to BPMN 2.0 format if validation fails", ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_SKIP },
                  { "Attempt to save to BPMN 2.0 format anyway if validation fails", ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_ATTEMPT } }, group));
    }

    if (additionalMarshallers.size() > 0) {
      Group group = new Group(getFieldEditorParent(), SWT.BORDER);
      group.setText("Additional Export formats");
      group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

      for (final ExportMarshaller exportMarshaller : additionalMarshallers) {
        addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(exportMarshaller), "Automatically save to " + exportMarshaller.getFormatName()
                + " format when saving diagrams", group));
      }
    }

  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Set preferences used while saving Activiti Diagrams");
    setTitle("Activiti Designer Save Preferences");
  }

}
