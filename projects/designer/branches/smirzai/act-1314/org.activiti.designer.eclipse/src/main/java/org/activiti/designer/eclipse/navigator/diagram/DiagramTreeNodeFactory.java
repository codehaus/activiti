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
package org.activiti.designer.eclipse.navigator.diagram;

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Pool;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.eclipse.navigator.TreeNode;
import org.eclipse.core.resources.IFile;

/**
 * @author Tiese Barrell
 * 
 */
public final class DiagramTreeNodeFactory {

  private DiagramTreeNodeFactory() {
    super();
  }

  public static final TreeNode createModelFileNode(final IFile modelFile) {
    return new FileDiagramTreeNode(modelFile);
  }

  public static final TreeNode createFlowElementNode(final TreeNode parent, final FlowElement flowElement) {
    if (flowElement instanceof SubProcess) {
      return createSubProcessNode(parent, (SubProcess) flowElement);
    } else {
      return new FlowElementDiagramTreeNode(parent, flowElement);
    }
  }

  public static final TreeNode createProcessNode(final TreeNode parent, final Process process) {
    return new ProcessDiagramTreeNode(parent, process);
  }

  public static final TreeNode createTransparentProcessNode(final TreeNode parent, final Process process) {
    return new TransparentProcessDiagramTreeNode(parent, process);
  }

  public static final TreeNode createSubProcessNode(final TreeNode parent, final SubProcess subProcess) {
    return new SubProcessDiagramTreeNode(parent, subProcess);
  }

  public static final TreeNode createPoolNode(final TreeNode parent, final Pool pool) {
    return new PoolDiagramTreeNode(parent, pool);
  }

  public static final TreeNode createLaneNode(final TreeNode parent, final Lane lane) {
    return new LaneDiagramTreeNode(parent, lane);
  }

}
