package org.activiti.cycle;

import java.util.Map;

/**
 * Base class for actions requiring paremeters which must be displayed in an own
 * gui form element.
 * 
 * The form element must be returned as HTML and the paremeters posted are
 * handed in as {@link Map} from the GUI.
 * 
 * Normally best is to use the {@link ParametrizedFreemakerTemplateAction} which
 * has already built in support for generating HTML forms via freemarker
 * templates as it is done already in the Task-Form-GUI
 * 
 * @author ruecker
 */
public abstract class ParametrizedAction extends ArtifactAction {

  public ParametrizedAction() {
  }
  
  public ParametrizedAction(RepositoryArtifact artifact) {
    super(artifact);
  }

  /**
   * returns html form for the action parameters as String. Returns null if no
   * form is needed
   * 
   * TODO: Think about Actions without parameters (Return null or create own
   * superclass)
   */
  public abstract String getFormAsHtml();

  public abstract void execute(Map<String, Object> parameter) throws Exception;
  
  public Object getParameter(Map<String, Object> parameters, String name, boolean required, Object defaultValue, Class expectedClass) {
    Object value = parameters.get(name);
    if (value == null || (value instanceof String && ((String) value).length() == 0)) {
      if (required) {
        throw new RepositoryException("Required parameter '" + name + "' not set while executing action '" + getName() + "'");
      } else {
        return defaultValue;
      }
    }
    if (expectedClass != null && !expectedClass.isAssignableFrom(value.getClass())) {
      throw new RepositoryException("Parameter '" + name + "' with value '" + value + "' has wrong type " + value.getClass() + " instead of " + expectedClass
              + " not set while executing action '" + getName() + "'");
    }
    return value;
  }
  
}
