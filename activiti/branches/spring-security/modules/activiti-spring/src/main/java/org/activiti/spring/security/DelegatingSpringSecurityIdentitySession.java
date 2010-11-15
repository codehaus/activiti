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

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.IdentitySession;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.interceptor.Session;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * Simple {@link org.activiti.engine.impl.cfg.IdentitySession} instance
 *
 * The class handles keeping Spring Security-based storages in-sync with the state of the data being kept in Activiti.
 *
 * Read-only methods simply delegate to the #delegate {@link org.activiti.engine.impl.cfg.IdentitySession}
 *
 * @author Tom Baeyens
 * @author Josh Long
 * @since 5.0
 */
public class DelegatingSpringSecurityIdentitySession implements IdentitySession, Session {


	private static Logger log = Logger.getLogger( DelegatingSpringSecurityIdentitySession.class.getName());

	/**
	 * default {@link org.springframework.security.core.GrantedAuthority} instances to be used in our call to {@link org.springframework.security.provisioning.GroupManager#createGroup(String, java.util.List)}
	 */
	private volatile List<GrantedAuthority> grantedAuthorities = Arrays.asList( );

	/**
	 * the {@link org.springframework.security.provisioning.UserDetailsManager} instance
	 * can handle CRUD operations and can be configured using the various Spring Security
	 * options (user-service, ldap-user-service, jdbc-user-service, etc)
	 */
	private volatile UserDetailsManager userDetailsManager ;

	/**
	 * we happen to know that a few Spring Security implemenations of {@link org.springframework.security.provisioning.UserDetailsManager}
	 * also provide {@link org.springframework.security.provisioning.GroupManager} support.
	 *
	 */
	private volatile GroupManager groupManager ;// optional

	/**
	 * this is a delegate {@link org.activiti.engine.impl.cfg.IdentitySession} instance
	 */
	private volatile IdentitySession identitySession ;

	public DelegatingSpringSecurityIdentitySession(IdentitySession identitySession, UserDetailsManager userDetailsManager, GroupManager groupManager) {
		this.construct(identitySession ,userDetailsManager , groupManager);
	}

	/**
	 * Default constructor in case the two implementations are seperate
	 *
	 * @param is an {@link org.activiti.engine.impl.cfg.IdentitySession} instance that's delegated to
	 * @param userDetailsManager the {@link org.springframework.security.provisioning.UserDetailsManager} instance
	 * @param groupManager the {@link org.springframework.security.provisioning.GroupManager} instance
	 */
	protected void construct(IdentitySession is,UserDetailsManager userDetailsManager , GroupManager groupManager) {
		this.userDetailsManager = userDetailsManager ;
		this.groupManager = groupManager ;
		this.identitySession = is;
	}

	public DelegatingSpringSecurityIdentitySession(IdentitySession is, UserDetailsManager userDetailsManager) {
		GroupManager groupManager = userDetailsManager instanceof GroupManager ? (GroupManager) userDetailsManager : null;
		this.construct(is, userDetailsManager , groupManager);
	}

	public void createMembership(String userId, String groupId) {
		 if(this.groupManager!=null)
			 this.groupManager.addUserToGroup( userId ,groupId );
	}


	public Group createNewGroup(String groupId) {
		if(this.groupManager!=null)
			this.groupManager.createGroup(groupId , this.grantedAuthorities);

		return this.identitySession.createNewGroup(groupId );
	}

	public GroupQuery createNewGroupQuery(CommandExecutor commandExecutor) {
		return this.identitySession.createNewGroupQuery(commandExecutor) ;
	}

	public User createNewUser(String userId) {
		ActivitiUserDetails activitiUserDetails = new ActivitiUserDetails( userId , null);
		this.userDetailsManager.createUser( activitiUserDetails );
		return identitySession.createNewUser( userId);
	}

	public UserQuery createNewUserQuery(CommandExecutor commandExecutor) {
		return this.identitySession.createNewUserQuery( commandExecutor );
	}

	public void deleteGroup(String groupId) {
		if(this.groupManager!=null){
			this.groupManager.deleteGroup( groupId );
		}
		identitySession.deleteGroup( groupId );
	}

	public void deleteMembership(String userId, String groupId) {
		if(this.groupManager!=null){
			this.groupManager.removeUserFromGroup( userId, groupId );
		}
		identitySession.deleteMembership(userId, groupId );
	}

	public void deleteUser(String userId) {
		this.userDetailsManager.deleteUser(userId );
		identitySession.deleteUser(userId);
	}

	public Group findGroupById(String groupId) {
		return identitySession.findGroupById( groupId );
	}

	public List<Group> findGroupByQueryCriteria(Object query, Page page) {
		return identitySession.findGroupByQueryCriteria(query, page);
	}

	public long findGroupCountByQueryCriteria(Object query) {
		return identitySession.findGroupCountByQueryCriteria(query);
	}

	public List<Group> findGroupsByUser(String userId) {
		return identitySession.findGroupsByUser(userId);
	}

	public User findUserById(String userId) {
		return identitySession.findUserById( userId );
	}

	public List<User> findUserByQueryCriteria(Object query, Page page) {
		return identitySession.findUserByQueryCriteria(query, page);
	}

	public long findUserCountByQueryCriteria(Object query) {
		return identitySession.findUserCountByQueryCriteria(query);
	}

	public List<User> findUsersByGroupId(String groupId) {
		return this.identitySession.findUsersByGroupId(groupId );
	}

	public void insertGroup(Group group) {
		if(this.groupManager!=null){
			groupManager.createGroup(  group.getId() , this.grantedAuthorities);
		}
		this.identitySession.insertGroup( group );
	}

	public void insertUser(User user) {
		this.userDetailsManager.createUser(this.activitiUserDetailsFromUser(user));
		this.identitySession.insertUser(user);
	}

	protected UserDetails activitiUserDetailsFromUser(User user){
		return new ActivitiUserDetails(user);
	}

	public boolean isValidUser(String userId) {
		return this.userDetailsManager.userExists( userId ) && this.identitySession.isValidUser( userId );
	}

	public void updateGroup(Group updatedGroup) {
	  identitySession.updateGroup( updatedGroup );
	}


	public void updateUser(User updatedUser) {
		this.userDetailsManager.updateUser( this.activitiUserDetailsFromUser(updatedUser) );
		identitySession.updateUser( updatedUser );
	}

	public void close() {
		if( this.groupManager !=null && this.groupManager instanceof DisposableBean){
			try{
				((DisposableBean)this.groupManager).destroy() ;
			} catch (Exception e){
				log.severe( "couldn't destroy the GroupManager instance");
			}
		}
		if( this.userDetailsManager!=null && this.userDetailsManager instanceof DisposableBean)
			try {
				((DisposableBean)this.userDetailsManager).destroy() ;
			} catch (Exception e) {
				log.severe( "couldn't destroy the UserDetailsManager instance");
			}
	}

	public void flush() {
		if(identitySession instanceof Session)
			((Session)this.identitySession).flush();
	}
}
