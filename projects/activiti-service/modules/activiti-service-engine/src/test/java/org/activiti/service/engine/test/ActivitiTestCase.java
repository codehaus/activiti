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

package org.activiti.service.engine.test;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.activiti.service.api.Activiti;
import org.activiti.service.api.ActivitiConfigurationBuilder;
import org.activiti.service.api.Users;
import org.activiti.service.api.identity.User;
import org.activiti.service.impl.util.LogUtil;
import org.junit.Ignore;


/**
 * @author Tom Baeyens
 */
@Ignore
public class ActivitiTestCase extends TestCase {

  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath();
  }
  
  private static Logger log = Logger.getLogger(RestTestCase.class.getName());
  
  protected static Activiti activiti = null;
  protected static TestMailService testMailService = new TestMailService();

  protected String userId = "kermit";
  protected String password = "kermit";
  
  public void setUp() throws Exception {
    super.setUp();
    
    if (activiti==null) {
      log.info("### initializing Activiti ######################################");
      activiti = new ActivitiConfigurationBuilder().buildActiviti();
      activiti
        .getActivitiConfiguration()
        .setMailService(testMailService);
      log.info("################################################################");
    }
    
    createUser(userId, password);
    
    testMailService.getMails().clear();
    log.info("=== starting test "+getName()+" =================================");
  }

  protected void tearDown() throws Exception {
    log.info("=== ending test "+getName()+" ===================================");
    activiti.clean(true);
    super.tearDown();
  }

  public void setUser(String userId, String password) {
    setUser(userId, password, false);
  }
  
  public void setUser(String userId, String password, boolean create) {
    if (create) {
      createUser(userId, password);
    }
    this.userId = userId;
    this.password = password;
  }
  
  /** @return user oid */
  public void createUser(String userId, String password) {
    User user = new User()
      .setId(userId)
      .setPassword(password);
    activiti.getManager(Users.class).insert(user);
  }
}
