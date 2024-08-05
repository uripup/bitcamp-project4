package bitcamp.project4;

import java.util.*;

public class Hangman {
  private static int MAX_TRIES = 6;
  //    private ListQuizDao quizDao;
  private String currentQuiz;
  private Set<Character> guessedLetters;
  private int turnsLeft;
  private String topic;
  private String hint;
  private int wrongGuesses;

  GetApi aiHint = new GetApi();

  RandomWordApi randomWord = new RandomWordApi();


  public Hangman() {
    guessedLetters = new HashSet<>();
  }

  public void startNewGame() {
    try {
      currentQuiz=randomWord.getRandomWords(1).get(0);
      System.out.println(currentQuiz);
      aiHint.sendRequest(1, currentQuiz);
      if (currentQuiz.isEmpty()) {
        throw new IllegalStateException("퀴즈를 받아오지 못했습니다.");
      }
    } catch (Exception e) {
      System.out.println("퀴즈를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
      System.exit(1);
    }

    guessedLetters.clear();
    turnsLeft = MAX_TRIES - wrongGuesses;
    //        topic = currentQuiz.getTopic();
    hint = aiHint.getAssistantReply();
    wrongGuesses = 0;
  }

  public boolean processGuess(char guess) {
    if (!Character.isLetter(guess)) {
      return false;
    }

    guess = Character.toLowerCase(guess);
    guessedLetters.add(guess);

    if (currentQuiz.toLowerCase().indexOf(guess) == -1) {
      wrongGuesses++;
      turnsLeft--;
      return false;
    } else {
      return true;
    }
  }

  public String getDisplayWord() {
    StringBuilder display = new StringBuilder();
    for (char c : currentQuiz.toCharArray()) {
      if (guessedLetters.contains(Character.toLowerCase(c))) {
        display.append(c);
      } else {
        display.append("_");
      }
      display.append(" ");
    }
    return display.toString().trim();
  }

  public boolean isNewGuess(char guess) {
    return !guessedLetters.contains(Character.toLowerCase(guess));
  }


  public boolean isGameOver() {
    return turnsLeft == 0 || getDisplayWord().replace(" ", "").equalsIgnoreCase(currentQuiz);
  }

  public boolean isWin() {
    return getDisplayWord().replace(" ", "").equalsIgnoreCase(currentQuiz);
  }

  public String getCurrentQuiz() {
    return currentQuiz;
  }

  public int getTurnsLeft() {
    return turnsLeft;
  }

  public String getTopic() {
    return topic;
  }

  public String getHint() {
    return hint;
  }

  public boolean shouldShowHint() {
    return wrongGuesses >= 3;
  }

  public String getHangmanImage() {
    String[] hangmanStages = {"  +---+\n  |   |\n      |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n      |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n  |   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|   |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n      |\n=========",
        "  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n      |\n========="};

    return hangmanStages[Math.min(wrongGuesses, MAX_TRIES)];
  }

  public String getGameState() {
    StringBuilder state = new StringBuilder();
    state.append(getHangmanImage()).append("\n\n");
    state.append("Word: ").append(getDisplayWord()).append("\n");
    state.append("Turns left: ").append(turnsLeft).append("\n");
    //        state.append("Topic: ").append(topic).append("\n");
    state.append("Guessed letters: ")
        .append(String.join(", ", guessedLetters.stream().map(String::valueOf).sorted().toList()))
        .append("\n");
            if (shouldShowHint()) {
                state.append("Hint: ").append(hint).append("\n");
            }
    return state.toString();
  }
}
