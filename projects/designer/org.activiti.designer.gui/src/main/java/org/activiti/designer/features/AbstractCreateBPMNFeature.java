/**
 * 
 */
package org.activiti.designer.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

/**
 * @author Tiese Barrell
 * @version 1
 * @since 1.0.0
 *
 */
public abstract class AbstractCreateBPMNFeature extends AbstractCreateFeature {
	
	private static final Map<String, Integer> idCache = new HashMap<String, Integer>();
	
	private static final String ID_PATTERN = "%s%s";

	public AbstractCreateBPMNFeature(IFeatureProvider fp, String name, String description) {
		super(fp, name, description);
	}
	
	protected abstract String getFeatureIdKey();
	
	@SuppressWarnings("rawtypes")
	protected abstract Class getFeatureClass();
	
	protected String getNextId() {
		
		int determinedId = -1;
		
		//If there's no id in the cache, assume we haven't loaded the diagram's current ids and proceed to do so
		if (!idCache.containsKey(getFeatureIdKey())) {
			determinedId = 0;
			for(EObject contentObject : getDiagram().eResource().getContents()) {
				if(contentObject.getClass() == getFeatureClass()) {
					BaseElement tempElement = (BaseElement) contentObject;
					String contentObjectId = tempElement.getId().replace(getFeatureIdKey(), "");
					Integer intGatewayNumber = Integer.valueOf(contentObjectId);
					if(intGatewayNumber > determinedId) {
						determinedId = intGatewayNumber;
					}
				}
			}
		} else {
			determinedId = idCache.get(getFeatureIdKey());
		}
		
		determinedId++;
		
		idCache.put(getFeatureIdKey(), determinedId);
		
		return String.format(ID_PATTERN, getFeatureIdKey(), determinedId);
	}

}
