package contentflow;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GroqService {

    // This automatically reads your API key from application.properties
    @Value("${GROQ_API_KEY}")
private String apiKey;
    

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String generateContent(String prompt) throws IOException {

    // Build JSON manually - more reliable than anonymous classes
    String requestBody = "{"
        + "\"model\": \"" + MODEL + "\","
        + "\"messages\": [{\"role\": \"user\", \"content\": " + gson.toJson(prompt) + "}],"
        + "\"max_tokens\": 1024"
        + "}";

    RequestBody body = RequestBody.create(
        requestBody,
        MediaType.get("application/json")
    );

    Request request = new Request.Builder()
        .url(API_URL)
        .addHeader("Authorization", "Bearer " + apiKey)
        .addHeader("Content-Type", "application/json")
        .post(body)
        .build();

    for (int attempt = 1; attempt <= 3; attempt++) {
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                if (attempt < 3) {
                    Thread.sleep(1000);
                    continue;
                }
                throw new IOException("Groq API error: " + response.code());
            }

            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            return json.getAsJsonArray("choices")
                       .get(0).getAsJsonObject()
                       .getAsJsonObject("message")
                       .get("content").getAsString();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted");
        }
    }

    throw new IOException("Failed after 3 attempts");
}
}