package org.activiti.cycle.impl.connector.signavio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;

public class SignavioConnectorTest {

  @Test
  public void test() {
    // TODO: Write :-)
  }

  @Test
  public void testLocalCreateModel() throws IOException, JSONException {
    String jsonData = readFileAsString("c:/bpmn20json.json");
    // String jsonData = readFileAsString("c:/createmodel.json.txt");
    System.out.println(jsonData);

    JSONObject jsonModel = new JSONObject(jsonData);

    SignavioConnectorConfiguration sigConf = new SignavioConnectorConfiguration();
    SignavioConnector sigCon = new SignavioConnector(sigConf);

    sigCon.login("christian.lipphardt@camunda.com", "cam123");

    Form modelForm = new Form();
    modelForm.add("comment", "Comment myTestModel");
    modelForm.add("description", "Description myTestModel");
    modelForm.add("glossary_xml", new JSONArray().toString());
    // modelForm.add("id", null);
    modelForm.add("json_xml", jsonModel.toString());
    modelForm.add("name", "Name myTestModel Time: " + Calendar.getInstance().getTime());
    modelForm.add("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
    modelForm.add("parent", "/directory/527b2e1a9989473381c6e821890f535f");
    modelForm.add("svg_xml", new JSONObject().toString());
    modelForm.add("type", "BPMN 2.0");
    // modelForm.add("views", new JSONArray().toString());
    Representation modelRep = modelForm.getWebRepresentation();

    Request jsonRequest = new Request(Method.POST, new Reference(sigConf.getModelUrl()), modelRep);
    jsonRequest.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));

