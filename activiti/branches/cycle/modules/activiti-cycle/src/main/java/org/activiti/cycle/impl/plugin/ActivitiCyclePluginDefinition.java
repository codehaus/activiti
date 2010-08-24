package org.activiti.cycle.impl.plugin;

import java.util.List;
import java.util.Map;

import org.activiti.cycle.ArtifactAction;
import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentationProvider;

/**
 * Interface for any plugin provider. Each plugin needs a definition class
 * implementing that interface to provide informations about the internals
 * 
 * @author ruecker
 */
public interface ActivitiCyclePluginDefinition {
  
  public void addDefinedArtifactTypeToList(List<ArtifactType> list);

  public void addContentRepresentationProviderToMap(Map<String, Class< ? extends ContentRepresentationProvider>> contentProviderMap);

  public void addArtifactActionToMap(Map<String, Class< ? extends ArtifactAction>> actionMap);
}
