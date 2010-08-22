package org.activiti.cycle;

import java.io.InputStream;

/**
 * This class encapsulates real content (text, images, ...).
 * 
 * 
 * Depending on the source or usage of the content, you may have strings, byte
 * arrays, streams, ... This class keeps track of the necessary transformations.
 * 
 * @author bernd.ruecker@camunda.com
 */
public class Content {

  // * It has a refernce to the {@link ContentRepresentationDefinition}, which
  // knows
  // * the type and name of the content (if needed)
  // private ContentRepresentationDefinition contentRepresentationDefinition;
  
  private byte[] contentAsByteArray;

  private String contentAsString;

  private InputStream contentAsInputStream;

  
   public byte[] asByteArray() {
    if (contentAsByteArray != null) {
      return contentAsByteArray;
    } else {
      throw new RuntimeException("Not yet implemented");
    }
  }

  public String asString() {
    if (contentAsString != null) {
      return contentAsString;
    } else if (contentAsByteArray != null) {
      return new String(contentAsByteArray);
    } else {
      throw new RuntimeException("Not yet implemented");
    }
  }

  public void setValue(String text) {
    this.contentAsString = text;
  }

  public void setValue(byte[] content) {
    this.contentAsByteArray = content;
  }

  public void setValue(InputStream stream) {
    this.contentAsInputStream = stream;
  }

  // public ContentRepresentationDefinition getContentRepresentationDefinition()
  // {
  // return contentRepresentationDefinition;
  // }
  //
  // public void
  // setContentRepresentationDefinition(ContentRepresentationDefinition
  // contentRepresentationDefinition) {
  // this.contentRepresentationDefinition = contentRepresentationDefinition;
  // }
}
