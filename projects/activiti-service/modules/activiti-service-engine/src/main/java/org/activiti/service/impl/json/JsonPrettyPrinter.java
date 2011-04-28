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

package org.activiti.service.impl.json;

import java.util.Set;
import java.util.TreeSet;

import org.activiti.service.api.ActivitiException;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/**
 * @author Tom Baeyens
 */
public abstract class JsonPrettyPrinter {

  public static String toJsonPrettyPrint(DBObject jsonObject) {
    StringBuffer jsonText = new StringBuffer();
    jsonObjectToTextFormatted(jsonObject, 0, jsonText);
    return jsonText.toString();
  }

  static String spaces = "                    ";
  
  public static void jsonObjectToTextFormatted(BasicDBObject jsonObject, int indent, StringBuffer jsonText) {
    jsonText.append("{ ");
    appendNewLine(indent+2, jsonText);
    Set<String> keys = new TreeSet<String>(jsonObject.keySet());
    boolean isFirst = true;
    for (String key : keys) {
      if (isFirst) {
        isFirst = false;
      } else {
        jsonText.append(", "); 
        appendNewLine(indent+2, jsonText);
      }
      jsonText.append("\""); 
      jsonText.append(key);
      jsonText.append("\" : ");
      jsonObjectToTextFormatted(jsonObject.get(key), indent+2, jsonText);
    }
    appendNewLine(indent, jsonText);
    jsonText.append("}");
  }

  public static void jsonObjectToTextFormatted(BasicDBList jsonList, int indent, StringBuffer jsonText) {
    jsonText.append("[ ");
    appendNewLine(indent+2, jsonText);
    boolean isFirst = true;
    for (Object element : jsonList) {
      if (isFirst) {
        isFirst = false;
      } else {
        jsonText.append(", "); 
        appendNewLine(indent+2, jsonText);
      }
      jsonObjectToTextFormatted(element, indent+2, jsonText);
    }
    appendNewLine(indent, jsonText);
    jsonText.append("]");
  }

  private static void appendNewLine(int indent, StringBuffer jsonText) {
    jsonText.append("\n");
    jsonText.append(spaces.substring(0,indent));
  }

  public static void jsonObjectToTextFormatted(Object jsonObject, int indent, StringBuffer jsonText) {
    if (jsonObject instanceof BasicDBObject) {
      jsonObjectToTextFormatted((BasicDBObject) jsonObject, indent, jsonText);
    } else if (jsonObject instanceof BasicDBList) {
      jsonObjectToTextFormatted((BasicDBList) jsonObject, indent, jsonText);
    } else if (jsonObject instanceof String) {
      jsonText.append("\""); 
      jsonText.append(jsonObject);
      jsonText.append("\"");
    } else if (jsonObject instanceof ObjectId) {
      jsonText.append("{ \"$oid\" : \""+jsonObject.toString()+"\" }");
    } else {
      throw new ActivitiException("couldn't pretty print "+jsonObject.getClass().getName());
    }
  }
}
