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

import org.activiti.engine.identity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


/**
 * implementation of {@link org.springframework.security.core.userdetails.UserDetails}
 * which is 1/2 of Spring Security's domain model for representing authorizable entities
 *
 * @see org.springframework.security.provisioning.GroupManager
 * @author Josh Long
 * @since 5.0
 */
public class ActivitiUserDetails implements UserDetails {
	/**
	 * Activiti's notion of a user {@link org.activiti.engine.identity.User}
	 */
	private User user; // readonly

	/**
	 * the {@link org.activiti.engine.identity.User} is immutable, and we need this object even when we don't have a {@link org.activiti.engine.identity.User}
	 */
	private String username;

	/**
	 * the {@link org.activiti.engine.identity.User} is immutable, and we need this object even when we don't have a {@link org.activiti.engine.identity.User}
	 */
	private String password;

	public ActivitiUserDetails(User user) {
		this.user = user;
		this.username  = this.user.getId();
		this.password = this.user.getPassword();
	}

	public ActivitiUserDetails(String username, String password) {
		this.username = username;
		this.password = password;
	}

	// todo - how does this map to Activiti's world?
	public Collection<GrantedAuthority> getAuthorities() {
		return new ArrayList<GrantedAuthority>();
	}

	public String getPassword() {
		return this.password ;
	}

	public String getUsername() {
		return this.username ;
	}

	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}

	public boolean isEnabled() {
		return this.user != null; // basically true always
	}
}
