package bitcamp.project4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RandomWordApi {
  private static final String API_URL = "https://random-word-api.herokuapp.com/word";
  private static final HttpClient client = HttpClient.newHttpClient();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static List<String> getRandomWords(int wordNo) throws Exception {
    String url = API_URL + "?number=" + wordNo + "&length=6";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      String body = response.body();
      return objectMapper.readValue(body, new TypeReference<List<String>>() {
      });
    } else {
      throw new RuntimeException("API request failed with status code: " + response.statusCode());
    }
  }

}
