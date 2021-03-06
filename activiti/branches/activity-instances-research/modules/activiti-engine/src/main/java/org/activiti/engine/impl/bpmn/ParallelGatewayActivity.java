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

package org.activiti.engine.impl.bpmn;

import java.util.logging.Logger;

import org.activiti.pvm.activity.ActivityContext;
import org.activiti.pvm.process.PvmActivity;

/**
 * Implementation of the Parallel Gateway/AND gateway as definined in the BPMN
 * 2.0 specification.
 * 
 * The Parallel Gateway can be used for splitting a path of execution into
 * multiple paths of executions (AND-split/fork behavior), one for every
 * outgoing sequence flow.
 * 
 * The Parallel Gateway can also be used for merging or joining paths of
 * execution (AND-join). In this case, on every incoming sequence flow an
 * execution needs to arrive, before leaving the Parallel Gateway (and
 * potentially then doing the fork behavior in case of multiple outgoing
 * sequence flow).
 * 
 * Note that a Parallel Gateway having one incoming and multiple ougoing
 * sequence flow, is the same as having multiple outgoing sequence flow on a
 * given activity. However, a parallel gateway does NOT check conditions on the
 * outgoing sequence flow.
 * 
 * @author Joram Barrez
 * @author Tom Baeyens
 */
public class ParallelGatewayActivity extends GatewayActivity {
  
  private static Logger log = Logger.getLogger(ParallelGatewayActivity.class.getName());

  public void start(ActivityContext activityContext) throws Exception { 
    PvmActivity activity = activityContext.getActivity();

    throw new UnsupportedOperationException("please implement me");
//    ConcurrencyController concurrencyController = new ConcurrencyController(activityContext);
//    concurrencyController.inactivate();
//    
//    List<ActivityExecution> joinedExecutions = concurrencyController.findInactiveConcurrentExecutions(activity);
//    
//    int nbrOfExecutionsToJoin = activityContext.getIncomingTransitions().size();
//    int nbrOfExecutionsJoined = joinedExecutions.size();
//    
//    if (nbrOfExecutionsJoined==nbrOfExecutionsToJoin) {
//      log.fine("parallel gateway '"+activity.getId()+"' activates: "+nbrOfExecutionsJoined+" of "+nbrOfExecutionsToJoin+" joined");
//      concurrencyController.takeAll(activityContext.getOutgoingTransitions(), joinedExecutions);
//      
//    } else if (log.isLoggable(Level.FINE)){
//      log.fine("parallel gateway '"+activity.getId()+"' does not activate: "+nbrOfExecutionsJoined+" of "+nbrOfExecutionsToJoin+" joined");
//    }
  }
}
