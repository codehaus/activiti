package org.activiti.engine.impl.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.activiti.engine.impl.javax.el.CompositeELResolver;
import org.activiti.engine.impl.javax.el.ELContext;
import org.activiti.engine.impl.javax.el.ELResolver;

/**
 * @see CompositeELResolver
 * @see ELResolver
 */
public class EJBELResolver extends ELResolver {

  /**
   * Test whether the given base should be resolved by this ELResolver.
   * 
   * @param base
   *          The bean to analyze.
   * @param property
   *          The name of the property to analyze.
   * @return base == null && property != null && property instanceof String;
   */
  private final boolean isResolvable(Object base, Object property) {
    return base == null && property != null && property instanceof String;
  }

  @Override
  public Object getValue(ELContext context, Object base, Object property) {

    Object o = null;

    if (isResolvable(base, property)) {
      try {
        // Properties currently read from jndi.properties if needed
        InitialContext i = new InitialContext();
        /* ejbName/local is not allowed as a full name in EL
         * Resolving it in parts does not work. So an escape trick 
         * needs to be used to circumvent this problem. 
         * So use ejbName__local or ejbName__remote or ... 
         * Another option is to use 'fail trough' to just use a name
         * and add /local if it fails and after that /remote.
         * 
         * Still need to work out the . and : separators
         *
         */
        property = ((String) property).replaceAll("__", "/");
        o = i.lookup((String) property);
        context.setPropertyResolved(true);
      } catch (NamingException e) {
        e.printStackTrace();
      }
      return o;
    }
    return null;
  }

  @Override
  public boolean isReadOnly(ELContext context, Object base, Object property) {
    // Not applicable for EJB's
    return false;
  }

  @Override
  public Class< ? > getCommonPropertyType(ELContext context, Object base) {
    // Not applicable for EJB's
    return null;
  }

  @Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
    // Not applicable here?
    return null;
  }

  @Override
  public Class< ? > getType(ELContext context, Object base, Object property) {
    // Not applicable for EJB's
    return null;
  }

  @Override
  public void setValue(ELContext context, Object base, Object property, Object value) {
    // Not applicable for EJB's
  }

}
