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

package org.activiti.service.engine.test.api;

import java.util.List;

import org.activiti.service.api.Repositories;
import org.activiti.service.api.content.Folder;
import org.activiti.service.api.content.Repository;
import org.activiti.service.engine.test.ActivitiTestCase;


/**
 * @author Tom Baeyens
 */
public class RepositoryTest extends ActivitiTestCase {

  public void testRepositories() {
    Repositories repositories = activiti.getManager(Repositories.class);
    
    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tbaeyens")
      .setName("My Files in Alfresco")
      .addFolder(new Folder().setName("Notes"))
      .addFolder(new Folder().setName("Meetings")));

    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tbaeyens")
      .setName("DevCon Site in Alfresco")
      .addFolder(new Folder().setName("Agenda"))
      .addFolder(new Folder().setName("Logos")
        .addFolder(new Folder().setName("Full Color"))
        .addFolder(new Folder().setName("One Color"))
        .addFolder(new Folder().setName("Reverse Colors"))
      )
      .addFolder(new Folder().setName("Submissions"))
      .addFolder(new Folder().setName("Templates"))
      .addFolder(new Folder().setName("Venue")));

    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tbaeyens")
      .setName("Activiti Site in Alfresco"));
  
    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tombaeyens")
      .setName("Activiti Wiki")
      .addFolder(new Folder().setName("a) General"))
      .addFolder(new Folder().setName("b) FAQ"))
      .addFolder(new Folder().setName("c) Experimental Features"))
      .addFolder(new Folder().setName("d) Developer")));

    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tombaeyens3@gmail.com")
      .setName("Google Docs"));
  
    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tbaeyens")
      .setName("Alfresco Mail"));
  
    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tombaeyens3")
      .setName("Google Mail"));
  
    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tb")
      .setName("Alfresco Salesforce")
      .addFolder(new Folder().setName("Customers"))
      .addFolder(new Folder().setName("Opportunities"))
      .addFolder(new Folder().setName("Deals")));

    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tomb")
      .setName("Alfresco Webex"));

    repositories.insert(new Repository()
      .setUserId("tom")
      .setAccountUsername("tombaeyens")
      .setName("Doodle"));

    List<Repository> tomsRepos = repositories.findByExample(new Repository().setUserId("tom"));

    // sample testing
    assertEquals(10, tomsRepos.size());
    
    Repository repository = tomsRepos.get(1);
    assertEquals("tom", repository.getUserId());
    assertEquals("tbaeyens", repository.getAccountUsername());
    assertEquals("DevCon Site in Alfresco", repository.getName());
    
    List<Folder> folders = repository.getFolders();
    assertEquals("Agenda", folders.get(0).getName());
    assertEquals("Logos", folders.get(1).getName());
    assertEquals("Submissions", folders.get(2).getName());
    assertEquals("Templates", folders.get(3).getName());
    assertEquals("Venue", folders.get(4).getName());
    
    folders = repository.getFolders().get(1).getFolders();
    assertEquals("Full Color", folders.get(0).getName());
    assertEquals("One Color", folders.get(1).getName());
    assertEquals("Reverse Colors", folders.get(2).getName());
  }
}
