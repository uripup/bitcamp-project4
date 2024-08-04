package bitcamp.project4.listener;

import bitcamp.project4.command.user.*;
import bitcamp.project4.context.ApplicationContext;
import bitcamp.project4.menu.MenuGroup;
import bitcamp.project4.menu.MenuItem;
import bitcamp.project4.myapp.dao.UserDao;
import bitcamp.project4.myapp.dao.stub.UserDaoStub;

public class InitApplicationListener implements ApplicationListener {

  UserDao userDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {

    String host = (String) ctx.getAttribute("host");
    int port = (int) ctx.getAttribute("port");

    MenuGroup mainMenu = ctx.getMainMenu();

    userDao = new UserDaoStub(host, port, "user");
//    quizDao = new QuizDaoStub(host, port, "quiz");
//
    MenuGroup userMenu = new MenuGroup("회원");
    userMenu.add(new MenuItem("등록", new UserAddCommand(userDao)));
    userMenu.add(new MenuItem("목록", new UserListCommand(userDao)));
    userMenu.add(new MenuItem("조회", new UserViewCommand(userDao)));
    userMenu.add(new MenuItem("변경", new UserUpdateCommand(userDao)));
    userMenu.add(new MenuItem("삭제", new UserDeleteCommand(userDao)));
    mainMenu.add(userMenu);

    mainMenu.setExitMenuTitle("종료");
  }
}
