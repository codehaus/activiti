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

package org.activiti.engine.impl.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.util.ClassNameUtil;
import org.apache.ibatis.session.SqlSessionFactory;


/**
 * @author Tom Baeyens
 */
public class DbSqlSessionFactory implements SessionFactory {

  protected static final Map<String, Map<String, String>> databaseSpecificStatements = new HashMap<String, Map<String,String>>();

  static {
	  //mysql specific  
    addDatabaseSpecificStatement("mysql", "selectNextJobsToExecute", "selectNextJobsToExecute_mysql");
    addDatabaseSpecificStatement("mysql", "selectProcessDefinitionsByQueryCriteria", "selectProcessDefinitionsByQueryCriteria_mysql");
    addDatabaseSpecificStatement("mysql", "selectProcessDefinitionCountByQueryCriteria", "selectProcessDefinitionCountByQueryCriteria_mysql");
    addDatabaseSpecificStatement("mysql", "selectDeploymentsByQueryCriteria", "selectDeploymentsByQueryCriteria_mysql");
    addDatabaseSpecificStatement("mysql", "selectDeploymentCountByQueryCriteria", "selectDeploymentCountByQueryCriteria_mysql");
    
    //postgres specific
    addDatabaseSpecificStatement("postgres", "insertByteArray", "insertByteArray_postgres");
    addDatabaseSpecificStatement("postgres", "updateByteArray", "updateByteArray_postgres");
    addDatabaseSpecificStatement("postgres", "selectByteArrayById", "selectByteArrayById_postgres");
    addDatabaseSpecificStatement("postgres", "selectResourceByDeploymentIdAndResourceName", "selectResourceByDeploymentIdAndResourceName_postgres");
    addDatabaseSpecificStatement("postgres", "selectResourcesByDeploymentId", "selectResourcesByDeploymentId_postgres");
  }
  
  protected String databaseType;
  protected SqlSessionFactory sqlSessionFactory;
  protected IdGenerator idGenerator;
  protected Map<String, String> statementMappings;
  protected Map<Class<?>,String>  insertStatements = Collections.synchronizedMap(new HashMap<Class<?>, String>());
  protected Map<Class<?>,String>  updateStatements = Collections.synchronizedMap(new HashMap<Class<?>, String>());
  protected Map<Class<?>,String>  deleteStatements = Collections.synchronizedMap(new HashMap<Class<?>, String>());
  protected Map<Class<?>,String>  selectStatements = Collections.synchronizedMap(new HashMap<Class<?>, String>());
  protected boolean isDbIdentityUsed = true;
  protected boolean isDbHistoryUsed = true;
  protected boolean isDbCycleUsed = true;

  public Class< ? > getSessionType() {
    return DbSqlSession.class;
  }

  public Session openSession() {
    return new DbSqlSession(this);
  }
  
  // insert, update and delete statements /////////////////////////////////////
  
  public String getInsertStatement(PersistentObject object) {
    return getStatement(object.getClass(), insertStatements, "insert");
  }

  public String getUpdateStatement(PersistentObject object) {
    return getStatement(object.getClass(), updateStatements, "update");
  }

  public String getDeleteStatement(Class<?> persistentObjectClass) {
    return getStatement(persistentObjectClass, deleteStatements, "delete");
  }

  public String getSelectStatement(Class<?> persistentObjectClass) {
    return getStatement(persistentObjectClass, selectStatements, "select");
  }

  private String getStatement(Class<?> persistentObjectClass, Map<Class<?>,String> cachedStatements, String prefix) {
    String statement = cachedStatements.get(persistentObjectClass);
    if (statement!=null) {
      return statement;
    }
    statement = prefix+ClassNameUtil.getClassNameWithoutPackage(persistentObjectClass);
    statement = statement.substring(0, statement.length()-6);
    cachedStatements.put(persistentObjectClass, statement);
    return statement;
  }

  // db specific mappings /////////////////////////////////////////////////////
  
  protected static void addDatabaseSpecificStatement(String databaseType, String activitiStatement, String ibatisStatement) {
    Map<String, String> specificStatements = databaseSpecificStatements.get(databaseType);
    if (specificStatements == null) {
      specificStatements = new HashMap<String, String>();
      databaseSpecificStatements.put(databaseType, specificStatements);
    }
    specificStatements.put(activitiStatement, ibatisStatement);
  }
  
  public String mapStatement(String statement) {
    if (statementMappings==null) {
      return statement;
    }
    String mappedStatement = statementMappings.get(statement);
    return (mappedStatement!=null ? mappedStatement : statement);
  }
  
  // customized getters and setters ///////////////////////////////////////////
  
  public void setDatabaseType(String databaseType) {
    this.databaseType = databaseType;
    this.statementMappings = databaseSpecificStatements.get(databaseType);
  }

  // getters and setters //////////////////////////////////////////////////////
  
  public SqlSessionFactory getSqlSessionFactory() {
    return sqlSessionFactory;
  }
  
  public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }
  
  public IdGenerator getIdGenerator() {
    return idGenerator;
  }
  
  public void setIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  
  public String getDatabaseType() {
    return databaseType;
  }

  
  public Map<String, String> getStatementMappings() {
    return statementMappings;
  }

  
  public void setStatementMappings(Map<String, String> statementMappings) {
    this.statementMappings = statementMappings;
  }

  
  public Map<Class< ? >, String> getInsertStatements() {
    return insertStatements;
  }

  
  public void setInsertStatements(Map<Class< ? >, String> insertStatements) {
    this.insertStatements = insertStatements;
  }

  
  public Map<Class< ? >, String> getUpdateStatements() {
    return updateStatements;
  }

  
  public void setUpdateStatements(Map<Class< ? >, String> updateStatements) {
    this.updateStatements = updateStatements;
  }

  
  public Map<Class< ? >, String> getDeleteStatements() {
    return deleteStatements;
  }

  
  public void setDeleteStatements(Map<Class< ? >, String> deleteStatements) {
    this.deleteStatements = deleteStatements;
  }

  
  public Map<Class< ? >, String> getSelectStatements() {
    return selectStatements;
  }

  
  public void setSelectStatements(Map<Class< ? >, String> selectStatements) {
    this.selectStatements = selectStatements;
  }

  public boolean isDbIdentityUsed() {
    return isDbIdentityUsed;
  }

  
  public void setDbIdentityUsed(boolean isDbIdentityUsed) {
    this.isDbIdentityUsed = isDbIdentityUsed;
  }

  
  public boolean isDbHistoryUsed() {
    return isDbHistoryUsed;
  }

  
  public void setDbHistoryUsed(boolean isDbHistoryUsed) {
    this.isDbHistoryUsed = isDbHistoryUsed;
  }

  
  public boolean isDbCycleUsed() {
    return isDbCycleUsed;
  }

  
  public void setDbCycleUsed(boolean isDbCycleUsed) {
    this.isDbCycleUsed = isDbCycleUsed;
  }
}
