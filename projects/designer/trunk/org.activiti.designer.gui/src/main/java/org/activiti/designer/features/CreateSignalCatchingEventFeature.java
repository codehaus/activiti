package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.IntermediateCatchEvent;
import org.activiti.designer.bpmn2.model.SignalEventDefinition;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateSignalCatchingEventFeature extends AbstractCreateFastBPMNFeature {

  public static final String FEATURE_ID_KEY = "signalintermediatecatchevent";

  public CreateSignalCatchingEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "SignalCatchingEvent", "Add signal intermediate catching event");
  }

  public Object[] create(ICreateContext context) {
    IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
    SignalEventDefinition eventDef = new SignalEventDefinition();
    catchEvent.getEventDefinitions().add(eventDef);
    addObjectToContainer(context, catchEvent, "SignalCatchEvent");

    // return newly created business object(s)
    return new Object[] { catchEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_BOUNDARY_SIGNAL.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
