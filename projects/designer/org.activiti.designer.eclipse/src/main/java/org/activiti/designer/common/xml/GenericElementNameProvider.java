package org.activiti.designer.common.xml;

import org.activiti.designer.common.model.GenericElement;
import org.activiti.designer.common.model.SemanticElement;

public class GenericElementNameProvider implements XmlAdapterNameProvider {

	public String getName(SemanticElement element) {
		if (!(element instanceof GenericElement)) return null;
		return ((GenericElement)element).getName();
	}

}
