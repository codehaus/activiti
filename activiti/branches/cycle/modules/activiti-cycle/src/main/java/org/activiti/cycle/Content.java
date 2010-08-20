package org.activiti.cycle;

import java.io.InputStream;

/**
 * This class encapuslates real content (text, images, ...).
 * 
 * It has a refernce to the {@link ContentRepresentationDefinition}, which knows
 * the type and name of the content (if needed)
 * 
 * Depending on the source or usage of the content, you may have strings, byte
 * arrays, streams, ... This class keeps track of the necessary transformations.
 * 
 * @author bernd.ruecker@camunda.com
 */
public class Content {

  private ContentRepresentationDefinition contentRepresentationDefinition;
  
  private byte[] contentAsByteArray;

  private InputStream contentAsInputStream;

  
   public byte[] getContentAsByteArray() {
    if (contentAsByteArray != null) {
      return contentAsByteArray;
    } else {
      throw new RuntimeException("Not yet implemented");
    }
  }

  public String getContentAsString() {
    if (contentAsByteArray != null) {
      return new String(contentAsByteArray);
    } else {
      throw new RuntimeException("Not yet implemented");
    }
  }

  public void setValue(String text) {
    this.contentAsByteArray = text.getBytes();
  }

  public void setValue(byte[] content) {
    this.contentAsByteArray = content;
  }

  public void setValue(InputStream stream) {
    this.contentAsInputStream = stream;
  }
  
  public ContentRepresentationDefinition getContentRepresentationDefinition() {
    return contentRepresentationDefinition;
  }

  public void setContentRepresentationDefinition(ContentRepresentationDefinition contentRepresentationDefinition) {
    this.contentRepresentationDefinition = contentRepresentationDefinition;
  }
}
