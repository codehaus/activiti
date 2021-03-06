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

package org.activiti.pvm.test;

import java.util.ArrayList;
import java.util.List;

import org.activiti.pvm.ProcessDefinitionBuilder;
import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.process.PvmProcessDefinition;
import org.activiti.pvm.runtime.PvmExecution;
import org.activiti.pvm.runtime.PvmProcessInstance;
import org.activiti.test.pvm.activities.Automatic;
import org.activiti.test.pvm.activities.EmbeddedSubProcess;
import org.activiti.test.pvm.activities.End;
import org.activiti.test.pvm.activities.ParallelGateway;
import org.activiti.test.pvm.activities.WaitState;


/**
 * @author Tom Baeyens
 */
public class PvmEventTest extends PvmTestCase {

  /**
   * +-------+   +-----+
   * | start |-->| end |
   * +-------+   +-----+
   */
  public void testStartEndEvents() {
    EventCollector eventCollector = new EventCollector();
    
    PvmProcessDefinition processDefinition = new ProcessDefinitionBuilder("events")
      .eventListener(EventListener.EVENTNAME_START, eventCollector)
      .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .createActivity("start")
        .initial()
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .startTransition("end")
          .eventListener(eventCollector)
        .endTransition()
      .endActivity()
      .createActivity("end")
        .behavior(new End())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .endActivity()
    .buildProcessDefinition();
    
    PvmProcessInstance processInstance = processDefinition.createProcessInstance();
    processInstance.start();

    List<String> expectedEvents = new ArrayList<String>();
    expectedEvents.add("start on ProcessDefinition(events)");
    expectedEvents.add("start on Activity(start)");
    expectedEvents.add("end on Activity(start)");
    expectedEvents.add("take on (start)-->(end)");
    expectedEvents.add("start on Activity(end)");
    expectedEvents.add("end on Activity(end)");
    expectedEvents.add("end on ProcessDefinition(events)");

    assertEquals("expected "+expectedEvents+", but was \n"+eventCollector+"\n", expectedEvents, eventCollector.events);
  }

  /**
   *         +--------------+
   *         |outerscope    |
   *         | +----------+ |
   *         | |innerscope| |
   * +-----+ | | +----+   | | +---+
   * |start|---->|wait|------>|end|
   * +-----+ | | +----+   | | +---+
   *         | +----------+ |
   *         +--------------+
   */
  public void testNestedActivitiesEventsOnTransitionEvents() {
    EventCollector eventCollector = new EventCollector();
    
    PvmProcessDefinition processDefinition = new ProcessDefinitionBuilder("events")
      .eventListener(EventListener.EVENTNAME_START, eventCollector)
      .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .createActivity("start")
        .initial()
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .startTransition("wait")
          .eventListener(eventCollector)
        .endTransition()
      .endActivity()
      .createActivity("outerscope")
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .createActivity("innerscope")
          .scope()
          .eventListener(EventListener.EVENTNAME_START, eventCollector)
          .eventListener(EventListener.EVENTNAME_END, eventCollector)
          .createActivity("wait")
            .behavior(new WaitState())
            .eventListener(EventListener.EVENTNAME_START, eventCollector)
            .eventListener(EventListener.EVENTNAME_END, eventCollector)
            .transition("end")
          .endActivity()
        .endActivity()
      .endActivity()
      .createActivity("end")
        .behavior(new WaitState())
      .endActivity()
    .buildProcessDefinition();
    
    PvmProcessInstance processInstance = processDefinition.createProcessInstance();
    processInstance.start();
    
    List<String> expectedEvents = new ArrayList<String>();
    expectedEvents.add("start on ProcessDefinition(events)");
    expectedEvents.add("start on Activity(start)");
    expectedEvents.add("end on Activity(start)");
    expectedEvents.add("take on (start)-->(wait)");
    expectedEvents.add("start on Activity(outerscope)");
    expectedEvents.add("start on Activity(innerscope)");
    expectedEvents.add("start on Activity(wait)");

    assertEquals("expected "+expectedEvents+", but was \n"+eventCollector+"\n", expectedEvents, eventCollector.events);

    PvmExecution execution = processInstance.findExecution("wait");
    execution.signal(null, null);
    
    expectedEvents.add("end on Activity(wait)");
    expectedEvents.add("end on Activity(innerscope)");
    expectedEvents.add("end on Activity(outerscope)");

    assertEquals("expected "+expectedEvents+", but was \n"+eventCollector+"\n", expectedEvents, eventCollector.events); 
  }

