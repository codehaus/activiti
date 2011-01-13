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

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.javax.el.PropertyNotFoundException;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.scripting.ScriptingEngines;


/**
 * activity implementation of the BPMN 2.0 'outer' loop activity.
 * 
 * @author Ronald van Kuijk
 */
public class StandardLoopActivity extends AbstractBpmnActivity implements LoopActivity {

    private static Logger log = Logger.getLogger(StandardLoopActivity.class.getName());

    private ActivityImpl activity;
    private StandardLoopCharacteristics slc;
    private int loopCounter = 0;

    public int getLoopCounter() {
        return loopCounter;
    }

    public StandardLoopActivity(ActivityImpl activity, StandardLoopCharacteristics slc) {
        super();
        this.activity = activity;
        this.slc = slc;
    }

    public void execute(ActivityExecution execution) throws Exception {
        ScriptingEngines scriptingEngines = CommandContext.getCurrent().getProcessEngineConfiguration().getScriptingEngines();

        boolean repeat = true;
        while (repeat && (slc.getLoopMaximum() == 0 || loopCounter < slc.getLoopMaximum())) {
            if (slc.isTestBefore()) {
                try {
                    repeat = slc.getLoopCondition() != null ? (Boolean) scriptingEngines.evaluate(slc.getLoopCondition(), "juel", execution)
                            : true;
                } catch (ActivitiException e) {
                    log.warning(e.getMessage() + " so loopCondition interpreted as false");
                    repeat = false;
                }
                if (repeat) {
                    activity.getActivityBehavior().execute(execution);
                }
            } else {
                activity.getActivityBehavior().execute(execution);
                try {
                    repeat = slc.getLoopCondition() != null ? (Boolean) scriptingEngines.evaluate(slc.getLoopCondition(), "juel", execution)
                            : true;
                } catch (PropertyNotFoundException pnfe) {
                    log.warning(pnfe.getMessage() + " so loopCondition interpreted as false");
                    repeat = false;
                }
            }
            loopCounter++;
        }
        lastExecutionEnded(execution);
    }

    public void lastExecutionEnded(ActivityExecution execution) {
        leaveLast(execution);
    }

}
