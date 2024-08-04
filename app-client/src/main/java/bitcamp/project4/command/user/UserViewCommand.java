package bitcamp.project4.command.user;

import bitcamp.project4.myapp.dao.UserDao;
import bitcamp.project4.myapp.vo.User;
import bitcamp.project4.command.Command;
import bitcamp.project4.util.Prompt;

public class UserViewCommand implements Command {

  private UserDao userDao;

  public UserViewCommand(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void execute(String menuName) {
    System.out.printf("[%s]\n", menuName);
    int userNo = Prompt.inputInt("회원번호?");

    try {
      User user = userDao.findBy(userNo);
      if (user == null) {
        System.out.println("없는 회원입니다.");
        return;
      }

      System.out.printf("이름: %s\n", user.getName());
      System.out.printf("아이디: %s\n", user.getID());
      System.out.printf("포인트: %d\n", user.getPoint());

    } catch (Exception e) {
      System.out.println("조회 중 오류 발생!");
    }
  }
}
