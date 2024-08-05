package bitcamp.project4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class GetApi {
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private String assistantReply;
  private String apiKey;


  public GetApi() {
    loadApiKey();
  }

  private void loadApiKey() {
    try {
      // 현재 작업 디렉토리를 기준으로 상위 디렉토리의 config.json 파일 경로 설정
      File configFile = new File("app-server/config.json");

      if (!configFile.exists()) {
        System.err.println("config.json 파일을 찾을 수 없습니다.");
        System.err.println("현재 디렉토리: " + System.getProperty("user.dir"));
        System.err.println("찾으려는 파일 경로: " + configFile.getAbsolutePath());
        // 여기서 추가적인 오류 처리 로직을 구현할 수 있습니다.
        return;
      }

      ObjectMapper mapper = new ObjectMapper();
      JsonNode config = mapper.readTree(configFile);
      apiKey = config.get("api_key").asText();

      if (apiKey.isEmpty()) {
        System.err.println("API 키가 비어 있습니다. config.json을 확인해주세요.");
      } else {
        System.out.println("API 키를 성공적으로 로드했습니다.");
      }
    } catch (IOException e) {
      System.err.println("config.json 파일을 읽는 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void sendRequest(int situation, String word) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    ObjectMapper objectMapper = new ObjectMapper();
    String prompt;

    switch (situation) {
      case 1:
        prompt =
            "Imagine that you are an English teacher and are currently playing a game of Hangman with your students. You need to give a hint for the word *"+ word + "*. Provide a simple and concise explanation in one sentence. However, you must not directly mention any of the letters in the given word. Your next reply must be in Korean. Example: 이 단어는 빨갛고 단 맛이 나는 과일을 가리킵니다.";
        break;
      case 2:
        prompt =
            "Say cheering to me as a speaking lovely Eevee (Pokemon). Be super creative and proactive. FINALLY, korean plz! ";
        break;
      case 3:
        prompt =
            "Praise me as an adorable talking Eevee (Pokémon). Be super creative and proactive. FINALLY, korean plz! ";
        break;
      default:
        throw new IllegalArgumentException("Invalid situation: " + situation);
    }

    String requestBody = String.format(
        "{\"model\": \"gpt-4o\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 100, \"temperature\": 0.8 }",
        prompt);

    try {
      HttpPost httpPost = new HttpPost(API_URL);
      httpPost.setHeader("Authorization", "Bearer " + apiKey);
      httpPost.setHeader("Content-Type", "application/json");
      StringEntity entity = new StringEntity(requestBody);
      httpPost.setEntity(entity);
      System.out.println("코멘트 로딩 중. . . ");

      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
          HttpEntity responseEntity = response.getEntity();
          String responseBody = EntityUtils.toString(responseEntity);
          JsonNode responseJson = objectMapper.readTree(responseBody);
          if (responseJson.has("choices") && responseJson.get("choices")
              .isArray() && responseJson.get("choices").size() > 0 && responseJson.get("choices")
              .get(0).has("message") && responseJson.get("choices").get(0).get("message")
              .has("content")) {
            assistantReply =
                responseJson.get("choices").get(0).get("message").get("content").asText().trim();
          } else {
            System.err.println("Unexpected response format: " + responseBody);
          }
        } else {
          throw new IOException("Unexpected status code: " + statusCode);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getAssistantReply() {
    System.out.println("힌트는"+ assistantReply);
    return assistantReply;
  }

  public void printAssistantReply() {
    System.out.println(assistantReply);
  }

  //  public static void main(String[] args) {
  //    GptApi gpt = new GptApi();
  //    gpt.sendRequest();
  //    gpt.printAssistantReply();
  //  }
}


