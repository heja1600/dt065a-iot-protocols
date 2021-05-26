package src.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpUtil {
    public static String plainTextHttpGetRequest(String uri) throws IOException, InterruptedException  {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(uri))
              .build();
    
        HttpResponse<String> response =
              client.send(request, BodyHandlers.ofString());
        return response.body();
    }
}
