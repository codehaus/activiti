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

package org.activiti.engine.impl.cmd;

import java.util.List;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;


/**
 * @author Tom Baeyens
 */
@SuppressWarnings("unchecked")
public class FindGroupsByUserIdAndGroupType implements Command<List> {

  protected String userId;
  protected String groupType;

  public FindGroupsByUserIdAndGroupType(String userId, String groupType) {
    this.userId = userId;
    this.groupType = groupType;
  }

  public List execute(CommandContext commandContext) {
    return commandContext
      .getIdentitySession()
      .findGroupsByUserAndType(userId, groupType);
  }
}
