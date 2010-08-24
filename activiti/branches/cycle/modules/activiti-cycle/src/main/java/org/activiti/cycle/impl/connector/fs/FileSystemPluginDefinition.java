package org.activiti.cycle.impl.connector.fs;

import java.util.List;

import org.activiti.cycle.ArtifactAction;
import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentationProvider;
import org.activiti.cycle.impl.connector.fs.provider.FileSystemBinaryProvider;
import org.activiti.cycle.impl.connector.fs.provider.FileSystemTextProvider;
import org.activiti.cycle.impl.connector.fs.provider.FileSystemXmlProvider;
import org.activiti.cycle.impl.plugin.ActivitiCyclePlugin;
import org.activiti.cycle.impl.plugin.ActivitiCyclePluginDefinition;
import org.activiti.cycle.impl.plugin.DefinitionEntry;

@ActivitiCyclePlugin
public class FileSystemPluginDefinition implements ActivitiCyclePluginDefinition {

  public void addDefinedArtifactTypeToList(List<ArtifactType> list) {
    list.add(new ArtifactType("Bpmn 2.0 Xml", FileSystemConnector.BPMN_20_XML));
    list.add(new ArtifactType("Xml", FileSystemConnector.XML));
    list.add(new ArtifactType("Text", FileSystemConnector.TEXT));
    list.add(new ArtifactType("Ms Word", FileSystemConnector.MS_WORD));
    list.add(new ArtifactType("Ms Word X", FileSystemConnector.MS_WORD_X));
    list.add(new ArtifactType("Ms Powerpoint", FileSystemConnector.MS_PP));
    list.add(new ArtifactType("Ms Powerpoint X", FileSystemConnector.MS_PP_X));    
  }
  
  public void addContentRepresentationProviderToMap(List<DefinitionEntry<Class< ? extends ContentRepresentationProvider>>> contentProviderMap) {
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.BPMN_20_XML, FileSystemXmlProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.XML, FileSystemXmlProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.TEXT, FileSystemTextProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.MS_WORD, FileSystemBinaryProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.MS_WORD_X, FileSystemBinaryProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.MS_PP, FileSystemBinaryProvider.class));
    contentProviderMap.add(new DefinitionEntry<Class< ? extends ContentRepresentationProvider>>(FileSystemConnector.MS_PP_X, FileSystemBinaryProvider.class));
  }

  public void addArtifactActionToMap(List<DefinitionEntry<Class< ? extends ArtifactAction>>> actionMap) {

  }
}
