package bitcamp.project4.command.user;

import bitcamp.project4.myapp.dao.UserDao;
import bitcamp.project4.myapp.vo.User;
import bitcamp.project4.command.Command;
import bitcamp.project4.util.Prompt;

public class UserUpdateCommand implements Command {

  private UserDao userDao;

  public UserUpdateCommand(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void execute(String menuName) {
    System.out.printf("[%s]\n", menuName);
    int userNo = Prompt.inputInt("아이디를 입력해주세요.");

    try {
      User user = userDao.findBy(userNo);
      if (user == null) {
        System.out.println("없는 회원입니다.");
        return;
      }

      user.setName(Prompt.input("이름(%s)?", user.getName()));
      user.setPassword(Prompt.input("암호?"));

      userDao.update(user);
      System.out.println("변경 했습니다.");

    } catch (Exception e) {
      System.out.println("변경 중 오류 발생!");
    }
  }

}
