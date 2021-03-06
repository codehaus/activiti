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

package org.activiti.explorer.navigation;

import org.activiti.explorer.ExplorerApplication;
import org.activiti.explorer.ui.flow.FlowPage;


/**
 * @author Frederik Heremans
 */
public class FlowNavigationHandler implements NavigationHandler {

  public static final String FLOW_URI_PART = "flows";
  
  public String getTrigger() {
    return FLOW_URI_PART;
  }

  public void handleNavigation(UriFragment uriFragment) {
    String processDefinitionId = uriFragment.getUriPart(1);
    
    if(processDefinitionId != null) {
      ExplorerApplication.getCurrent().switchView(new FlowPage(processDefinitionId));
    } else {
      ExplorerApplication.getCurrent().switchView(new FlowPage());
    }
  }

}
