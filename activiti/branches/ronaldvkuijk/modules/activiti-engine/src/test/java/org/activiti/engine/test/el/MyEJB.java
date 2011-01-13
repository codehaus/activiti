package org.activiti.engine.test.el;

import javax.ejb.LocalHome;
import javax.ejb.RemoteHome;
import javax.ejb.Stateless;

@Stateless
@LocalHome(MyEJBLocalHome.class)
@RemoteHome(MyEJBRemoteHome.class)
public class MyEJB {

   public int doSomething(int x, int y) {
     return  x*y;
   }

   public int doAnotherThing() {
     return 12;
   }
   
  
}
