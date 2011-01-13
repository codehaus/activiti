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

/**
 * activity implementation of the BPMN 2.0 'outer' loop activity.
 * 
 * @author Ronald van Kuijk
 */
public class StandardLoopCharacteristics {

    private static Logger log = Logger.getLogger(StandardLoopCharacteristics.class.getName());

    private String loopCondition;
    private boolean testBefore;
    private int loopMaximum;
    private final String loopConfigWarning = "No loopCondition or loopMaxmimum specified. Will loop infinite until an error";

    public StandardLoopCharacteristics() {
        
    }
    
    public StandardLoopCharacteristics(String loopCondition, boolean testBefore, int loopMaximum) {
      
        this.loopCondition = loopCondition;
        this.testBefore = testBefore;
        this.loopMaximum = loopMaximum;
        
        selectiveWarn();
        
    }
    
    public void selectiveWarn() {
        if (loopCondition == null && loopMaximum == 0) {
            log.warning(loopConfigWarning);
        }
    }
    
    public String getLoopCondition() {
        selectiveWarn();
        return loopCondition;
    }
    
    public void setLoopCondition(String loopCondition) {
        this.loopCondition = loopCondition;
    }

    public boolean isTestBefore() {
        return testBefore;
    }

    public void setTestBefore(boolean testBefore) {
        this.testBefore = testBefore;
    }

    public int getLoopMaximum() {
        selectiveWarn();
        return loopMaximum;
    }

    public void setLoopMaximum(int loopMaximum) {
        this.loopMaximum = loopMaximum;
    }

}
