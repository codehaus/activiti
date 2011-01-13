package org.activiti.engine.test.el;

import javax.ejb.Remote;

@Remote
public interface MyEJBRemoteHome {
  
  public int doSomething(int x, int y);
  
  public int doAnotherThing();
  
}
