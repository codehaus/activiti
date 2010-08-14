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

  public abstract String getFormAsHtml();

  public abstract void execute(Map<String, Object> parameter) throws Exception;
}
