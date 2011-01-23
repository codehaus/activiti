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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.model.FieldExtensionModel;
import org.eclipse.bpmn2.ActivitiListener;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;


/**
 * @author Tijs Rademakers
 */
public class BpmnBOUtil {
  
  public static Object getExecutionListenerBO(PictogramElement pe, Diagram diagram) {
    Object bo = null;
    if(pe instanceof Diagram) {
      bo = ActivitiUiUtil.getProcessObject(diagram);
    } else {
      bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    }
    return bo;
  }

  public static List<ActivitiListener> getListeners(Object bo) {
    List<ActivitiListener> listenerList = null;
    if(bo instanceof Task) {
      listenerList = ((Task) bo).getActivitiListeners();
    } else if(bo instanceof SequenceFlow) {
      listenerList = ((SequenceFlow) bo).getExecutionListeners();
    } else if(bo instanceof org.eclipse.bpmn2.Process) {
      listenerList = ((org.eclipse.bpmn2.Process) bo).getExecutionListeners();
    }
    return listenerList;
  }
  
  public static void addListener(Object bo, ActivitiListener listener) {
    if(bo instanceof Task) {
      ((Task) bo).getActivitiListeners().add(listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().add(listener);
    } else if(bo instanceof org.eclipse.bpmn2.Process) {
      ((org.eclipse.bpmn2.Process) bo).getExecutionListeners().add(listener);
    }
  }
  
  public static void removeExecutionListener(Object bo, ActivitiListener listener) {
    if(bo instanceof Task) {
      ((Task) bo).getActivitiListeners().remove(listener);
    } else if(bo instanceof SequenceFlow) {
      ((SequenceFlow) bo).getExecutionListeners().remove(listener);
    } else if(bo instanceof org.eclipse.bpmn2.Process) {
      ((org.eclipse.bpmn2.Process) bo).getExecutionListeners().remove(listener);
    }
  }
  
  public static List<FieldExtensionModel> getFieldModelList(String fieldString) {
    String[] fieldStringList = fieldString.split(", ");
    List<FieldExtensionModel> fieldList = new ArrayList<FieldExtensionModel>();
    for (String field : fieldStringList) {
      String[] fieldExtensionStringList = field.split(":");
      if(fieldExtensionStringList != null && fieldExtensionStringList.length == 2) {
        FieldExtensionModel fieldExtension = new FieldExtensionModel();
        fieldExtension.fieldName = fieldExtensionStringList[0];
        fieldExtension.expression = fieldExtensionStringList[1];
        fieldList.add(fieldExtension);
      }
    }
    return fieldList;
  }
}
