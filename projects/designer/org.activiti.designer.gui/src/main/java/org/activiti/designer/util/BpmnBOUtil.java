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

package org.activiti.designer.util;

import java.util.List;

import org.eclipse.bpmn2.ExecutionListener;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;


/**
 * @author Tijs Rademakers
 */
public class BpmnBOUtil {
  
  public static Object getExecutionLisenerBO(PictogramElement pe, Diagram diagram) {
    Object bo = null;
    if(pe instanceof Diagram) {
      bo = ActivitiUiUtil.getProcessObject(diagram);
    } else {
      bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    }
    return bo;
  }

  public static List<ExecutionListener> getExecutionListeners(Object bo) {
    List<ExecutionListener> executionListenerList = null;
    if(bo instanceof Task) {
      executionListenerList = ((Task) bo).getExecutionListeners();
    } else if(bo instanceof SequenceFlow) {
      executionListenerList = ((SequenceFlow) bo).getExecutionListeners();
    } else if(bo instanceof org.eclipse.bpmn2.Process) {
      executionListenerList = ((org.eclipse.bpmn2.Process) bo).getExecutionListeners();
    }
    return executionListenerList;
  }
  
  public static void addExecutionListener(Object bo, ExecutionListener listener) {
    if(bo instanceof Task) {
      ((Task) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof org.eclipse.bpmn2.Process) {
      ((org.eclipse.bpmn2.Process) bo).getExecutionListeners().add(listener);
    }
  }
}
