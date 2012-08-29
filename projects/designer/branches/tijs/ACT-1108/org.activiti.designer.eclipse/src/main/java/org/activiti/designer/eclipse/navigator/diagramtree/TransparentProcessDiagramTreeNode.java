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

package org.activiti.designer.eclipse.navigator.diagramtree;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.Artifact;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Process;

/**
 * @author Tiese Barrell
 */
public class TransparentProcessDiagramTreeNode extends
		AbstractProcessDiagramTreeNode {

	public TransparentProcessDiagramTreeNode(final Object parent,
			final Process process) {
		super(parent, process);
	}

	@Override
	protected List<DiagramTreeNode> createChildNodesForFlowElements(
			List<FlowElement> flowElements) {
		final List<DiagramTreeNode> result = new ArrayList<DiagramTreeNode>();

		for (final FlowElement flowElement : flowElements) {
			// wire directly to parent to provide transparency
			result.add(DiagramTreeNodeFactory.createFlowElementNode(
					getParentNode(), flowElement));
		}

		return result;
	}

	@Override
	protected List<DiagramTreeNode> createChildNodesForLanes(List<Lane> lanes) {
		final List<DiagramTreeNode> result = new ArrayList<DiagramTreeNode>();

		for (final Lane lane : lanes) {
			// wire directly to parent to provide transparency
			result.add(DiagramTreeNodeFactory.createLaneNode(getParentNode(),
					lane));
		}

		return result;
	}

	@Override
	protected List<DiagramTreeNode> createChildNodesForArtifacts(
			List<Artifact> artifacts) {

		final List<DiagramTreeNode> result = new ArrayList<DiagramTreeNode>();
		for (final Artifact artifact : artifacts) {
			result.add(DiagramTreeNodeFactory.createArtifactNode(getParentNode(), artifact));
		}
		
		return result;
	}
}
