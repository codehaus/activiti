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

package org.activiti.service.api;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.service.api.identity.User;
import org.activiti.service.impl.json.JsonConverter;
import org.activiti.service.impl.persistence.Manager;
import org.activiti.service.impl.util.ClassNameUtil;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


/**
 * @author Tom Baeyens
 */
public class Activiti {
  
  private static Logger log = Logger.getLogger(Activiti.class.getName());

  protected ActivitiConfiguration activitiConfiguration;
  protected JsonConverter jsonConverter = new JsonConverter(this); 
  protected Map<Class<?>, Manager<?>> managers = new HashMap<Class<?>, Manager<?>>();
  protected Mongo mongo = null;
  protected DB db = null;
  
  Activiti(ActivitiConfiguration activitiConfiguration) {
    this.activitiConfiguration = activitiConfiguration;
    init();
    User user = new User();
    user.setId("test");
    user.setPassword("test");
    String feedback = this.getManager(Users.class).insert(user);
    log.log(Level.SEVERE, "user test created " + feedback);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Manager<?>> T getManager(Class<T> managerType) {
    T manager = (T) managers.get(managerType);
    if (manager==null) {
      try {
        String collectionName = "activiti."+ClassNameUtil.getClassNameWithoutPackage(managerType).toLowerCase();
        DBCollection dbCollection = db.getCollection(collectionName);
        manager = managerType.newInstance();
        Method init = findManagerInit(managerType);
        init.setAccessible(true);
        init.invoke(manager, new Object[]{this, dbCollection});
        managers.put(managerType, (T) manager);
        
      } catch (Exception e) {
        throw new ActivitiException("persistence reflection problem", e);
      }
    }
    return manager;
  }

  protected <T> Method findManagerInit(Class<T> managerType) throws NoSuchMethodException {
    for (Method method: managerType.getDeclaredMethods()) {
      if ("init".equals(method.getName())) {
        return method;
      }
    }
    return findManagerInit(managerType.getSuperclass());
  }

  protected void init() {
    try {
    	if(activitiConfiguration.isOnCloud() == false) {
    		mongo = new Mongo();
    	} else {
    		log.log(Level.SEVERE, "running on cloud");
    		mongo = new Mongo(activitiConfiguration.getActivitiHost(), activitiConfiguration.getActivitiPort());
    	}
      db = mongo.getDB(activitiConfiguration.getDatabaseName());
      log.log(Level.SEVERE, "database " + activitiConfiguration.getDatabaseName());
      if(activitiConfiguration.getUserName() != null) {
      	log.log(Level.SEVERE, "authenticating");
      	boolean authenticated = db.authenticate(activitiConfiguration.getUserName(), activitiConfiguration.getPassword().toCharArray());
      	log.log(Level.SEVERE, "authenticated " + authenticated);
      }
      
    } catch (Exception e) {
      throw new RuntimeException("couldn't open mongodb connection: "+e.getMessage(), e);
    }
  }

  public void clean(boolean areYouSure) {
    if (areYouSure) {
      log.info("cleaning database");
      Set<String> collectionNames = db.getCollectionNames();
      for (String collectionName : collectionNames) {
        log.info(collectionName + " -----------------------");
        DBCollection collection = db.getCollection(collectionName);
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
          DBObject dbObject = cursor.next();
          log.info("  " + dbObject);
          collection.remove(dbObject);
        }
        if (!collectionName.startsWith("system.")) {
          collection.drop();
        }
      }
    }
  }

  public void dump() {
    log.info("dumping activiti database contents");
    Set<String> collectionNames = db.getCollectionNames();
    for (String collectionName : collectionNames) {
      log.info(collectionName + " -----------------------");
      DBCollection collection = db.getCollection(collectionName);
      DBCursor cursor = collection.find();
      while (cursor.hasNext()) {
        DBObject dbObject = cursor.next();
        log.info("  " + dbObject);
      }
    }
  }
  
  // getters and setters //////////////////////////////////////////////////////

  public ActivitiConfiguration getActivitiConfiguration() {
    return activitiConfiguration;
  }

  public JsonConverter getJsonConverter() {
    return jsonConverter;
  }
}