  /**
   *           +------------------------------+
   * +-----+   | +-----------+   +----------+ |   +---+
   * |start|-->| |startInside|-->|endInsdide| |-->|end|
   * +-----+   | +-----------+   +----------+ |   +---+
   *           +------------------------------+
   */
  public void testEmbeddedSubProcessEvents() {
    EventCollector eventCollector = new EventCollector();
    
    PvmProcessDefinition processDefinition = new ProcessDefinitionBuilder("events")
      .eventListener(EventListener.EVENTNAME_START, eventCollector)
      .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .createActivity("start")
        .initial()
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("embeddedsubprocess")
      .endActivity()
      .createActivity("embeddedsubprocess")
        .scope()
        .behavior(new EmbeddedSubProcess())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .createActivity("startInside")
          .behavior(new Automatic())
          .eventListener(EventListener.EVENTNAME_START, eventCollector)
          .eventListener(EventListener.EVENTNAME_END, eventCollector)
          .transition("endInside")
        .endActivity()
        .createActivity("endInside")
          .behavior(new End())
          .eventListener(EventListener.EVENTNAME_START, eventCollector)
          .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .endActivity()
        .transition("end")
      .endActivity()
      .createActivity("end")
        .behavior(new End())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .endActivity()
    .buildProcessDefinition();
    
    PvmProcessInstance processInstance = processDefinition.createProcessInstance(); 
    processInstance.start();

    List<String> expectedEvents = new ArrayList<String>();
    expectedEvents.add("start on ProcessDefinition(events)");
    expectedEvents.add("start on Activity(start)");
    expectedEvents.add("end on Activity(start)");
    expectedEvents.add("start on Activity(embeddedsubprocess)");
    expectedEvents.add("start on Activity(startInside)");
    expectedEvents.add("end on Activity(startInside)");
    expectedEvents.add("start on Activity(endInside)");
    expectedEvents.add("end on Activity(endInside)");
    expectedEvents.add("end on Activity(embeddedsubprocess)");
    expectedEvents.add("start on Activity(end)");
    expectedEvents.add("end on Activity(end)");
    expectedEvents.add("end on ProcessDefinition(events)");
    
    assertEquals("expected "+expectedEvents+", but was \n"+eventCollector+"\n", expectedEvents, eventCollector.events); 
  }
  
  /**
   *                   +--+
   *              +--->|c1|---+
   *              |    +--+   |
   *              |           v
   * +-----+   +----+       +----+   +---+
   * |start|-->|fork|       |join|-->|end|
   * +-----+   +----+       +----+   +---+
   *              |           ^
   *              |    +--+   |
   *              +--->|c2|---+
   *                   +--+
   */
  public void testSimpleAutmaticConcurrencyEvents() {
    EventCollector eventCollector = new EventCollector();
    
    PvmProcessDefinition processDefinition = new ProcessDefinitionBuilder("events")
      .eventListener(EventListener.EVENTNAME_START, eventCollector)
      .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .createActivity("start")
        .initial()
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("fork")
      .endActivity()
      .createActivity("fork")
        .behavior(new ParallelGateway())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("c1")
        .transition("c2")
      .endActivity()
      .createActivity("c1")
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("join")
      .endActivity()
      .createActivity("c2")
        .behavior(new Automatic())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("join")
      .endActivity()
      .createActivity("join")
        .behavior(new ParallelGateway())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
        .transition("end")
      .endActivity()
      .createActivity("end")
        .behavior(new End())
        .eventListener(EventListener.EVENTNAME_START, eventCollector)
        .eventListener(EventListener.EVENTNAME_END, eventCollector)
      .endActivity()
    .buildProcessDefinition();
    
    PvmProcessInstance processInstance = processDefinition.createProcessInstance(); 
    processInstance.start();
    
    List<String> expectedEvents = new ArrayList<String>();
    expectedEvents.add("start on ProcessDefinition(events)");
    expectedEvents.add("start on Activity(start)");
    expectedEvents.add("end on Activity(start)");
    expectedEvents.add("start on Activity(fork)");
    expectedEvents.add("end on Activity(fork)");
    expectedEvents.add("start on Activity(c1)");
    expectedEvents.add("end on Activity(c1)");
    expectedEvents.add("start on Activity(join)");
    expectedEvents.add("end on Activity(fork)");
    expectedEvents.add("start on Activity(c2)");
    expectedEvents.add("end on Activity(c2)");
    expectedEvents.add("start on Activity(join)");
    expectedEvents.add("end on Activity(join)");
    expectedEvents.add("start on Activity(end)");
    expectedEvents.add("end on Activity(end)");
    expectedEvents.add("end on ProcessDefinition(events)");
    
    assertEquals("expected "+expectedEvents+", but was \n"+eventCollector+"\n", expectedEvents, eventCollector.events); 
  }
}
