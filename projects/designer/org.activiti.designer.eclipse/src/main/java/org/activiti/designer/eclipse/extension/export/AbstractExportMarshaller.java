/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.extension.AbstractDiagramWorker;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Base class for {@link ExportMarshaller} implementations.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 2
 * 
 */
public abstract class AbstractExportMarshaller extends AbstractDiagramWorker implements ExportMarshaller {

  /**
   * Invokes validators marked by the provided validatorIds. If no validator is
   * registered by one of the validatorIds, that validator is skipped.
   * 
   * TODO: monitor progress
   * 
   * @param validatorIds
   *          the list of ids of the validators to invoke
   * @return true if *all* of the validators completed successfully or false
   *         otherwise
   */
  protected boolean invokeValidators(final List<String> validatorIds, final Diagram diagram, final IProgressMonitor monitor) {

    monitor.subTask("Invoking validators");

    boolean overallResult = true;

    if (validatorIds.size() > 0) {

      monitor.worked(20);

      for (final String validatorId : validatorIds) {

        // get validator, else skip
        final ProcessValidator processValidator = ExtensionPointUtil.getProcessValidator(validatorId);

        if (processValidator != null) {

          monitor.beginTask("Invoking " + processValidator.getValidatorName(), 10);

          if (!(processValidator.validateDiagram(diagram, monitor))) {
            // don't break even if one result is false: keep validating to get
            // all
            // of the problems
            overallResult = false;
          }
          monitor.worked(10);
        }
      }
      monitor.worked(80);
    }

    monitor.done();
    return overallResult;
  }

  /**
   * Invokes validator marked by the provided validatorId. If no validator is
   * registered by the validatorId, that validator is skipped.
   * 
   * @param validatorId
   *          the id of the validator to invoke
   * @return true if the validator completed successfully or false otherwise
   */
  protected boolean invokeValidator(final String validatorId, final Diagram diagram, final IProgressMonitor monitor) {
    final List<String> validatorIds = new ArrayList<String>();
    validatorIds.add(validatorId);
    return invokeValidators(validatorIds, diagram, monitor);
  }

}
