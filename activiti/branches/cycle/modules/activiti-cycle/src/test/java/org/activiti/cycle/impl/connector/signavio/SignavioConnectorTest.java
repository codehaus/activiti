package org.activiti.cycle.impl.connector.signavio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.Response;

// TODO: We need a way to start 
public class SignavioConnectorTest {

  @Test
  public void test() {
    // TODO: Write :-)
  }

  @Ignore
  @Test
  public void testCreateModelIncludingSvg() throws IOException {
    String file = "c:/bpmn20json.json";
    // String file = "c:/createmodel.json.txt";
    // String file =
    // "c:/Name myTestModel Time_ Tue Aug 10 08_43_43 CEST 2010 _ Signavio.json.txt";

    String jsonData = readFileAsString(file);

    SignavioConnectorConfiguration sigConf = new SignavioConnectorConfiguration();
    SignavioConnector sigCon = new SignavioConnector(sigConf);

    sigCon.login("christian.lipphardt@camunda.com", "cam123");

    sigCon.createNewModel("b4ccfc5748dc482db621c7075aedd5df", "myTestModel - " + Calendar.getInstance().getTime(), jsonData, "testSVG", "testSVG");
  }

  @Ignore
  @Test
  public void testDifferentRepresentations() throws IOException {
    // String signavioModelId = "b68bc46a4879425e8afb4581bffc2f52";
    // String signavioUrl = "http://127.0.0.1:8080";
    // SignavioConnectorConfiguration signavioConf = new
    // SignavioConnectorConfiguration();
    // SignavioConnector signavioCon = new SignavioConnector(signavioConf);
    // signavioCon.login("christian.lipphardt@camunda.com", "cam123");
    // Response signavioResponse =
    // signavioCon.getJsonResponse(signavioConf.getModelUrl() +
    // signavioModelId);
    // System.out.println(signavioResponse.getEntity().getText());

    // String oryxModelId = "";
    // String oryxUrl = "";
    // SignavioConnectorConfiguration oryxConf = new
    // SignavioConnectorConfiguration();
    // SignavioConnector oryxCon = new SignavioConnector(oryxConf);
    // Response oryxResponse = oryxCon.getJsonResponse(oryxConf.getModelUrl() +
    // oryxModelId);
    // System.out.println(oryxResponse.getEntity().getText());

    // String activitiModelId = "1a4deb43e4434738ab30153e119a4b90";
    String activitiModelId = "root-directory;testVersionModeler.oryx.xml";
    String activitiUrl = "http://localhost:8080/activiti-modeler";
    SignavioConnectorConfiguration activitiConf = new SignavioConnectorConfiguration(activitiUrl);
    SignavioConnector activitiCon = new SignavioConnector(activitiConf);
    Response activitiResponse = activitiCon.getJsonResponse(activitiConf.getModelUrl(activitiModelId));
    System.out.println(activitiResponse.getEntity().getText());
  }

  @Ignore
  @Test
  public void testPdfRepresentation() throws IOException {
    String activitiModelId = "root-directory;testVersionModeler.oryx.xml";
    String activitiUrl = "http://localhost:8080/activiti-modeler";
    SignavioConnectorConfiguration activitiConf = new SignavioConnectorConfiguration(activitiUrl);
    SignavioConnector activitiCon = new SignavioConnector(activitiConf);
    Response activitiResponse = activitiCon.getJsonResponse(activitiConf.getModelUrl(activitiModelId) + "/pdf");
    FileOutputStream fos = new FileOutputStream("c:/pdftest.pdf");
    fos.write(activitiResponse.getEntity().getText().getBytes());
    fos.close();
  }

  private static String readFileAsString(String filePath) throws java.io.IOException {
    byte[] buffer = new byte[(int) new File(filePath).length()];
    BufferedInputStream f = null;
    try {
      f = new BufferedInputStream(new FileInputStream(filePath));
      f.read(buffer);
    } finally {
      if (f != null) {
        try {
          f.close();
        } catch (IOException ignored) {
        }
      }
    }
    return new String(buffer, "UTF-8");
  }
}
