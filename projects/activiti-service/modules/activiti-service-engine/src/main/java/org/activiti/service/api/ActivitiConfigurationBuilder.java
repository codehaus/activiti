package org.activiti.service.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class ActivitiConfigurationBuilder {
	
	protected String activitiHost = "activiti.com";
  protected int activitiPort = 80;
  protected String databaseName = "activiti";
	
	public ActivitiConfigurationBuilder() {
	}
	
	public Activiti buildActiviti() {
		ActivitiConfiguration config = new ActivitiConfiguration();
		fillConfig(config);
		return new Activiti(config);
	}
	
	private void fillConfig(ActivitiConfiguration config) {
		boolean onCloundFoundry = System.getenv().containsKey("VCAP_SERVICES");
		if(onCloundFoundry == true) {
			fillConfigCloundFoundry(config);
		} else {
			fillConfigDefault(config);
		}
	}
	
	private void fillConfigDefault(ActivitiConfiguration config) {
		config.setActivitiHost(activitiHost);
		config.setActivitiPort(activitiPort);
		config.setDatabaseName(databaseName);
	}
	
	private void fillConfigCloundFoundry(ActivitiConfiguration config) {
		String serviceParam = System.getenv().get("VCAP_SERVICES");
    Object jsonObj = JSONValue.parse(serviceParam);
    JSONObject serviceObject = (JSONObject) jsonObj;
    JSONArray mongoArray = (JSONArray) serviceObject.get("mongodb-1.8");
    JSONObject mongoObject = (JSONObject) mongoArray.get(0);
    JSONObject credentialsObject = (JSONObject) mongoObject.get("credentials");
    config.setOnCloud(true);
    config.setActivitiHost(credentialsObject.get("hostname").toString());
    config.setUserName(credentialsObject.get("username").toString());
    config.setPassword(credentialsObject.get("password").toString());
    config.setDatabaseName(credentialsObject.get("db").toString());
    config.setActivitiPort(Integer.valueOf(credentialsObject.get("port").toString()));
    
	}

}
