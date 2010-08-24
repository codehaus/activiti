package org.activiti.cycle.impl.plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.cycle.impl.connector.demo.DemoConnectorPluginDefinition;
import org.activiti.cycle.impl.connector.fs.FileSystemPluginDefinition;
import org.activiti.cycle.impl.connector.signavio.SignavioPluginDefinition;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

/**
 * Finder for Activiti Cycle Plugins based on finding annotations. The <a href=
 * "http://bill.burkecentral.com/2008/01/14/scanning-java-annotations-at-runtime/"
 * >Scannotation</a> library from bill Burke is used for that.
 * 
 * @author ruecker
 */
public class PluginFinder {
  
  protected Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * main method to find all existing plugins in the classpath and register them
   * in the {@link ActivitiCyclePluginRegistry}
   */
  public void publishAllPluginsToRegistry() {
    List<Class< ? extends ActivitiCyclePluginDefinition>> definitions = findPluginDefinitionClasses();
    eliminateOverwrittenPlugins(definitions);
    
    publishDefinitionsToRegistry(definitions);
  }
  
  @SuppressWarnings("unchecked")
  private List<Class< ? extends ActivitiCyclePluginDefinition>> findPluginDefinitionClasses() {
    List<Class<? extends ActivitiCyclePluginDefinition>> result = new ArrayList<Class<? extends ActivitiCyclePluginDefinition>>();
    try {
      URL[] urls = ClasspathUrlFinder.findClassPaths();
      AnnotationDB db = new AnnotationDB();
      db.scanArchives(urls);

      Set<String> connectors = db.getAnnotationIndex().get(ActivitiCyclePlugin.class.getName());
      
      if (connectors == null) {
        // seems we currently have a classloading problem in the webapp
        result.add(SignavioPluginDefinition.class);
        result.add(FileSystemPluginDefinition.class);
        result.add(DemoConnectorPluginDefinition.class);
      } else {
        for (String pluginClass : connectors) {
          Class< ? > cyclePluginClass = this.getClass().getClassLoader().loadClass(pluginClass);
          if (ActivitiCyclePluginDefinition.class.isAssignableFrom(cyclePluginClass)) {
            logger.log(Level.INFO, "Found Activiti Cycle plugin class: " + cyclePluginClass.getName());
            result.add((Class< ? extends ActivitiCyclePluginDefinition>) cyclePluginClass);
          } else {
            logger.log(Level.SEVERE, "Found plugin class with " + ActivitiCyclePlugin.class.getSimpleName() + " annotation but without implementing the "
                    + ActivitiCyclePluginDefinition.class.getSimpleName() + " interface");
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  /**
   * Remove PluginDefinitions that are overwritten by another one
   */
  private void eliminateOverwrittenPlugins(List<Class< ? extends ActivitiCyclePluginDefinition>> definitions) {
    List<Class< ? extends ActivitiCyclePluginDefinition>> definitionsForRemoval = new ArrayList<Class< ? extends ActivitiCyclePluginDefinition>>();
    for (Class< ? extends ActivitiCyclePluginDefinition> pluginClass1 : definitions) {
      for (Class< ? extends ActivitiCyclePluginDefinition> pluginClass2 : definitions) {
        if (pluginClass1 != pluginClass2 && pluginClass1.isAssignableFrom(pluginClass2)) {
          logger.log(Level.INFO, "remove plugin definition " + pluginClass1.getName() + " because it was overwritten by " + pluginClass2.getName());
          definitionsForRemoval.add(pluginClass1);
        }
      }
    }
    
    for (Class< ? extends ActivitiCyclePluginDefinition> pluginClass : definitionsForRemoval) {
      definitions.remove(pluginClass);
    }
  }

  private void publishDefinitionsToRegistry(List<Class< ? extends ActivitiCyclePluginDefinition>> definitions) {
    for (Class< ? extends ActivitiCyclePluginDefinition> pluginDefinitionClass : definitions) {
      try {
        ActivitiCyclePluginDefinition pluginDefinition = pluginDefinitionClass.newInstance();
        ActivitiCyclePluginRegistry.addPluginDefinition(pluginDefinition);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error while reading plugin configuration from class " + pluginDefinitionClass, e);
      }
    }
  }

  public static void main(String[] args) {
    new PluginFinder().findPluginDefinitionClasses();
  }

}
