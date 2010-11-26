package org.activiti.designer.common.model;


public interface NamedElement extends SemanticElement {

	public void setName(String newName);
	public String getName();
	public boolean isNameMandatory();

}
