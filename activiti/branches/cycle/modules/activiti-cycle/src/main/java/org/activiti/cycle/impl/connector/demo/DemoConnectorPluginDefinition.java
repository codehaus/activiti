package org.activiti.cycle.impl.connector.demo;

import java.util.List;
import java.util.Map;

import org.activiti.cycle.ArtifactAction;
import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentationProvider;
import org.activiti.cycle.impl.connector.demo.action.CopyArtifactAction;
import org.activiti.cycle.impl.connector.demo.action.OpenActivitiAction;
import org.activiti.cycle.impl.connector.demo.provider.DemoImageProvider;
import org.activiti.cycle.impl.connector.demo.provider.DemoTextProvider;
import org.activiti.cycle.impl.connector.demo.provider.DemoXmlProvider;
import org.activiti.cycle.impl.plugin.ActivitiCyclePlugin;
import org.activiti.cycle.impl.plugin.ActivitiCyclePluginDefinition;

@ActivitiCyclePlugin
public class DemoConnectorPluginDefinition implements ActivitiCyclePluginDefinition {

  public void addDefinedArtifactTypeToList(List<ArtifactType> list) {
    list.add(new ArtifactType(DemoConnector.ARTIFACT_TYPE_TEXT, DemoConnector.ARTIFACT_TYPE_TEXT));
    list.add(new ArtifactType(DemoConnector.ARTIFACT_TYPE_MINDMAP, DemoConnector.ARTIFACT_TYPE_MINDMAP));
    list.add(new ArtifactType(DemoConnector.ARTIFACT_TYPE_BPMN_20, DemoConnector.ARTIFACT_TYPE_BPMN_20));
  }
    
  public void addContentRepresentationProviderToMap(Map<String, Class< ? extends ContentRepresentationProvider>> contentProviderMap) {
    contentProviderMap.put(DemoConnector.ARTIFACT_TYPE_TEXT, DemoTextProvider.class);
    contentProviderMap.put(DemoConnector.ARTIFACT_TYPE_MINDMAP, DemoImageProvider.class);
    contentProviderMap.put(DemoConnector.ARTIFACT_TYPE_BPMN_20, DemoImageProvider.class);
    contentProviderMap.put(DemoConnector.ARTIFACT_TYPE_BPMN_20, DemoXmlProvider.class);
  }

  public void addArtifactActionToMap(Map<String, Class< ? extends ArtifactAction>> actionMap) {
    // and register demo actions (skip Mindmap to see a difference)
    actionMap.put(DemoConnector.ARTIFACT_TYPE_TEXT, CopyArtifactAction.class);
    actionMap.put(DemoConnector.ARTIFACT_TYPE_TEXT, OpenActivitiAction.class);
    // RepositoryRegistry.registerArtifactAction(DemoConnector.ARTIFACT_TYPE_MINDMAP,
    // CopyArtifactAction.class);
    // RepositoryRegistry.registerArtifactAction(DemoConnector.ARTIFACT_TYPE_MINDMAP,
    // OpenActivitiAction.class);
    actionMap.put(DemoConnector.ARTIFACT_TYPE_BPMN_20, CopyArtifactAction.class);
    actionMap.put(DemoConnector.ARTIFACT_TYPE_BPMN_20, OpenActivitiAction.class);
  }
}
