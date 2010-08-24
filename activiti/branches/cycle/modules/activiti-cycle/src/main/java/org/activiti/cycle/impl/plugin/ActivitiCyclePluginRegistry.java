/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cycle.impl.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.cycle.ArtifactAction;
import org.activiti.cycle.ArtifactType;
import org.activiti.cycle.ContentRepresentationProvider;

/**
 * Central class to register {@link ArtifactType}s, associated
 * {@link ArtifactAction}s and {@link ContentRepresentationProvider}s.
 * 
 * @author bernd.ruecker@camunda.com
 */
public class ActivitiCyclePluginRegistry {

  private static Map<String, ArtifactType> registeredArtifactTypes = new HashMap<String, ArtifactType>();
  private static Map<String, List<Class< ? extends ArtifactAction>>> registeredArtifactActions = new HashMap<String, List<Class< ? extends ArtifactAction>>>();
  private static Map<String, List<Class< ? extends ContentRepresentationProvider>>> registeredContentRepresentationProviders = new HashMap<String, List<Class< ? extends ContentRepresentationProvider>>>();

  private static Boolean initialized = Boolean.FALSE;
  
  static {
    loadPlugins();
  }
  
  /**
   * TODO: Think about lazy loading when requested instead of static block
   */
  public static void checkInitialized() {
    if (!initialized) {
      synchronized (initialized) {
        if (!initialized) {
          loadPlugins();
          initialized = Boolean.TRUE;
        }
      }
    }
  }

  private static void loadPlugins() {
    new PluginFinder().publishAllPluginsToRegistry();
  }

  public static void addPluginDefinition(ActivitiCyclePluginDefinition definition) {
    List<ArtifactType> list = new ArrayList<ArtifactType>();
    definition.addDefinedArtifactTypeToList(list);
    for (ArtifactType artifactType : list) {
      registerArtifactType(artifactType);
    }

    Map<String, Class< ? extends ContentRepresentationProvider>> contentProviderMap = new HashMap<String, Class< ? extends ContentRepresentationProvider>>();
    definition.addContentRepresentationProviderToMap(contentProviderMap);
    for (Entry<String, Class< ? extends ContentRepresentationProvider>> entry : contentProviderMap.entrySet()) {
      registerContentRepresentationProvider(entry.getKey(), entry.getValue());
    }

    Map<String, Class< ? extends ArtifactAction>> actionMap = new HashMap<String, Class< ? extends ArtifactAction>>();
    definition.addArtifactActionToMap(actionMap);
    for (Entry<String, Class< ? extends ArtifactAction>> entry : actionMap.entrySet()) {
      registerArtifactAction(entry.getKey(), entry.getValue());
    }
  }

  private static void registerArtifactType(ArtifactType ft) {
    registeredArtifactTypes.put(ft.getTypeIdentifier(), ft);
  }

  public static Collection<String> getArtifactTypeIdentifiers() {
    return registeredArtifactTypes.keySet();
  }

  public static ArtifactType getArtifactTypeByIdentifier(String fileTypeIdentifier) {
    return registeredArtifactTypes.get(fileTypeIdentifier);
  }

  private static void registerContentRepresentationProvider(String fileTypeIdentifier, Class< ? extends ContentRepresentationProvider> provider) {
    if (registeredContentRepresentationProviders.containsKey(fileTypeIdentifier)) {
      registeredContentRepresentationProviders.get(fileTypeIdentifier).add(provider);
    } else {
      ArrayList<Class< ? extends ContentRepresentationProvider>> list = new ArrayList<Class< ? extends ContentRepresentationProvider>>();
      list.add(provider);
      registeredContentRepresentationProviders.put(fileTypeIdentifier, list);
    }
  }

  private static void registerArtifactAction(String artifactTypeIdentifier, Class< ? extends ArtifactAction> action) {
    if (registeredArtifactActions.containsKey(artifactTypeIdentifier)) {
      registeredArtifactActions.get(artifactTypeIdentifier).add(action);
    } else {
      ArrayList<Class< ? extends ArtifactAction>> list = new ArrayList<Class< ? extends ArtifactAction>>();
      list.add(action);
      registeredArtifactActions.put(artifactTypeIdentifier, list);
    }
  }

  public static List<Class< ? extends ArtifactAction>> getArtifactAction(String artifactTypeName) {
    if (registeredArtifactActions.containsKey(artifactTypeName)) {
      return registeredArtifactActions.get(artifactTypeName);
    } else {
      return new ArrayList<Class< ? extends ArtifactAction>>();
    }
  }

  public static List<Class< ? extends ContentRepresentationProvider>> getContentRepresentationProviders(String fileTypeName) {
    if (registeredContentRepresentationProviders.containsKey(fileTypeName)) {
      return registeredContentRepresentationProviders.get(fileTypeName);
    } else {
      return new ArrayList<Class< ? extends ContentRepresentationProvider>>();
    }
  }

}
