package org.activiti.cdi.impl.annotation;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.activiti.engine.annotations.BusinessKey;
import org.activiti.engine.runtime.ProcessInstance;

/**
 * Producer for the current business key.
 * 
 * @author Daniel Meyer
 */
public class BusinessKeyProducer {

  @Produces
  @Named
  @BusinessKey
  public String businessKey(ProcessInstance processInstance) {
    return processInstance.getBusinessKey();
  }

}
