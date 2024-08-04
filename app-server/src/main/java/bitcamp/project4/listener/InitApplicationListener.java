package bitcamp.project4.listener;

import bitcamp.project4.context.ApplicationContext;
import bitcamp.project4.dao.ListUserDao;
import bitcamp.project4.dao.skel.UserDaoSkel;
import bitcamp.project4.myapp.dao.UserDao;


public class InitApplicationListener implements ApplicationListener {

// UserDao userDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {
//    userDao = new ListUserDao("app-server/data.xlsx");
//    UserDaoSkel userDaoSkel = new UserDaoSkel(userDao);
//
//    ctx.setAttribute("userDaoSkel", userDaoSkel);
  }

  @Override
  public void onShutdown(ApplicationContext ctx) throws Exception {
//    try {
//      ((ListUserDao) userDao).save();
//    } catch (Exception e) {
//      System.out.println("퀴즈 데이터 저장 중 오류 발생!");
//      e.printStackTrace();
//      System.out.println();
//    }
  }
}
