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
package org.activiti.test.db;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiWrongDbException;
import org.activiti.engine.DbSchemaStrategy;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineBuilder;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.persistence.db.DbSqlSession;
import org.activiti.engine.impl.persistence.db.DbSqlSessionFactory;
import org.activiti.test.LogInitializer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;

/**
 * @author Tom Baeyens
 */
public class ProcessEngineInitializationTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public LogInitializer logInitializer = new LogInitializer();

  @Test
  public void testNoTables() {

    exception.expect(ActivitiException.class);
    exception.expectMessage("no activiti tables in db.  set property db.schema.strategy=create-drop in activiti.properties for automatic schema creation");

    new ProcessEngineBuilder().configureFromPropertiesResource("org/activiti/test/db/activiti.properties").buildProcessEngine();

  }

  @Test
  public void testVersionMismatch() {
    // first create the schema
    ProcessEngineImpl processEngine = (ProcessEngineImpl) new ProcessEngineBuilder().configureFromPropertiesResource(
            "org/activiti/test/db/activiti.properties").setDbSchemaStrategy(DbSchemaStrategy.CREATE_DROP).buildProcessEngine();

    // then update the version to something that is different to the library
    // version
    DbSqlSessionFactory dbSqlSessionFactory = (DbSqlSessionFactory) processEngine.getProcessEngineConfiguration().getSessionFactories().get(DbSqlSession.class);
    SqlSessionFactory sqlSessionFactory = dbSqlSessionFactory.getSqlSessionFactory();
    SqlSession sqlSession = sqlSessionFactory.openSession();
    boolean success = false;
    try {
      Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put("name", "schema.version");
      parameters.put("value", "25.7");
      parameters.put("revision", new Integer(1));
      parameters.put("newRevision", new Integer(2));
      sqlSession.update("updateProperty", parameters);
      success = true;
    } catch (Exception e) {
      throw new ActivitiException("couldn't update db schema version", e);
    } finally {
      if (success) {
        sqlSession.commit(true);
      } else {
        sqlSession.rollback(true);
      }
      sqlSession.close();
    }

    exception.expect(ActivitiWrongDbException.class);
    exception.expect(new TypeSafeMatcher<ActivitiWrongDbException>() {

      @Override
      public boolean matchesSafely(ActivitiWrongDbException e) {
        return e.getMessage().contains("version mismatch") && "25.7".equals(e.getDbVersion()) && ProcessEngine.VERSION == e.getLibraryVersion();
      }
      public void describeTo(Description description) {
        description.appendText("'version mismatch' with dbVersion=25.7 and libraryVersion=").appendValue(ProcessEngine.VERSION);
      }
    });

    // now we can see what happens if when a process engine is being
    // build with a version mismatch between library and db tables
    new ProcessEngineBuilder().configureFromPropertiesResource("org/activiti/test/db/activiti.properties")
            .setDbSchemaStrategy(DbSchemaStrategy.CHECK_VERSION).buildProcessEngine();

    // closing the original process engine to drop the db tables
    processEngine.close();
  }
}
