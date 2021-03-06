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
package org.activiti.impl.bpmn.parser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.ActivitiException;
import org.activiti.ProcessDefinition;
import org.activiti.activity.ActivityBehavior;
import org.activiti.impl.bpmn.binding.BpmnBinding;
import org.activiti.impl.bpmn.binding.EndEventBinding;
import org.activiti.impl.bpmn.binding.StartEventBinding;
import org.activiti.impl.bpmn.binding.UserTaskBinding;
import org.activiti.impl.definition.ActivityImpl;
import org.activiti.impl.definition.ProcessDefinitionImpl;
import org.activiti.impl.definition.TransitionImpl;
import org.activiti.impl.xml.Element;
import org.activiti.impl.xml.Parse;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class BpmnParse extends Parse {
  
  private static final Logger LOG = Logger.getLogger(BpmnParse.class.getName());
  
  protected static final List<BpmnBinding> ACTIVITY_BEHAVIOR_BINDINGS = Arrays.asList(
    new StartEventBinding(),
    new EndEventBinding(),
    new UserTaskBinding()
  );
  
  protected static final String SCHEMA_RESOURCE = "org/activiti/impl/bpmn/parser/BPMN20.xsd";

  protected List<ProcessDefinitionImpl> processDefinitions = new ArrayList<ProcessDefinitionImpl>();
  protected Class<? extends ProcessDefinition> processDefinitionClass = ProcessDefinitionImpl.class;
  
  BpmnParse(BpmnParser bpmnParser) {
    super(bpmnParser);
    setSchemaResource(SCHEMA_RESOURCE);
  }

  protected void parseProcessDefinitions() {
    // TODO: parse specific definitions data (id, imports, etc)
    for (Element processElement : rootElement.elements("process")) {
      processDefinitions.add(parseProcess(processElement));
    }
  }
  
  protected ProcessDefinitionImpl parseProcess(Element processElement) {
    ProcessDefinitionImpl processDefinition = null;
    try {
      processDefinition = (ProcessDefinitionImpl) processDefinitionClass.newInstance();
    } catch (Exception e) {
      throw new ActivitiException("couldn't instantiate process definition '"+processDefinitionClass+"'", e);
    }
    
    pushContextObject(processDefinition);
    try {
      
      /* Mapping object model - bpmn xml:
       * processDefinition.id -> generated by activiti engine
       * processDefinition.key -> bpmn id (required)
       * processDefinition.name -> bpmn name (optional)
       */
      processDefinition.setKey(processElement.attribute("id"));
      processDefinition.setName(processElement.attribute("name"));
      
      if (LOG.isLoggable(Level.FINE)) {
        LOG.fine("Parsing process " + processDefinition.getKey());
      }
      
      parseActivities(processElement, processDefinition);
      parseSequenceFlow(processElement, processDefinition);
    } finally {
      popContextObject();
    }
    
    return processDefinition;
  }
  
  protected void parseActivities(Element processElement, ProcessDefinitionImpl processDefinition) {
    for (Element element : processElement.elements()) {
      
      BpmnBinding binding = getActivityBehaviorBinding(element);
      if (binding != null) {
        
        String id = element.attribute("id");
        if (LOG.isLoggable(Level.FINE)) {
          LOG.fine("Parsing activity " + id);
        }
        
        ActivityImpl activity = processDefinition.createActivity(id);
        try {
          pushContextObject(activity);
          activity.setName(element.attribute("name"));
          ActivityBehavior activityBehavior = (ActivityBehavior) binding.parse(element, this);
          activity.setActivityBehavior(activityBehavior);
        } finally {
          popContextObject();
        }
      }
    }
  }
  
  protected void parseSequenceFlow(Element processElement, ProcessDefinitionImpl processDefinition) {
    for (Element sequenceFlowElement : processElement.elements("sequenceFlow")) {
      
      String sourceRef = sequenceFlowElement.attribute("sourceRef");
      String destinationRef = sequenceFlowElement.attribute("targetRef");
      ActivityImpl sourceActivity = processDefinition.findActivity(sourceRef);
      ActivityImpl destinationActivity = processDefinition.findActivity(destinationRef);
      
      TransitionImpl transition = sourceActivity.createTransition();
      transition.setId(sequenceFlowElement.attribute("id"));
      transition.setName(sequenceFlowElement.attribute("name"));
      transition.setDestination(destinationActivity);
      
    }
  }
  
  protected BpmnBinding getActivityBehaviorBinding(Element element) {
    for (BpmnBinding activityBehaviorBinding : ACTIVITY_BEHAVIOR_BINDINGS) {
      if (activityBehaviorBinding.matches(element, this)) {
        return activityBehaviorBinding;
      }
    }
    return null;
  }

  public List<ProcessDefinitionImpl> getProcessDefinitions() {
    return processDefinitions;
  }
  
  public BpmnParse processDefinitionClass(Class<? extends ProcessDefinition> processDefinitionClass) {
    this.processDefinitionClass = processDefinitionClass;
    return this;
  }

  @Override
  public BpmnParse execute() {
    super.execute(); 
    parseProcessDefinitions();
    return this;
  }

  @Override
  public BpmnParse name(String name) {
    super.name(name);
    return this;
  }

  @Override
  public BpmnParse sourceInputStream(InputStream inputStream) {
    super.sourceInputStream(inputStream);
    return this;
  }

  @Override
  public BpmnParse sourceResource(String resource, ClassLoader classLoader) {
    super.sourceResource(resource, classLoader);
    return this;
  }

  @Override
  public BpmnParse sourceResource(String resource) {
    super.sourceResource(resource);
    return this;
  }

  @Override
  public BpmnParse sourceString(String string) {
    super.sourceString(string);
    return this;
  }

  @Override
  public BpmnParse sourceUrl(String url) {
    super.sourceUrl(url);
    return this;
  }

  @Override
  public BpmnParse sourceUrl(URL url) {
    super.sourceUrl(url);
    return this;
  }

}
