package org.activiti.designer.features;

import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;

public class AddUserTaskFeature extends AddTaskFeature {

  public AddUserTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  protected String getIcon(Object bo) {
    return PluginImage.IMG_USERTASK.getImageKey();
  }
}
