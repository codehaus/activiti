package org.activiti.designer.common.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class SemanticElementRegistry {

	private Map<String, IConfigurationElement> elementNameMap = new HashMap<String, IConfigurationElement>();
	private Map elementIdMap = new HashMap();
	
	public SemanticElementRegistry(String language) {
		initializeMaps(language);
	}
	
	private void initializeMaps(String language) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.activiti.designer.eclipse.semanticElements");
		IExtension[] extensions = extensionPoint.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				if (!language.equals(configElements[j].getAttribute("language"))) continue;
				String elementName = configElements[j].getAttribute("name");
				if (elementName == null) {
					elementName = configElements[j].getAttribute("id");
				}
				String elementId = configElements[j].getAttribute("id");
				elementNameMap.put(elementName, configElements[j]);
				elementIdMap.put(elementId, configElements[j]);
			}
		}		
	}
	
	public IConfigurationElement getConfigurationElementByName(String elementName) {
		return (IConfigurationElement)elementNameMap.get(elementName);
	}
	
	public IConfigurationElement getConfigurationElementById(String id) {
		return (IConfigurationElement)elementIdMap.get(id);
	}
	
	Set getSemanticElementIds() {
		return elementIdMap.keySet();
	}
	
}
