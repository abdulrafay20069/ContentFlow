package contentflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {

    // Spring automatically gives us the GroqService we created
    @Autowired
    private GroqService groqService;

    // POST /api/content/generate
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generate(@RequestBody Map<String, String> request) {

        String topic = request.get("topic");
        String type = request.get("type");

        // Validate input
        if (topic == null || topic.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Topic cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        if (type == null || type.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Type cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            // Build prompt based on content type
            String prompt = buildPrompt(topic, type);

            // Call Groq API through our service
            String generatedContent = groqService.generateContent(prompt);

            // Send back success response
            Map<String, String> response = new HashMap<>();
            response.put("content", generatedContent);
            response.put("topic", topic);
            response.put("type", type);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate content: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // POST /api/content/summarize
    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarize(@RequestBody Map<String, String> request) {

        String text = request.get("text");

        if (text == null || text.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Text cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            String prompt = "Summarize the following text clearly and concisely:\n\n" + text;
            String summary = groqService.generateContent(prompt);

            Map<String, String> response = new HashMap<>();
            response.put("summary", summary);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to summarize: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Helper method to build prompts
    private String buildPrompt(String topic, String type) {
        switch (type.toLowerCase()) {
            case "blog":
                return "Write a professional blog post about: " + topic +
                       ". Include an introduction, 3 main points, and a conclusion.";
            case "social":
                return "Write an engaging LinkedIn post about: " + topic +
                       ". Keep it under 200 words with relevant hashtags.";
            case "email":
                return "Write a professional email about: " + topic +
                       ". Include subject line, body, and sign-off.";
            case "product":
                return "Write a compelling product description for: " + topic +
                       ". Focus on benefits and features.";
            default:
                return "Write content about: " + topic;
        }
    }
}