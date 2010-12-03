/**
 * 
 */
package org.activiti.designer.util;

import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.property.extension.ExtensionUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public final class CloneUtil {

  private CloneUtil() {

  }

  public static FlowElement clone(final FlowElement element, final Diagram diagram) {

    if (element instanceof ServiceTask) {
      return clone((ServiceTask) element, diagram);
    }

    return null;

  }

  /**
   * Clones a {@link ServiceTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ServiceTask clone(final ServiceTask original, final Diagram diagram) {

    ServiceTask result = Bpmn2Factory.eINSTANCE.createServiceTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateServiceTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    result.setImplementation(original.getImplementation());

    for (CustomProperty property : original.getCustomProperties()) {
      final CustomProperty clone = clone(property, diagram);
      // Reset the id
      clone.setId(ExtensionUtil.wrapCustomPropertyId(result, ExtensionUtil.upWrapCustomPropertyId(clone.getId())));
      diagram.eResource().getContents().add(clone);
      result.getCustomProperties().add(clone);
    }

    diagram.eResource().getContents().add(result);

    return result;

  }

  /**
   * Clones a {@link CustomProperty}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final CustomProperty clone(final CustomProperty original, final Diagram diagram) {

    CustomProperty result = Bpmn2Factory.eINSTANCE.createCustomProperty();

    result.setId(original.getId());
    if (original.getComplexValue() != null) {
      result.setComplexValue(original.getComplexValue().clone());
    }
    result.setName(original.getName());
    result.setSimpleValue(original.getSimpleValue());

    return result;

  }

}
