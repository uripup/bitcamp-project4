package bitcamp.project4.command.user;

import bitcamp.project4.myapp.dao.UserDao;
import bitcamp.project4.myapp.vo.User;
import bitcamp.project4.command.Command;
import bitcamp.project4.util.Prompt;

public class UserDeleteCommand implements Command {

  private UserDao userDao;

  public UserDeleteCommand(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void execute(String menuName) {
    System.out.printf("[%s]\n", menuName);
    int userNo = Prompt.inputInt("회원번호?");

    try {
      User deletedUser = userDao.findBy(userNo);
      if (deletedUser == null) {
        System.out.println("없는 회원입니다.");
        return;
      }

      userDao.delete(userNo);
      System.out.printf("'%s' 회원을 삭제 했습니다.\n", deletedUser.getName());

    } catch (Exception e) {
      System.out.println("삭제 중 오류 발생!");
    }
  }
}
