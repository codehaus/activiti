package org.activiti.cycle.impl.connector.signavio;

import java.util.List;
import java.util.Map;

import org.activiti.cycle.ArtifactAction;
import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentationProvider;
import org.activiti.cycle.impl.connector.signavio.action.OpenModelerAction;
import org.activiti.cycle.impl.connector.signavio.provider.Bpmn20Provider;
import org.activiti.cycle.impl.connector.signavio.provider.EmbeddableModelProvider;
import org.activiti.cycle.impl.connector.signavio.provider.Jpdl4Provider;
import org.activiti.cycle.impl.connector.signavio.provider.JsonProvider;
import org.activiti.cycle.impl.connector.signavio.provider.PngProvider;
import org.activiti.cycle.impl.plugin.ActivitiCyclePlugin;
import org.activiti.cycle.impl.plugin.ActivitiCyclePluginDefinition;

@ActivitiCyclePlugin
public class SignavioPluginDefinition implements ActivitiCyclePluginDefinition {

  public void addDefinedArtifactTypeToList(List<ArtifactType> list) {
    list.add(new ArtifactType("Signavio BPMN 2.0", SignavioConnector.SIGNAVIO_BPMN_2_0));
    list.add(new ArtifactType("Signavio BPMN for jBPM 4", SignavioConnector.SIGNAVIO_BPMN_JBPM4));
  }

  public void addContentRepresentationProviderToMap(Map<String, Class< ? extends ContentRepresentationProvider>> contentProviderMap) {
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, Bpmn20Provider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, Bpmn20Provider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, JsonProvider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, PngProvider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, EmbeddableModelProvider.class);

    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_JBPM4, JsonProvider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_JBPM4, Jpdl4Provider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_JBPM4, PngProvider.class);
    contentProviderMap.put(SignavioConnector.SIGNAVIO_BPMN_JBPM4, EmbeddableModelProvider.class);
  }

  public void addArtifactActionToMap(Map<String, Class< ? extends ArtifactAction>> actionMap) {
    actionMap.put(SignavioConnector.SIGNAVIO_BPMN_2_0, OpenModelerAction.class);
    actionMap.put(SignavioConnector.SIGNAVIO_BPMN_JBPM4, OpenModelerAction.class);
    
    // TODO: Retrieve model through modellink (without /info) and dynamically
    // initialize RepositoryRegistry with supported formats?
  }
}
