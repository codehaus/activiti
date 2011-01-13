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

/**
 * activity implementation of the BPMN 2.0 ...
 * 
 * @author Ronald van Kuijk
 */
public class MultiInstanceLoopCharacteristics {

    private boolean isSequential;
    private String loopDataInput;
    private String inputDataItem;
    private String behaviour;
    private String loopCardinality;
    private String forEach;
    private String var;

    public MultiInstanceLoopCharacteristics() {
    }

    public void setSequential(boolean isSequential) {
        this.isSequential = isSequential;
    }

    public boolean isSequential() {
        return isSequential;
    }

    public String getLoopDataInput() {
        return loopDataInput;
    }

    public void setLoopDataInput(String loopDataInput) {
        this.loopDataInput = loopDataInput;
    }
    
    public Object getInputDataItem() {
        return inputDataItem;
    }

    public void setInputDataItem(String inputDataItem) {
        this.inputDataItem = inputDataItem;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public String getLoopCardinality() {
        return loopCardinality;
    }

    public void setLoopCardinality(String loopCardinality) {
        this.loopCardinality = loopCardinality;
    }

    public String getForEach() {
        return forEach;
    }

    public void setForEach(String forEach) {
        this.forEach = forEach;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

}
