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

import java.util.Collection;
import java.util.logging.Logger;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.scripting.ScriptingEngines;


/**
 * activity implementation of the BPMN 2.0 'outer' loop activity.
 * 
 * @author Ronald van Kuijk
 */
public class MultiInstanceLoopActivity extends AbstractBpmnActivity implements LoopActivity {

    private static Logger log = Logger.getLogger(MultiInstanceLoopActivity.class.getName());

    private ActivityImpl activity;
    private int loopCounter = 0;
    private MultiInstanceLoopCharacteristics milc;

    public int getLoopCounter() {
        return loopCounter;
    }

    public MultiInstanceLoopActivity(ActivityImpl activity, MultiInstanceLoopCharacteristics milc) {
        super();
        this.activity = activity;
        this.milc = milc;
    }

    public void execute(ActivityExecution execution) throws Exception {
        ScriptingEngines scriptingEngines = CommandContext.getCurrent().getProcessEngineConfiguration().getScriptingEngines();
        Collection<Object> forEach = null;
        if (milc.getForEach() != null) {
            forEach = (Collection) scriptingEngines.evaluate(milc.getForEach(), "juel", execution);
        }
        Object var;
        for (Object object : forEach) {
            execution.setVariable(milc.getVar(), object);
            activity.getActivityBehavior().execute(execution);
        }
        
        lastExecutionEnded(execution);
    }

    public void lastExecutionEnded(ActivityExecution execution) {
        leaveLast(execution);
    }

}
