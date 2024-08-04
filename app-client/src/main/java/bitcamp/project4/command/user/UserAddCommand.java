package bitcamp.project4.command.user;

import bitcamp.project4.myapp.dao.UserDao;
import bitcamp.project4.myapp.vo.User;
import bitcamp.project4.command.Command;
import bitcamp.project4.util.Prompt;

public class UserAddCommand implements Command {

  private UserDao userDao;

  public UserAddCommand(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void execute(String menuName) {
    System.out.printf("[%s]\n", menuName);
    try {
      User user = new User();
      user.setName(Prompt.input("이름?"));
      user.setID(Prompt.input("이메일?"));
      user.setPassword(Prompt.input("암호?"));
      user.setPoint(0);

      userDao.insert(user);
    } catch (Exception e) {
      System.out.println("등록 중 오류 발생!");
    }
  }
}
