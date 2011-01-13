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
package org.activiti.engine.test.jobexecutor;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.jobexecutor.JobHandler;
import org.activiti.engine.impl.runtime.ExecutionEntity;
import org.junit.Assert;

public class TweetHandler implements JobHandler {

  private static Logger log = Logger.getLogger(TweetHandler.class.getName());

  ConcurrentSkipListSet<String> messages = new ConcurrentSkipListSet<String>();

  public String getType() {
    return "tweet";
  }

  public void execute(String configuration, ExecutionEntity execution, CommandContext commandContext) {

//    try {
//     Thread.sleep(new Random().nextInt(51));
//     } catch (InterruptedException e) {
     log.fine("Thread" + Thread.currentThread().getName());
//     log.severe("Message: " + configuration);
//     log.severe("Error: " + e.getMessage());
//     throw (new RuntimeException("Error processing " + configuration,e));
//     }
    try {
      
//      if (new Random().nextInt(200) == 100) {
//        throw new RuntimeException("Bla " + configuration);
//      }
      
      if (messages.contains(configuration)) {
        log.severe("Duplicate insertion of " + configuration + ". (Not so) Silently ignoring" );
      } else {
        messages.add(configuration);
      }

    } catch (RuntimeException e) {
      log.log(Level.SEVERE, "exception during timer execution", e);
      throw e;

    } catch (Exception e) {
      log.log(Level.SEVERE, "exception during timer execution", e);
      throw new ActivitiException("exception during timer execution: " + e.getMessage(), e);
    }

    Assert.assertNotNull(commandContext);
  }

  public ConcurrentSkipListSet<String> getMessages() {
    return messages;
  }
}