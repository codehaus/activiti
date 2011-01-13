package org.activiti.engine.test.el;

import javax.ejb.Local;

@Local
public interface MyEJBLocalHome {
  
  public int doSomething(int x, int y);
  
  public int doAnotherThing();
  
}