    Response response = sigCon.sendRequest(jsonRequest);
  }

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

  @Test
  public void testDifferentRepresentations() throws IOException {
    String modelId = "";
    String oryxUrl = "";
    String signavioUrl = "http://127.0.0.1:8080";
    String activitiUrl = "http://localhost:8080/activiti-modeler";

    SignavioConnectorConfiguration sigConf = new SignavioConnectorConfiguration();
    SignavioConnector sigCon = new SignavioConnector(sigConf);

    Response response = sigCon.getJsonResponse(sigConf.getModelUrl() + modelId);
    System.out.println(response.getEntity().getText());
  }

  @Test
  public void testSvgUrlDecode() throws UnsupportedEncodingException {
    String svg = "%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Aoryx%3D%22http%3A%2F%2Foryx-editor.org%22%20id%3D%22sid-21A15FC6-4DF8-4BF5-A48C-E9A8095E4430%22%20width%3D%22298%22%20height%3D%22130%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20xmlns%3Asvg%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cdefs%3E%3Cmarker%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCstart%22%20refX%3D%221%22%20refY%3D%225%22%20markerUnits%3D%22userSpaceOnUse%22%20markerWidth%3D%2217%22%20markerHeight%3D%2211%22%20orient%3D%22auto%22%3E%20%20%20%20%20%20%3Cpath%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCconditional%22%20d%3D%22M%200%205%20L%208%200%20L%2016%205%20L%208%2010%20L%200%205%22%20fill%3D%22white%22%20stroke%3D%22black%22%20stroke-width%3D%221%22%20display%3D%22none%22%2F%3E%20%20%20%20%3Cpath%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCdefault%22%20d%3D%22M%205%200%20L%2011%2010%22%20fill%3D%22white%22%20stroke%3D%22black%22%20stroke-width%3D%221%22%20display%3D%22none%22%2F%3E%20%20%20%20%20%3C%2Fmarker%3E%3Cmarker%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCend%22%20refX%3D%2215%22%20refY%3D%226%22%20markerUnits%3D%22userSpaceOnUse%22%20markerWidth%3D%2215%22%20markerHeight%3D%2212%22%20orient%3D%22auto%22%3E%20%20%20%20%20%20%3Cpath%20d%3D%22M%200%201%20L%2015%206%20L%200%2011z%22%20fill%3D%22black%22%20stroke%3D%22black%22%20stroke-linejoin%3D%22round%22%20stroke-width%3D%222%22%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DC_sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DC_2%22%2F%3E%20%20%20%20%20%3C%2Fmarker%3E%3Cmarker%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fstart%22%20refX%3D%221%22%20refY%3D%225%22%20markerUnits%3D%22userSpaceOnUse%22%20markerWidth%3D%2217%22%20markerHeight%3D%2211%22%20orient%3D%22auto%22%3E%20%20%20%20%20%20%3Cpath%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fconditional%22%20d%3D%22M%200%205%20L%208%200%20L%2016%205%20L%208%2010%20L%200%205%22%20fill%3D%22white%22%20stroke%3D%22black%22%20stroke-width%3D%221%22%20display%3D%22none%22%2F%3E%20%20%20%20%3Cpath%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fdefault%22%20d%3D%22M%205%200%20L%2011%2010%22%20fill%3D%22white%22%20stroke%3D%22black%22%20stroke-width%3D%221%22%20display%3D%22none%22%2F%3E%20%20%20%20%20%3C%2Fmarker%3E%3Cmarker%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fend%22%20refX%3D%2215%22%20refY%3D%226%22%20markerUnits%3D%22userSpaceOnUse%22%20markerWidth%3D%2215%22%20markerHeight%3D%2212%22%20orient%3D%22auto%22%3E%20%20%20%20%20%20%3Cpath%20d%3D%22M%200%201%20L%2015%206%20L%200%2011z%22%20fill%3D%22black%22%20stroke%3D%22black%22%20stroke-linejoin%3D%22round%22%20stroke-width%3D%222%22%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1F_sid-EF0FA2E1-D830-4815-B487-C81AA966ED1F_2%22%2F%3E%20%20%20%20%20%3C%2Fmarker%3E%3C%2Fdefs%3E%3Cg%20stroke%3D%22black%22%20font-family%3D%22Verdana%2C%20sans-serif%22%20font-size-adjust%3D%22none%22%20font-style%3D%22normal%22%20font-variant%3D%22normal%22%20font-weight%3D%22normal%22%20line-heigth%3D%22normal%22%20font-size%3D%2212%22%3E%3Cg%20class%3D%22stencils%22%20transform%3D%22translate(-140%2C%20-115)%22%3E%3Cg%20class%3D%22me%22%2F%3E%3Cg%20class%3D%22children%22%3E%3Cg%20id%3D%22svg-sid-027823CC-30B0-4425-BE6B-514BA50D9773%22%3E%3Cg%20class%3D%22stencils%22%20transform%3D%22translate(165%2C%20165)%22%3E%3Cg%20class%3D%22me%22%3E%3Cg%20pointer-events%3D%22fill%22%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2E%22%20title%3D%22Start%20Event%22%3E%20%20%20%20%20%20%20%20%3Cdefs%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2E_sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2E_5%22%3E%20%20%20%3CradialGradient%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2Ebackground%22%20cx%3D%2210%25%22%20cy%3D%2210%25%22%20r%3D%22100%25%22%20fx%3D%2210%25%22%20fy%3D%2210%25%22%3E%20%20%20%20%3Cstop%20offset%3D%220%25%22%20stop-color%3D%22%23ffffff%22%20stop-opacity%3D%221%22%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2E_sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2E_6%22%2F%3E%20%20%20%20%3Cstop%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2Efill_el%22%20offset%3D%22100%25%22%20stop-color%3D%22%23ffffff%22%20stop-opacity%3D%221%22%2F%3E%20%20%20%3C%2FradialGradient%3E%20%20%3C%2Fdefs%3E%20%20%20%20%20%20%20%3Ccircle%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2Ebg_frame%22%20cx%3D%2215%22%20cy%3D%2215%22%20r%3D%2215%22%20stroke%3D%22black%22%20fill%3D%22url(%23sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2Ebackground)%20white%22%20stroke-width%3D%221%22%2F%3E%20%20%3Ctext%20font-size%3D%2211%22%20id%3D%22sid-1ACAE8D0-F507-43DD-906C-1810B24BFE2Etext_name%22%20x%3D%2215%22%20y%3D%2232%22%20oryx%3Aalign%3D%22top%20center%22%20stroke%3D%22black%22%20stroke-width%3D%220pt%22%20letter-spacing%3D%22-0.01px%22%20text-anchor%3D%22middle%22%20transform%3D%22rotate(0%2C%2015%2C%2032)%22%20visibility%3D%22inherit%22%20oryx%3AfontSize%3D%2211%22%2F%3E%20%20%20%3C%2Fg%3E%3C%2Fg%3E%3Cg%20class%3D%22children%22%20style%3D%22overflow%3A%20hidden%3B%22%2F%3E%3Cg%20class%3D%22edge%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22controls%22%3E%3Cg%20class%3D%22dockers%22%2F%3E%3Cg%20class%3D%22magnets%22%20transform%3D%22translate(165%2C%20165)%22%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(7%2C%207)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3Cg%20id%3D%22svg-sid-B1824DCB-7B2E-450E-BE76-0BD1023C9EAB%22%3E%3Cg%20class%3D%22stencils%22%20transform%3D%22translate(240%2C%20140)%22%3E%3Cg%20class%3D%22me%22%3E%3Cg%20pointer-events%3D%22fill%22%20oryx%3AminimumSize%3D%2250%2040%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0%22%20title%3D%22Task%22%3E%20%20%20%20%3Cdefs%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_17%22%3E%20%20%20%3CradialGradient%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0background%22%20cx%3D%2210%25%22%20cy%3D%2210%25%22%20r%3D%22100%25%22%20fx%3D%2210%25%22%20fy%3D%2210%25%22%3E%20%20%20%20%3Cstop%20offset%3D%220%25%22%20stop-color%3D%22%23ffffff%22%20stop-opacity%3D%221%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_18%22%2F%3E%20%20%20%20%3Cstop%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0fill_el%22%20offset%3D%22100%25%22%20stop-color%3D%22%23ffffcc%22%20stop-opacity%3D%221%22%2F%3E%20%20%20%3C%2FradialGradient%3E%20%20%3C%2Fdefs%3E%20%20%20%20%3Crect%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0text_frame%22%20oryx%3Aanchors%3D%22bottom%20top%20right%20left%22%20x%3D%221%22%20y%3D%221%22%20width%3D%2294%22%20height%3D%2279%22%20rx%3D%2210%22%20ry%3D%2210%22%20stroke%3D%22none%22%20stroke-width%3D%220%22%20fill%3D%22none%22%2F%3E%20%20%20%20%20%3Crect%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0callActivity%22%20oryx%3Aresize%3D%22vertical%20horizontal%22%20oryx%3Aanchors%3D%22bottom%20top%20right%20left%22%20x%3D%220%22%20y%3D%220%22%20width%3D%22100%22%20height%3D%2280%22%20rx%3D%2210%22%20ry%3D%2210%22%20stroke%3D%22black%22%20stroke-width%3D%224%22%20fill%3D%22none%22%20display%3D%22none%22%2F%3E%20%20%3Crect%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0bg_frame%22%20oryx%3Aresize%3D%22vertical%20horizontal%22%20x%3D%220%22%20y%3D%220%22%20width%3D%22100%22%20height%3D%2280%22%20rx%3D%2210%22%20ry%3D%2210%22%20stroke%3D%22black%22%20stroke-width%3D%221%22%20fill%3D%22url(%23sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0background)%20%23ffffcc%22%2F%3E%20%20%20%3Ctext%20font-size%3D%2212%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0text_name%22%20x%3D%2250%22%20y%3D%2240%22%20oryx%3Aalign%3D%22middle%20center%22%20oryx%3Afittoelem%3D%22text_frame%22%20stroke%3D%22black%22%20stroke-width%3D%220pt%22%20letter-spacing%3D%22-0.01px%22%20text-anchor%3D%22middle%22%20transform%3D%22rotate(0%2C%2050%2C%2040)%22%20visibility%3D%22inherit%22%20oryx%3AfontSize%3D%2212%22%2F%3E%20%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0manualTask%22%20transform%3D%22scale(0.7)%20translate(8%2C%208)%22%20display%3D%22none%22%3E%20%20%20%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0hand%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20fill-opacity%3A%201%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201%3B%22%20d%3D%22M0.5%2C3.751l4.083-3.25c0%2C0%2C11.166%2C0.083%2C12.083%2C0.083s-2.417%2C2.917-1.5%2C2.917%20%20%20%20%20s11.667%2C0%2C12.584%2C0c1.166%2C1.708-0.168%2C3.167-0.834%2C3.667s0.875%2C1.917-1%2C4.417c-0.75%2C0.25%2C0.75%2C1.875-1.333%2C3.333%20%20%20%20%20c-1.167%2C0.583%2C0.583%2C1.542-1.25%2C2.833c-1.167%2C0-20.833%2C0.083-20.833%2C0.083l-2-1.333V3.751z%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0finger%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%202%3B%22%20d%3D%22M%2013.5%207%20L%2027%207%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0finger1%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%202%3B%22%20d%3D%22M%2013.5%2011%20L%2026%2011%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0finger2%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%2014%2014.5%20L%2025%2014.5%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0thumb%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%208.2%203.1%20L%2015%203.1%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0businessRuleTask%22%20transform%3D%22scale(0.7)%20translate(8%2C%208)%22%20display%3D%22none%22%3E%20%20%20%3Crect%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0top%22%20x%3D%220%22%20y%3D%220%22%20width%3D%2222%22%20height%3D%224%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20rgb(179%2C%20177%2C%20179)%3B%20fill-opacity%3A%201%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%2F%3E%20%20%20%3Crect%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0rect%22%20x%3D%220%22%20y%3D%224%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20width%3D%2222%22%20height%3D%2212%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0row%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M%200%2010%20L%2022%2010%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0col%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M%207%204%20L%207%2016%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0scriptTask%22%20transform%3D%22scale(0.7)%20translate(8%2C%208)%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0paper%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M6.402%2C0.5h14.5c0%2C0-5.833%2C2.833-5.833%2C5.583s4.417%2C6%2C4.417%2C9.167%20%20%20%20s-4.167%2C5.083-4.167%2C5.083H0.235c0%2C0%2C5-2.667%2C5-5s-4.583-6.75-4.583-9.25S6.402%2C0.5%2C6.402%2C0.5z%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0line1%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%203.5%204.5%20L%2013.5%204.5%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0line2%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%203.8%208.5%20L%2013.8%208.5%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0line3%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%206.3%2012.5%20L%2016.3%2012.5%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0line4%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%22%20d%3D%22M%206.5%2016.5%20L%2016.5%2016.5%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0userTask%22%20transform%3D%22scale(0.7)%20translate(8%2C%208)%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20rgb(244%2C%20246%2C%20247)%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M0.585%2C24.167h24.083v-7.833c0%2C0-2.333-3.917-7.083-5.167h-9.25%20%20%20%20c-4.417%2C1.333-7.833%2C5.75-7.833%2C5.75L0.585%2C24.167z%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_19%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M%206%2020%20L%206%2024%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_20%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M%2020%2020%20L%2020%2024%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_21%22%2F%3E%20%20%20%3Ccircle%20oryx%3Aanchors%3D%22top%20left%22%20fill%3D%22%23000000%22%20stroke%3D%22%23000000%22%20cx%3D%2213.002%22%20cy%3D%225.916%22%20r%3D%225.417%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_22%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22top%20left%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20rgb(240%2C%20239%2C%20240)%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20d%3D%22M8.043%2C7.083c0%2C0%2C2.814-2.426%2C5.376-1.807s4.624-0.693%2C4.624-0.693%20%20%20%20c0.25%2C1.688%2C0.042%2C3.75-1.458%2C5.584c0%2C0%2C1.083%2C0.75%2C1.083%2C1.5s0.125%2C1.875-1%2C3s-5.5%2C1.25-6.75%2C0S8.668%2C12.834%2C8.668%2C12%20%20%20%20s0.583-1.25%2C1.25-1.917C8.835%2C9.5%2C7.419%2C7.708%2C8.043%2C7.083z%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_23%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0serviceTask%22%20transform%3D%22scale(0.7)%20translate(8%2C%208)%22%20display%3D%22none%22%3E%20%20%20%3Cpolygon%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0teethForeground%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20rgb(255%2C%20255%2C%20255)%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20points%3D%2215.392%2C5.064%2017.954%2C2.502%2020.347%2C4.895%2017.786%2C7.455%2018.729%2C9.732%2022.353%2C9.732%2022.353%2C13.115%2018.731%2C13.115%2017.788%2C15.392%2020.351%2C17.955%2017.958%2C20.347%2015.397%2C17.786%2013.12%2C18.729%2013.12%2C22.353%209.737%2C22.353%209.737%2C18.731%207.46%2C17.788%204.897%2C20.35%202.506%2C17.958%205.066%2C15.397%204.124%2C13.12%200.5%2C13.12%200.5%2C9.737%204.121%2C9.737%205.065%2C7.461%202.503%2C4.898%204.895%2C2.506%207.455%2C5.066%209.732%2C4.125%209.732%2C0.5%2013.116%2C0.5%2013.116%2C4.121%22%2F%3E%20%20%20%3Ccircle%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0ringForeground%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20cx%3D%2211.427%22%20cy%3D%2211.426%22%20r%3D%223.714%22%2F%3E%20%20%20%3Cpolygon%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0teethBackground%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20rgb(255%2C%20255%2C%20255)%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20points%3D%2221.392%2C11.064%2023.954%2C8.502%2026.347%2C10.895%2023.786%2C13.455%2024.729%2C15.732%2028.353%2C15.732%2028.353%2C19.115%2024.731%2C19.115%2023.788%2C21.392%2026.351%2C23.955%2023.958%2C26.347%2021.397%2C23.786%2019.12%2C24.729%2019.12%2C28.353%2015.737%2C28.353%2015.737%2C24.731%2013.46%2C23.788%2010.897%2C26.35%208.506%2C23.958%2011.066%2C21.397%2010.124%2C19.12%206.5%2C19.12%206.5%2C15.737%2010.121%2C15.737%2011.065%2C13.461%208.503%2C10.898%2010.895%2C8.506%2013.455%2C11.066%2015.732%2C10.125%2015.732%2C6.5%2019.116%2C6.5%2019.116%2C10.121%22%2F%3E%20%20%20%3Ccircle%20oryx%3Aanchors%3D%22top%20left%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0ringBackground%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%22%20cx%3D%2217.427%22%20cy%3D%2217.426%22%20r%3D%223.714%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0sendTask%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22left%20top%22%20stroke%3D%22white%22%20fill%3D%22black%22%20stroke-width%3D%221%22%20d%3D%22M8%2C11%20L8%2C21%20L24%2C21%20L24%2C11%20L16%2C17z%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_24%22%2F%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22left%20top%22%20stroke%3D%22white%22%20fill%3D%22black%22%20stroke-width%3D%221%22%20d%3D%22M7%2C10%20L16%2C17%20L25%2010z%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_25%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0receiveTask%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22left%20top%22%20stroke%3D%22black%22%20fill%3D%22none%22%20stroke-width%3D%221%22%20d%3D%22M8%2C11%20L8%2C21%20L24%2C21%20L24%2C11z%20M8%2C11%20L16%2C17%20L24%2C11%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_26%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0none%22%20display%3D%22inherit%22%2F%3E%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0loop%22%20display%3D%22none%22%3E%20%20%20%20%20%20%20%3Cpath%20oryx%3Aanchors%3D%22bottom%22%20style%3D%22opacity%3A%201%3B%20fill%3A%20none%3B%20fill-opacity%3A%201%3B%20stroke%3A%20rgb(0%2C%200%2C%200)%3B%20stroke-width%3A%201.5%3B%20stroke-linecap%3A%20round%3B%20stroke-linejoin%3A%20round%3B%20stroke-miterlimit%3A%202.1%3B%20stroke-dasharray%3A%20none%3B%20stroke-opacity%3A%201%3B%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0path2396%22%20d%3D%22M%2047.608384%2C75.188343%20L%2047.608384%2C78.188343%20L%2044.608384%2C78.188343%20M%2047.608384%2C78.188343%20A%204.875%2C4.875%200%201%201%2051.639336%2C78.189378%22%2F%3E%20%20%20%20%20%3C%2Fg%3E%20%20%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0parallel%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22bottom%22%20fill%3D%22none%22%20stroke%3D%22black%22%20d%3D%22M46%2070%20v8%20M50%2070%20v8%20M54%2070%20v8%22%20stroke-width%3D%222%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_27%22%2F%3E%20%20%3C%2Fg%3E%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0sequential%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22bottom%22%20fill%3D%22none%22%20stroke%3D%22%23000000%22%20stroke-width%3D%222%22%20d%3D%22M46%2C76h10M46%2C72h10%20M46%2C68h10%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_28%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%20%20%3Cg%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0compensation%22%20display%3D%22none%22%3E%20%20%20%3Cpath%20oryx%3Aanchors%3D%22bottom%22%20fill%3D%22none%22%20stroke%3D%22black%22%20d%3D%22M%2062%2074%20L%2066%2070%20L%2066%2078%20L%2062%2074%20L%2062%2070%20L%2058%2074%20L%2062%2078%20L%2062%2074%22%20stroke-width%3D%221%22%20id%3D%22sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_sid-CE6A5879-2EA8-4CCB-93B1-5798E49A72E0_29%22%2F%3E%20%20%3C%2Fg%3E%20%20%20%3C%2Fg%3E%3C%2Fg%3E%3Cg%20class%3D%22children%22%20style%3D%22overflow%3A%20hidden%3B%22%2F%3E%3Cg%20class%3D%22edge%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22controls%22%3E%3Cg%20class%3D%22dockers%22%2F%3E%3Cg%20class%3D%22magnets%22%20transform%3D%22translate(240%2C%20140)%22%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(-7%2C%2012)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(-7%2C%2032)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(-7%2C%2052)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(17%2C%2071)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(42%2C%2071)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(67%2C%2071)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(91%2C%2012)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(91%2C%2032)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(91%2C%2052)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(17%2C%20-7)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(42%2C%20-7)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(67%2C%20-7)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(42%2C%2032)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3Cg%20id%3D%22svg-sid-AF8456FE-3710-409C-911F-65F84282FEF0%22%3E%3Cg%20class%3D%22stencils%22%20transform%3D%22translate(385%2C%20166)%22%3E%3Cg%20class%3D%22me%22%3E%3Cg%20pointer-events%3D%22fill%22%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34%22%20title%3D%22End%20Event%22%3E%20%20%20%20%20%20%3Cdefs%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34_sid-48F25648-1CA5-40DA-957A-F096F7251B34_5%22%3E%20%20%20%3CradialGradient%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34background%22%20cx%3D%2210%25%22%20cy%3D%2210%25%22%20r%3D%22100%25%22%20fx%3D%2210%25%22%20fy%3D%2210%25%22%3E%20%20%20%20%3Cstop%20offset%3D%220%25%22%20stop-color%3D%22%23ffffff%22%20stop-opacity%3D%221%22%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34_sid-48F25648-1CA5-40DA-957A-F096F7251B34_6%22%2F%3E%20%20%20%20%3Cstop%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34fill_el%22%20offset%3D%22100%25%22%20stop-color%3D%22%23ffffff%22%20stop-opacity%3D%221%22%2F%3E%20%20%20%3C%2FradialGradient%3E%20%20%3C%2Fdefs%3E%20%20%20%20%20%20%20%3Ccircle%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34bg_frame%22%20cx%3D%2214%22%20cy%3D%2214%22%20r%3D%2214%22%20stroke%3D%22black%22%20fill%3D%22url(%23sid-48F25648-1CA5-40DA-957A-F096F7251B34background)%20white%22%20stroke-width%3D%223%22%2F%3E%20%20%3Ctext%20font-size%3D%2211%22%20id%3D%22sid-48F25648-1CA5-40DA-957A-F096F7251B34text_name%22%20x%3D%2214%22%20y%3D%2230%22%20oryx%3Aalign%3D%22top%20center%22%20stroke%3D%22black%22%20stroke-width%3D%220pt%22%20letter-spacing%3D%22-0.01px%22%20text-anchor%3D%22middle%22%20transform%3D%22rotate(0%2C%2014%2C%2030)%22%20visibility%3D%22inherit%22%20oryx%3AfontSize%3D%2211%22%2F%3E%20%20%20%3C%2Fg%3E%3C%2Fg%3E%3Cg%20class%3D%22children%22%20style%3D%22overflow%3A%20hidden%3B%22%2F%3E%3Cg%20class%3D%22edge%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22controls%22%3E%3Cg%20class%3D%22dockers%22%2F%3E%3Cg%20class%3D%22magnets%22%20transform%3D%22translate(385%2C%20166)%22%3E%3Cg%20pointer-events%3D%22all%22%20display%3D%22none%22%20transform%3D%22translate(6%2C%206)%22%3E%3Ccircle%20cx%3D%228%22%20cy%3D%228%22%20r%3D%224%22%20stroke%3D%22none%22%20fill%3D%22red%22%20fill-opacity%3D%220.3%22%2F%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3Cg%20class%3D%22edge%22%3E%3Cg%20id%3D%22svg-sid-8545EE45-1B1A-478A-A12C-0425B6D00081%22%3E%3Cg%20class%3D%22stencils%22%3E%3Cg%20class%3D%22me%22%20title%3D%22Sequence%20Flow%22%3E%3Cg%20pointer-events%3D%22painted%22%3E%3Cpath%20d%3D%22M195.609375%20180L239.15625%20180%20%22%20stroke%3D%22black%22%20fill%3D%22none%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20marker-start%3D%22url(%23sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCstart)%22%20marker-end%3D%22url(%23sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCend)%22%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DC_1%22%2F%3E%3C%2Fg%3E%3Ctext%20id%3D%22sid-9BBB328E-4B4D-4A82-AA85-BB855E0BC6DCcondition%22%20x%3D%22204.609%22%20y%3D%22171%22%20oryx%3AedgePosition%3D%22startTop%22%20stroke-width%3D%220pt%22%20letter-spacing%3D%22-0.01px%22%20text-anchor%3D%22start%22%20transform%3D%22rotate(360%2C%20195.609%2C%20180)%22%20visibility%3D%22inherit%22%20oryx%3AfontSize%3D%2212%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22children%22%20style%3D%22overflow%3A%20hidden%3B%22%2F%3E%3Cg%20class%3D%22edge%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22controls%22%3E%3Cg%20class%3D%22dockers%22%2F%3E%3Cg%20class%3D%22magnets%22%2F%3E%3C%2Fg%3E%3C%2Fg%3E%3Cg%20id%3D%22svg-sid-C33A7DAB-2607-4A38-A5BE-7D6EF22A4C86%22%3E%3Cg%20class%3D%22stencils%22%3E%3Cg%20class%3D%22me%22%20title%3D%22Sequence%20Flow%22%3E%3Cg%20pointer-events%3D%22painted%22%3E%3Cpath%20d%3D%22M340.390625%20180L384.375%20180%20%22%20stroke%3D%22black%22%20fill%3D%22none%22%20stroke-width%3D%222%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20marker-start%3D%22url(%23sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fstart)%22%20marker-end%3D%22url(%23sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fend)%22%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1F_1%22%2F%3E%3C%2Fg%3E%3Ctext%20id%3D%22sid-EF0FA2E1-D830-4815-B487-C81AA966ED1Fcondition%22%20x%3D%22349.391%22%20y%3D%22171%22%20oryx%3AedgePosition%3D%22startTop%22%20stroke-width%3D%220pt%22%20letter-spacing%3D%22-0.01px%22%20text-anchor%3D%22start%22%20transform%3D%22rotate(360%2C%20340.391%2C%20180)%22%20visibility%3D%22inherit%22%20oryx%3AfontSize%3D%2212%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22children%22%20style%3D%22overflow%3A%20hidden%3B%22%2F%3E%3Cg%20class%3D%22edge%22%2F%3E%3C%2Fg%3E%3Cg%20class%3D%22controls%22%3E%3Cg%20class%3D%22dockers%22%2F%3E%3Cg%20class%3D%22magnets%22%2F%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fg%3E%3C%2Fsvg%3E";
    System.out.println(URLDecoder.decode(svg, "UTF-8"));
  }

  @Test
  public void testBpmn20XmlExportWithJsonData() throws IOException {
    SignavioConnectorConfiguration sigConf = new SignavioConnectorConfiguration();
    SignavioConnector sigCon = new SignavioConnector(sigConf);

    sigCon.login("christian.lipphardt@camunda.com", "cam123");

    String url = "http://127.0.0.1:8080/editor/bpmn2_0serialization";
    String jsonData = readFileAsString("c:/bpmn20json.json");

    Form dataForm = new Form();
    dataForm.add("data", jsonData);

    Request request = new Request(Method.POST, url, dataForm.getWebRepresentation());
    Response response = sigCon.sendRequest(request);
    System.out.println(response.getEntity().getText());
  }

  private static String readFileAsString(String filePath) throws java.io.IOException {
    byte[] buffer = new byte[(int) new File(filePath).length()];
    BufferedInputStream f = null;
    try {
      f = new BufferedInputStream(new FileInputStream(filePath));
      f.read(buffer);
    } finally {
      if (f != null)
        try {
          f.close();
        } catch (IOException ignored) {
        }
    }
    return new String(buffer, "UTF-8");
  }
}
