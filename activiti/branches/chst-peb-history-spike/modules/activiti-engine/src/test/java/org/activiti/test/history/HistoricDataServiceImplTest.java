/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.test.history;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.activiti.ProcessInstance;
import org.activiti.impl.history.HistoricActivityInstance;
import org.activiti.impl.history.HistoricDataService;
import org.activiti.impl.history.HistoricDataServiceImpl;
import org.activiti.impl.history.HistoricProcessInstance;
import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.time.Clock;
import org.activiti.pvm.Activity;
import org.activiti.test.LogInitializer;
import org.activiti.test.ProcessDeployer;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christian Stettler
 */
public class HistoricDataServiceImplTest {

  @Rule
  public LogInitializer logSetup = new LogInitializer();
  @Rule
  public ProcessDeployer deployer = new ProcessDeployer();

  @Test
  public void testCreateHistoricProcessInstance() {
    try {
      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getId()).thenReturn("processInstanceId");
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      Date startTime = new Date();
      Clock.setCurrentTime(startTime);

      HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstance(processInstance);

      assertNotNull(historicProcessInstance);
      assertEquals("processInstanceId", historicProcessInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", historicProcessInstance.getProcessDefinitionId());
      assertEquals(startTime, historicProcessInstance.getStartTime());
      assertNull(historicProcessInstance.getEndTime());
      assertNull(historicProcessInstance.getDurationInMillis());
      assertNull(historicProcessInstance.getEndStateName());

      HistoricProcessInstance loadedHistoricProcessInstance = historicDataService.findHistoricProcessInstance(processInstance.getId());

      assertNotNull(loadedHistoricProcessInstance);
      assertEquals("processInstanceId", loadedHistoricProcessInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", loadedHistoricProcessInstance.getProcessDefinitionId());
      assertEquals(startTime, loadedHistoricProcessInstance.getStartTime());
      assertNull(loadedHistoricProcessInstance.getEndTime());
      assertNull(loadedHistoricProcessInstance.getDurationInMillis());
      assertNull(loadedHistoricProcessInstance.getEndStateName());
    } finally {
      Clock.reset();
      cleanHistoricProcessInstancesFromDatabase("processInstanceId");
    }
  }

  @Test
  public void testMarkHistoricProcessInstanceEnded() {
    try {
      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getId()).thenReturn("processInstanceId");
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      Date startTime = new Date();
      Clock.setCurrentTime(startTime);

      historicDataService.createHistoricProcessInstance(processInstance);

      Date endTime = new Date(startTime.getTime() + 1000);
      Clock.setCurrentTime(endTime);

      HistoricProcessInstance endedHistoricProcessInstance = historicDataService.markHistoricProcessInstanceEnded("processInstanceId", "endStateName");

      assertEquals("processInstanceId", endedHistoricProcessInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", endedHistoricProcessInstance.getProcessDefinitionId());
      assertEquals(startTime, endedHistoricProcessInstance.getStartTime());
      assertEquals(endTime, endedHistoricProcessInstance.getEndTime());
      assertEquals(Long.valueOf(1000L), endedHistoricProcessInstance.getDurationInMillis());
      assertEquals("endStateName", endedHistoricProcessInstance.getEndStateName());

      HistoricProcessInstance loadedHistoricProcessInstance = historicDataService.findHistoricProcessInstance("processInstanceId");

      assertEquals("processInstanceId", loadedHistoricProcessInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", loadedHistoricProcessInstance.getProcessDefinitionId());
      assertEquals(startTime, loadedHistoricProcessInstance.getStartTime());
      assertEquals(endTime, loadedHistoricProcessInstance.getEndTime());
      assertEquals(Long.valueOf(1000L), loadedHistoricProcessInstance.getDurationInMillis());
      assertEquals("endStateName", loadedHistoricProcessInstance.getEndStateName());
    } finally {
      Clock.reset();
      cleanHistoricProcessInstancesFromDatabase("processInstanceId");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMarkHistoricProcessInstanceEndedFailsForNonExistingHistoricProcessInstance() {
    HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());
    historicDataService.markHistoricProcessInstanceEnded("nonExistingProcessInstanceId", "endStateName");
  }

  @Test
  public void testUniqueConstraintsOnHistoricProcessInstance() {
    try {
      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataServiceImpl historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      when(processInstance.getId()).thenReturn("processInstanceIdOne");
      historicDataService.createHistoricProcessInstance(processInstance);

      try {
        historicDataService.createHistoricProcessInstance(processInstance);
        fail("unique key constraint violation expected");
      } catch (Exception expected) {
      }

      when(processInstance.getId()).thenReturn("processInstanceIdTwo");
      historicDataService.createHistoricProcessInstance(processInstance);
    } finally {
      cleanHistoricProcessInstancesFromDatabase("processInstanceIdOne");
      cleanHistoricProcessInstancesFromDatabase("processInstanceIdTwo");
    }
  }

  @Test
  public void testCreateHistoricActivtiyInstance() {
    try {
      Activity activity = mock(Activity.class);
      when(activity.getId()).thenReturn("activityId");
      when(activity.getName()).thenReturn("activityName");
      when(activity.getType()).thenReturn("activityType");

      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getId()).thenReturn("processInstanceId");
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      Date startTime = new Date();
      Clock.setCurrentTime(startTime);

      HistoricActivityInstance historicActivityInstance = historicDataService.createHistoricActivityInstance(activity, processInstance);

      assertNotNull(historicActivityInstance);
      assertEquals("activityId", historicActivityInstance.getActivityId());
      assertEquals("activityName", historicActivityInstance.getActivityName());
      assertEquals("activityType", historicActivityInstance.getActivityType());
      assertEquals("processInstanceId", historicActivityInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", historicActivityInstance.getProcessDefinitionId());
      assertEquals(startTime, historicActivityInstance.getStartTime());
      assertNull(historicActivityInstance.getEndTime());
      assertNull(historicActivityInstance.getDurationInMillis());

      HistoricActivityInstance loadedHistoricActivityInstance = historicDataService.findHistoricActivityInstance("activityId", "processInstanceId");

      assertNotNull(loadedHistoricActivityInstance);
      assertEquals("activityId", loadedHistoricActivityInstance.getActivityId());
      assertEquals("activityName", loadedHistoricActivityInstance.getActivityName());
      assertEquals("activityType", loadedHistoricActivityInstance.getActivityType());
      assertEquals("processInstanceId", loadedHistoricActivityInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", loadedHistoricActivityInstance.getProcessDefinitionId());
      assertEquals(startTime, loadedHistoricActivityInstance.getStartTime());
      assertNull(loadedHistoricActivityInstance.getEndTime());
      assertNull(loadedHistoricActivityInstance.getDurationInMillis());
    } finally {
      Clock.reset();
      cleanHistoricActivityInstancesFromDatabase("activityId", "processInstanceId");
    }
  }

  @Test
  public void testMarkHistoricActivityInstanceEnded() {
    try {
      Activity activity = mock(Activity.class);
      when(activity.getId()).thenReturn("activityId");
      when(activity.getName()).thenReturn("activityName");
      when(activity.getType()).thenReturn("activityType");

      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getId()).thenReturn("processInstanceId");
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      Date startTime = new Date();
      Clock.setCurrentTime(startTime);

      historicDataService.createHistoricActivityInstance(activity, processInstance);

      Date endTime = new Date(startTime.getTime() + 1000);
      Clock.setCurrentTime(endTime);

      HistoricActivityInstance endedHistoricActivityInstance = historicDataService.markHistoricActivityInstanceEnded("activityId", "processInstanceId");

      assertEquals("activityId", endedHistoricActivityInstance.getActivityId());
      assertEquals("activityName", endedHistoricActivityInstance.getActivityName());
      assertEquals("activityType", endedHistoricActivityInstance.getActivityType());
      assertEquals("processInstanceId", endedHistoricActivityInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", endedHistoricActivityInstance.getProcessDefinitionId());
      assertEquals(startTime, endedHistoricActivityInstance.getStartTime());
      assertEquals(endTime, endedHistoricActivityInstance.getEndTime());
      assertEquals(Long.valueOf(1000L), endedHistoricActivityInstance.getDurationInMillis());

      HistoricActivityInstance loadedHistoricActivityInstance = historicDataService.findHistoricActivityInstance("activityId", "processInstanceId");

      assertEquals("activityId", loadedHistoricActivityInstance.getActivityId());
      assertEquals("activityName", loadedHistoricActivityInstance.getActivityName());
      assertEquals("activityType", loadedHistoricActivityInstance.getActivityType());
      assertEquals("processInstanceId", loadedHistoricActivityInstance.getProcessInstanceId());
      assertEquals("processDefinitionId", loadedHistoricActivityInstance.getProcessDefinitionId());
      assertEquals(startTime, loadedHistoricActivityInstance.getStartTime());
      assertEquals(endTime, loadedHistoricActivityInstance.getEndTime());
      assertEquals(Long.valueOf(1000L), loadedHistoricActivityInstance.getDurationInMillis());
    } finally {
      Clock.reset();
      cleanHistoricActivityInstancesFromDatabase("activityId", "processInstanceId");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMarkHistoricActivityInstanceEndedFailsForNonExistingHistoricActivityInstance() {
    HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());
    historicDataService.markHistoricActivityInstanceEnded("nonExsitingActivityId", "nonExistingProcessInstanceId");
  }

  @Test
  public void testUnqiueConstraintsOnHistoricActivityInstance() {
    try {
      Activity activity = mock(Activity.class);
      when(activity.getName()).thenReturn("activityName");
      when(activity.getType()).thenReturn("activityType");

      ProcessInstance processInstance = mock(ProcessInstance.class);
      when(processInstance.getProcessDefinitionId()).thenReturn("processDefinitionId");

      HistoricDataService historicDataService = new HistoricDataServiceImpl(deployer.getCommandExecutor());

      when(activity.getId()).thenReturn("activityIdOne");
      when(processInstance.getId()).thenReturn("processInstanceIdOne");
      historicDataService.createHistoricActivityInstance(activity, processInstance);

      try {
        historicDataService.createHistoricActivityInstance(activity, processInstance);
        fail("unique key constraint violation expected");
      } catch (Exception expected) {
      }

      when(activity.getId()).thenReturn("activityIdTwo");
      when(processInstance.getId()).thenReturn("processInstanceIdOne");
      historicDataService.createHistoricActivityInstance(activity, processInstance);

      when(activity.getId()).thenReturn("activityIdOne");
      when(processInstance.getId()).thenReturn("processInstanceIdTwo");
      historicDataService.createHistoricActivityInstance(activity, processInstance);

      when(activity.getId()).thenReturn("activityIdTwo");
      when(processInstance.getId()).thenReturn("processInstanceIdTwo");
      historicDataService.createHistoricActivityInstance(activity, processInstance);
    } finally {
      cleanHistoricActivityInstancesFromDatabase("activityIdOne", "processInstanceIdOne");
      cleanHistoricActivityInstancesFromDatabase("activityIdTwo", "processInstanceIdOne");
      cleanHistoricActivityInstancesFromDatabase("activityIdOne", "processInstanceIdTwo");
      cleanHistoricActivityInstancesFromDatabase("activityIdTwo", "processInstanceIdTwo");
    }
  }

  private void cleanHistoricProcessInstancesFromDatabase(final String processInstanceId) {
    deployer.getCommandExecutor().execute(new Command<Object>() {
      public Object execute(CommandContext commandContext) {
        commandContext.getPersistenceSession().deleteHistoricProcessInstance(processInstanceId);

        return null;
      }
    });
  }

  private void cleanHistoricActivityInstancesFromDatabase(final String activityId, final String processInstanceId) {
    deployer.getCommandExecutor().execute(new Command<Object>() {
      public Object execute(CommandContext commandContext) {
        commandContext.getPersistenceSession().deleteHistoricActivityInstance(activityId, processInstanceId);

        return null;
      }
    });
  }

}
