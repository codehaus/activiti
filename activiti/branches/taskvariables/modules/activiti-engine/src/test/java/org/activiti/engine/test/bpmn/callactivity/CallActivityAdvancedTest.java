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

package org.activiti.engine.test.bpmn.callactivity;

import java.util.Date;
import java.util.List;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.Deployment;

/**
 * @author Joram Barrez
 */
public class CallActivityAdvancedTest extends PluggableActivitiTestCase {

  @Deployment(resources = {
    "org/activiti/engine/test/bpmn/callactivity/CallActivity.testCallSimpleSubProcess.bpmn20.xml", 
    "org/activiti/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml" 
  })
  public void testCallSimpleSubProcess() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("callSimpleSubProcess");
    
    // one task in the subprocess should be active after starting the process instance
    TaskQuery taskQuery = taskService.createTaskQuery();
    Task taskBeforeSubProcess = taskQuery.singleResult();
    assertEquals("Task before subprocess", taskBeforeSubProcess.getName());
    
    // Completing the task continues the process which leads to calling the subprocess
    taskService.complete(taskBeforeSubProcess.getId());
    Task taskInSubProcess = taskQuery.singleResult();
    assertEquals("Task in subprocess", taskInSubProcess.getName());
    
    // Completing the task in the subprocess, finishes the subprocess
    taskService.complete(taskInSubProcess.getId());
    Task taskAfterSubProcess = taskQuery.singleResult();
    assertEquals("Task after subprocess", taskAfterSubProcess.getName());
    
    // Completing this task end the process instance
    taskService.complete(taskAfterSubProcess.getId());
    assertProcessEnded(processInstance.getId());
  }
  
  /**
   * Test case for a possible tricky case: reaching the end event
   * of the subprocess leads to an end event in the super process instance.
   */
  @Deployment(resources = {
    "org/activiti/engine/test/bpmn/callactivity/CallActivity.testSubProcessEndsSuperProcess.bpmn20.xml", 
    "org/activiti/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml" })
  public void testSubProcessEndsSuperProcess() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("subProcessEndsSuperProcess");
    
    // one task in the subprocess should be active after starting the process instance
    TaskQuery taskQuery = taskService.createTaskQuery();
    Task taskBeforeSubProcess = taskQuery.singleResult();
    assertEquals("Task in subprocess", taskBeforeSubProcess.getName());
    
    // Completing this task ends the subprocess which leads to the end of the whole process instance
    taskService.complete(taskBeforeSubProcess.getId());
    assertProcessEnded(processInstance.getId());
    assertEquals(0, runtimeService.createExecutionQuery().list().size());
  }
  
  @Deployment(resources = {
    "org/activiti/engine/test/bpmn/callactivity/CallActivity.testCallParallelSubProcess.bpmn20.xml", 
    "org/activiti/engine/test/bpmn/callactivity/simpleParallelSubProcess.bpmn20.xml"})
  public void testCallParallelSubProcess() {
    runtimeService.startProcessInstanceByKey("callParallelSubProcess");
  
    // The two tasks in the parallel subprocess should be active
    TaskQuery taskQuery = taskService
      .createTaskQuery()
      .orderByTaskName()
      .asc();
    List<Task> tasks = taskQuery.list();
    assertEquals(2, tasks.size());
    
    Task taskA = tasks.get(0);
    Task taskB = tasks.get(1);
    assertEquals("Task A", taskA.getName());
    assertEquals("Task B", taskB.getName());

    // Completing the first task should not end the subprocess
    taskService.complete(taskA.getId());
    assertEquals(1, taskQuery.list().size());
    assertEquals(2, runtimeService.createExecutionQuery().count());
    
    // Completing the second task should end the subprocess and end the whole process instance
    taskService.complete(taskB.getId());
    assertEquals(0, runtimeService.createExecutionQuery().count());
  }
  
  @Deployment(resources = {
    "org/activiti/engine/test/bpmn/callactivity/CallActivity.testTimerOnCallActivity.bpmn20.xml",
    "org/activiti/engine/test/bpmn/callactivity/simpleSubProcess.bpmn20.xml"})
  public void testTimerOnCallActivity() {
    Date startTime = ClockUtil.getCurrentTime();
    
    // After process start, the task in the subprocess should be active
    runtimeService.startProcessInstanceByKey("timerOnCallActivity");
    TaskQuery taskQuery = taskService.createTaskQuery();
    Task taskInSubProcess = taskQuery.singleResult();
    assertEquals("Task in subprocess", taskInSubProcess.getName());
    
    // When the timer on the subprocess is fired, the complete subprocess is destroyed
    ClockUtil.setCurrentTime(new Date(startTime.getTime() + (6 * 60 * 1000))); // + 6 minutes, timer fires on 5 minutes
    waitForJobExecutorToProcessAllJobs(10000, 100);
    
    Task escalatedTask = taskQuery.singleResult();
    assertEquals("Escalated Task", escalatedTask.getName());
    
    // Completing the task ends the complete process
    taskService.complete(escalatedTask.getId());
    assertEquals(0, runtimeService.createExecutionQuery().list().size());
  }
}
