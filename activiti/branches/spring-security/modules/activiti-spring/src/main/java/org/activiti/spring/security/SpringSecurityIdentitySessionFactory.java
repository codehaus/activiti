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
package org.activiti.spring.security;

import org.activiti.engine.impl.cfg.IdentitySession;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import java.util.logging.Logger;


/**
 * A factory for {@link DelegatingSpringSecurityIdentitySession} instances
 *
 * @author Tom Baeyens
 * @author Josh Long
 * @since 5.0
 */
public class SpringSecurityIdentitySessionFactory implements SessionFactory {
	private static Logger log = Logger.getLogger(SpringSecurityIdentitySessionFactory.class.getName());
	private volatile UserDetailsManager userDetailsManager;
	private volatile GroupManager groupManager;
  private volatile IdentitySession delegateIdentitySession ;

	public SpringSecurityIdentitySessionFactory(IdentitySession identitySession, UserDetailsManager userDetailsManager, GroupManager groupManager) {
		this.groupManager = groupManager;
		this.userDetailsManager = userDetailsManager;
		this.delegateIdentitySession = identitySession ;
		Assert.notNull(this.userDetailsManager, "the userDetailsManager must be non null");
	}

	public Session openSession() {
		return new DelegatingSpringSecurityIdentitySession(
				this.delegateIdentitySession , this.userDetailsManager, this.groupManager);
	}
}
