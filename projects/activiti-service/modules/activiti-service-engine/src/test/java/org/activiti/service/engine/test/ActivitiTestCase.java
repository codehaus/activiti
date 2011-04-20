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
import org.activiti.service.api.ActivitiConfiguration;
import org.activiti.service.api.model.User;
import org.activiti.service.api.model.Users;
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

  protected String username = "kermit";
  protected String password = "kermit";
  
  public void setUp() throws Exception {
    super.setUp();
    
    if (activiti==null) {
      log.info("### initializing Activiti ######################################");
      activiti = buildActiviti();
      activiti
        .getActivitiConfiguration()
        .setMailService(testMailService);
      log.info("################################################################");
    }
    
    User defaultUser = new User()
      .setId(username)
      .setPassword(password);
    activiti.getManager(Users.class).insert(defaultUser);
    
    testMailService.getMails().clear();
    log.info("=== starting test "+getName()+" =================================");
  }
  
  protected Activiti buildActiviti() throws Exception {
    return new ActivitiConfiguration().buildActiviti();
  }

  protected void tearDown() throws Exception {
    log.info("=== ending test "+getName()+" ===================================");
    activiti.clean(true);
    super.tearDown();
  }

  public void setUser(String username, String password) {
    setUser(username, password, false);
  }
  
  public void setUser(String username, String password, boolean create) {
    if (create) {
      User user = new User()
        .setId(username)
        .setPassword(password);
      activiti.getManager(Users.class).insert(user);
    }
    this.username = username;
    this.password = password;
  }
}
