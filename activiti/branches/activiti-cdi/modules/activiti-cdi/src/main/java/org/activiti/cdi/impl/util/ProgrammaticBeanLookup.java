/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cdi.impl.util;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Utility class for performing programmatic bean lookups.
 * 
 * @author Daniel Meyer
 */
public class ProgrammaticBeanLookup {

  @SuppressWarnings("unchecked")
  public static <T> T lookup(Class<T> clazz) {
    BeanManager beanManager = BeanManagerLookup.getBeanManager();
    Set<Bean< ? >> beans = beanManager.getBeans(clazz);
    if (beans.isEmpty()) {
      // throw exception instead ?
      return null;
    }
    Bean<T> bean = (Bean<T>) beans.iterator().next();
    T beanInstance = beanManager.getContext(bean.getScope()).get(bean);
    if (beanInstance != null) {
      return beanInstance;
    }
    CreationalContext<T> ct = beanManager.createCreationalContext(bean);
    return beanManager.getContext(bean.getScope()).get(bean, ct);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T lookup(String name) {
    BeanManager beanManager = BeanManagerLookup.getBeanManager();
    Set<Bean< ? >> beans = beanManager.getBeans(name);
    if (beans.isEmpty()) {
      // throw exception instead ?
      return null;
    }
    Bean<T> bean = (Bean<T>) beans.iterator().next();
    T beanInstance = beanManager.getContext(bean.getScope()).get(bean);
    if (beanInstance != null) {
      return beanInstance;
    }
    CreationalContext<T> ct = beanManager.createCreationalContext(bean);
    return beanManager.getContext(bean.getScope()).get(bean, ct);
  }

 
}
