/**
 * 
 */
package org.activiti.designer.features;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * @author a139923
 * 
 */
public class CreateCustomServiceTaskFeature extends CreateServiceTaskFeature {

	public CreateCustomServiceTaskFeature(IFeatureProvider fp, String name, String description,
			String customServiceTaskName) {
		super(fp, name, description, customServiceTaskName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.activiti.designer.features.CreateServiceTaskFeature#getCreateImageId
	 * ()
	 */
	@Override
	public String getCreateImageId() {
		// TODO Auto-generated method stub
		return super.getCreateImageId();
	}

}
