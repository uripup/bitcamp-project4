package bitcamp.project4;

import bitcamp.project4.context.ApplicationContext;
//import bitcamp.project4.dao.skel.QuizDaoSkel;
import bitcamp.project4.listener.ApplicationListener;
import bitcamp.project4.listener.InitApplicationListener;
//


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();

//  UserDaoSkel userDaoSkel;
  private final ExecutorService threadPool = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    ServerApp app = new ServerApp();

    // 애플리케이션이 시작되거나 종료될 때 알림 받을 객체의 연락처를 등록한다.
    app.addApplicationListener(new InitApplicationListener());

    app.execute();
  }

  private void addApplicationListener(ApplicationListener listener) {
    listeners.add(listener);
  }

  private void removeApplicationListener(ApplicationListener listener) {
    listeners.remove(listener);
  }

  void execute() {

    // 애플리케이션이 시작될 때 리스너에게 알린다.
    for (ApplicationListener listener : listeners) {
      try {
        listener.onStart(appCtx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }

//     서버에서 사용할 Dao Skeloton 객체를 준비한다.
//    userDaoSkel = (UserDaoSkel) appCtx.getAttribute("userDaoSkel");

    System.out.println("서버 프로젝트 관리 시스템 시작!");

    try (ServerSocket serverSocket = new ServerSocket(8888);) {
      System.out.println("서버 실행 중...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        threadPool.submit(() -> processRequest(clientSocket));
      }

    } catch (Exception e) {
      System.out.println("통신 중 오류 발생!");
      e.printStackTrace();
    } finally {
      threadPool.shutdown();
    }

    System.out.println("종료합니다.");

    // 애플리케이션이 종료될 때 리스너에게 알린다.
    for (ApplicationListener listener : listeners) {
      try {
        listener.onShutdown(appCtx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }
  }


  void processRequest(Socket s) {
    String remoteHost = null;
    int port = 0;

    try {
      InetSocketAddress addr = (InetSocketAddress) s.getRemoteSocketAddress();
      remoteHost = addr.getHostString();
      port = addr.getPort();

      System.out.printf("%s:%d 클라이언트와 연결되었음!\n", remoteHost, port);
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

      boolean continueRunning = true;
      while (continueRunning) {
        String dataName = in.readUTF();
        switch (dataName) {
          case "hangman":
            playHangman(in, out);
            break;
//          case "user":
//            userDaoSkel.service(in, out);
//            break;
          case "quit":
            continueRunning = false;
            break;
          default:
            System.out.println("Unknown request: " + dataName);
        }
      }
      System.out.printf("%s:%d 클라이언트와의 연결을 종료합니다.\n", remoteHost, port);
    } catch (Exception e) {
      System.out.printf("%s:%d 클라이언트 요청 처리 중 오류 발생!\n", remoteHost, port);
      e.printStackTrace();
    } finally {
      try {
        if (s != null && !s.isClosed()) {
          s.close();
        }
      } catch (IOException e) {
        System.out.printf("%s:%d 소켓 종료 중 오류 발생!\n", remoteHost, port);
        e.printStackTrace();
      }
    }
  }

  private void playHangman(ObjectInputStream in, ObjectOutputStream out) throws Exception {
    Hangman hangman = new Hangman();
    hangman.startNewGame();

    out.writeInt(hangman.getCurrentQuiz().length()); // 글자 수 전송
    out.writeInt(hangman.getTurnsLeft()); // 초기 턴 수 전송
//    out.writeObject(hangman.getTopic()); // 주제 전송
    out.writeObject(hangman.getGameState()); // 초기 게임 상태 전송
    out.flush();

    while (!hangman.isGameOver()) {
      char guess = in.readChar();
      boolean correctGuess = hangman.processGuess(guess);

      out.writeBoolean(correctGuess);
      out.writeInt(hangman.getTurnsLeft());
      out.writeObject(hangman.getDisplayWord());
      out.writeBoolean(hangman.isGameOver());
      out.writeObject(hangman.getGameState()); // 현재 게임 상태 전송
      out.flush();
    }

    // 게임 종료 후 정답과 승패 여부 전송
    out.writeObject(hangman.getCurrentQuiz());
    out.writeBoolean(hangman.isWin());
    out.flush();
  }
}
