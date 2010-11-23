package org.activiti.cycle.impl.db.impl;

import java.util.HashMap;
import java.util.logging.Logger;

import org.activiti.cycle.impl.conf.CycleDbSqlSessionFactory;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfiguration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


public abstract class AbstractCycleDaoMyBatisImpl {
  
  private static HashMap<String, CycleDbSqlSessionFactory> dbFactories = new HashMap<String, CycleDbSqlSessionFactory>();
  protected String processEngineName = DEFAULT_ENGINE;
  protected static String DEFAULT_ENGINE = "DEFAULT_PROCESS_ENGINE";
  
  private static Logger log = Logger.getLogger(AbstractCycleDaoMyBatisImpl.class.getName());
  
  protected SqlSessionFactory getSessionFactory() {
    if (dbFactories.get(processEngineName) == null) {
      synchronized (dbFactories) {
        // lazy initialization, only done once per proces engine!
        if (dbFactories.get(processEngineName) == null) {
          CycleDbSqlSessionFactory factory = new CycleDbSqlSessionFactory();
          factory.configurationCompleted(getProcessEngineConfiguration());
          performDbSchemaCreation(factory, getProcessEngineConfiguration());
          dbFactories.put(processEngineName, factory);
        }
      }
    }
    return dbFactories.get(processEngineName).getSqlSessionFactory();
  }
  
  protected SqlSession openSession() {
    SqlSessionFactory sqlMapper = getSessionFactory();
    return sqlMapper.openSession();
  }

  protected ProcessEngineConfiguration getProcessEngineConfiguration() {
    if (DEFAULT_ENGINE.equals(processEngineName)) {
      return ((ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine()).getProcessEngineConfiguration();
    } else {
      return ((ProcessEngineImpl) ProcessEngines.getProcessEngine(processEngineName)).getProcessEngineConfiguration();
    }
  }
  
  private void performDbSchemaCreation(CycleDbSqlSessionFactory dbSqlSessionFactory, ProcessEngineConfiguration processEngineConfiguration) {
    String dbSchemaStrategy = processEngineConfiguration.getDbSchemaStrategy();
    
    if (ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_DROP_CREATE.equals(dbSchemaStrategy)) {
      try {
        dbSqlSessionFactory.dbSchemaDrop();
      } catch (RuntimeException e) {
        // ignore
      }
    }
    if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_CREATE_DROP.equals(dbSchemaStrategy) || ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_DROP_CREATE.equals(dbSchemaStrategy)
            || ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_CREATE.equals(dbSchemaStrategy)) {
      dbSqlSessionFactory.dbSchemaCreate();
      
    } else if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_CHECK_VERSION.equals(dbSchemaStrategy)) {
      dbSqlSessionFactory.dbSchemaCheckVersion();
      
    } else if (ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_CREATE_IF_NECESSARY.equals(dbSchemaStrategy)) {
      log.warning("Cycle doesn't support '" + ProcessEngineConfiguration.DB_SCHEMA_STRATEGY_CREATE_IF_NECESSARY
              + "' DB strategy at the moment. Nothing is created!");
      // TODO: the check if necessary doesn't work, since the tables are alway created by the engine already!
//      try {
      // dbSqlSessionFactory.dbSchemaCreate();
//        dbSqlSessionFactory.dbSchemaCheckVersion();
//      } catch (Exception e) {
//        if (e.getMessage().indexOf("no activiti tables in db") != -1) {
//          dbSqlSessionFactory.dbSchemaCreate();
//        }
      // }
    }
    

    //
    // if
    // (ProcessEngineConfiguration.DBSCHEMASTRATEGY_DROP_CREATE.equals(dbSchemaStrategy))
    // {
    // try {
    // dbSqlSessionFactory.dbSchemaDrop();
    // } catch (RuntimeException e) {
    // // ignore
    // }
    // }
    //
    // if (DbSchemaStrategy.CREATE_DROP.equals(dbSchemaStrategy) ||
    // ProcessEngineConfiguration.DBSCHEMASTRATEGY_DROP_CREATE.equals(dbSchemaStrategy)
    // ||
    // ProcessEngineConfiguration.DBSCHEMASTRATEGY_CREATE.equals(dbSchemaStrategy))
    // {
    // dbSqlSessionFactory.dbSchemaCreate();
    //
    // } else if (DbSchemaStrategy.CHECK_VERSION.equals(dbSchemaStrategy)) {
    // dbSqlSessionFactory.dbSchemaCheckVersion();
    // }
  }
  
}
