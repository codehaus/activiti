package org.activiti.cycle.impl.connector.ci.hudson.action;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.activiti.cycle.RepositoryException;
import org.activiti.engine.impl.util.IoUtil;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

/**
 * Allows to create a basic Maven2/3 continuous integration job in Hudson/Jenkins.
 * 
 * @author christian.lipphardt@camunda.com
 */
public class CreateHudsonJob {

	public static final String CONFIG_XML_TEMPLATE = "template.config.xml";

	public static final String PARAM_HUDSON_CREATE_JOB = "createItem";
	public static final String PATTERN_SVN_LOCATION = "@SVN_LOCATION@";
	public static final String PATTERN_EMAIL_ADDRESSES = "@EMAIL_ADDRESSES@";
	
	public static final String DEFAULT_JOB_NAME = "CreatedByActivitiCycle";

	public Boolean createCIJobForProject(String hudsonUrl,
			String projectSVNUrl, String jobName, List<String> emailAddresses) {
		// TODO: check supplied values or not ;)
		if (hudsonUrl == null || hudsonUrl.length() ==0 || projectSVNUrl == null || projectSVNUrl.length() == 0) {
			throw new RepositoryException("HudsonUrl and ProjectSVNUrl must not be null or empty");
		}

		// retrieve template.config.xml and enrich it with given data
		String configXml = getTemplate();
		
		// convert emails to single string with whitespace between them
		String emailString = transformEmailAddress(emailAddresses);
		
		configXml = replaceVarsInTemplate(configXml, projectSVNUrl, emailString);
		
		hudsonUrl = transformUrl(hudsonUrl);
		Reference reference = new Reference(hudsonUrl + PARAM_HUDSON_CREATE_JOB);
		
		if (jobName == null || jobName.length() == 0) {
			// create a default name
			jobName = DEFAULT_JOB_NAME + "-" + projectSVNUrl;
		}
		reference.addQueryParameter("name", jobName);
		ClientResource clientResource = new ClientResource(reference);

		StringRepresentation configFileRep = new StringRepresentation(configXml, MediaType.APPLICATION_XML);
		try {
			Representation postResult = clientResource.post(configFileRep);
			return Boolean.TRUE;
		} catch (Exception e) {
			throw new RepositoryException("Encountered an error while creating Hudson CI Job for Project '" + jobName + "'", e);
		} finally {
			clientResource.release();
			Engine.clearThreadLocalVariables();
		}
	}
	
	private String getTemplate() {
		InputStream is = null;
		try {
			is = this.getClass().getResourceAsStream(CONFIG_XML_TEMPLATE);
			if (is == null) {
				throw new RepositoryException("Template '" + CONFIG_XML_TEMPLATE + "' doesn't exist in classpath");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			return sb.toString();
		} catch (Exception ex) {
			throw new RepositoryException("Exception while creating template '" + CONFIG_XML_TEMPLATE + "'", ex);
		} finally {
			IoUtil.closeSilently(is);
		}
	}
	
	private String transformEmailAddress(List<String> emailAddresses) {
		StringBuffer emailString = new StringBuffer();
		
		for (String emailAddress : emailAddresses) {
			emailString.append(emailAddress + " ");
		}

		return emailString.toString();
	}

	private String transformUrl(String url) {
		if (url != null && !url.endsWith("/")) {
			url = url + "/";
		}
		
		return url;
	}
	
	private String replaceVarsInTemplate(String template, String projectSVNUrl, String emailAddresses) {
		template = template.replaceFirst(PATTERN_SVN_LOCATION, projectSVNUrl);
		template = template.replaceFirst(PATTERN_EMAIL_ADDRESSES, emailAddresses);
		
		return template;
	}
	
	public static void main(String[] args) {
		String hudsonUrl = "http://localhost:8080";
		String projectSVNUrl = "projectLocationinSVN";
		String jobName = "JavaGenerated-" + System.currentTimeMillis();
		List<String> emailAddresses = new ArrayList<String>();

		emailAddresses.add("christian.lipphardt@camunda.com");
		emailAddresses.add("bernd.ruecker@camunda.com");
		
		CreateHudsonJob createHudsonJob = new CreateHudsonJob();
		Boolean successful = createHudsonJob.createCIJobForProject(hudsonUrl,
				projectSVNUrl, jobName, emailAddresses);
		if (successful) {
			System.out.println("Successfully created Hudson CI Job for Project '" + jobName + "'");
		} else {
			System.out.println("Unable to create Hudson CI Job for Project '" + jobName + "'");
		}
	}

}
