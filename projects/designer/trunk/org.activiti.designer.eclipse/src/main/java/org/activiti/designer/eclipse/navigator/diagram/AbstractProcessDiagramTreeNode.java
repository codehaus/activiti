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

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.eclipse.navigator.TreeNode;

/**
 * @author Tiese Barrell
 */
public abstract class AbstractProcessDiagramTreeNode extends AbstractDiagramTreeNode<Process> {

  public AbstractProcessDiagramTreeNode(final Object parent, final Process process) {
    super(parent, process, process.getName());
  }

  @Override
  protected void extractChildren() {

    final List<FlowElement> allFlowElements = getModelObject().getFlowElements();

    final List<Lane> lanes = getModelObject().getLanes();

    final List<FlowElement> orphanedFlowElements = new ArrayList<FlowElement>();

    for (final FlowElement flowElement : allFlowElements) {
      boolean orphaned = true;
      for (final Lane lane : lanes) {
        if (lane.getFlowReferences().contains(flowElement.getId())) {
          orphaned = false;
        }
      }
      if (orphaned) {
        orphanedFlowElements.add(flowElement);
      }
    }

    final List<TreeNode> flowElementChildren = createChildNodesForFlowElements(orphanedFlowElements);
    for (final TreeNode flowElementChild : flowElementChildren) {
      addChildNode(flowElementChild);
    }

    final List<TreeNode> laneChildren = createChildNodesForLanes(lanes);
    for (final TreeNode laneChild : laneChildren) {
      addChildNode(laneChild);
    }

  }

  protected abstract List<TreeNode> createChildNodesForFlowElements(List<FlowElement> flowElements);

  protected abstract List<TreeNode> createChildNodesForLanes(List<Lane> lanes);

}
