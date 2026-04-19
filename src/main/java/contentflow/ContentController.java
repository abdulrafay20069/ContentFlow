package contentflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class ContentController {

    @Autowired
    private GroqService groqService;

    @Autowired
    private ContentRepository contentRepository;

    // POST /api/content/generate
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generate(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String type  = request.get("type");

        if (topic == null || topic.trim().isEmpty()) return badRequest("Topic cannot be empty");
        if (type  == null || type.trim().isEmpty())  return badRequest("Type cannot be empty");

        try {
            String content = groqService.generateContent(buildPrompt(topic, type));
            contentRepository.save(new ContentItem(topic, type, content));

            Map<String, String> response = new HashMap<>();
            response.put("content", content);
            response.put("topic", topic);
            response.put("type", type);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return serverError("Failed to generate: " + e.getMessage());
        }
    }

    // POST /api/content/summarize
    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) return badRequest("Text cannot be empty");

        try {
            String prompt = "Summarize the following text clearly and concisely:\n\n" + text;
            String summary = groqService.generateContent(prompt);
            contentRepository.save(new ContentItem("Text summarization", "summary", summary));

            Map<String, String> response = new HashMap<>();
            response.put("summary", summary);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return serverError("Failed to summarize: " + e.getMessage());
        }
    }

    // POST /api/content/refine
    // Takes existing content + an action, runs a second AI call to transform it
    @PostMapping("/refine")
    public ResponseEntity<Map<String, String>> refine(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String action  = request.get("action"); // improve | shorten | expand | formal | casual

        if (content == null || content.trim().isEmpty()) return badRequest("Content cannot be empty");
        if (action  == null || action.trim().isEmpty())  return badRequest("Action cannot be empty");

        try {
            String refined = groqService.generateContent(buildRefinePrompt(content, action));
            contentRepository.save(new ContentItem("Refined: " + action, "refined", refined));

            Map<String, String> response = new HashMap<>();
            response.put("content", refined);
            response.put("action", action);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return serverError("Failed to refine: " + e.getMessage());
        }
    }

    // POST /api/content/bulk
    // Generates all 4 content types for a single topic
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, String>> bulk(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        if (topic == null || topic.trim().isEmpty()) return badRequest("Topic cannot be empty");

        Map<String, String> results = new LinkedHashMap<>();
        String[] types = {"blog", "social", "email", "product"};

        for (String type : types) {
            try {
                String content = groqService.generateContent(buildPrompt(topic, type));
                results.put(type, content);
                contentRepository.save(new ContentItem(topic, type, content));
            } catch (Exception e) {
                results.put(type + "_error", "Failed: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(results);
    }

    // GET /api/content/history
    @GetMapping("/history")
    public ResponseEntity<List<ContentItem>> history() {
        return ResponseEntity.ok(contentRepository.findAllByOrderByCreatedAtDesc());
    }

    // DELETE /api/content/history/{id}
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, String>> deleteHistory(@PathVariable Long id) {
        if (!contentRepository.existsById(id)) return ResponseEntity.notFound().build();
        contentRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Deleted");
        return ResponseEntity.ok(response);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, String>> badRequest(String msg) {
        Map<String, String> e = new HashMap<>(); e.put("error", msg);
        return ResponseEntity.badRequest().body(e);
    }

    private ResponseEntity<Map<String, String>> serverError(String msg) {
        Map<String, String> e = new HashMap<>(); e.put("error", msg);
        return ResponseEntity.internalServerError().body(e);
    }

    private String buildPrompt(String topic, String type) {
        switch (type.toLowerCase()) {
            case "blog":   return "Write a professional blog post about: " + topic + ". Include an introduction, 3 main points, and a conclusion.";
            case "social": return "Write an engaging LinkedIn post about: " + topic + ". Keep it under 200 words with relevant hashtags.";
            case "email":  return "Write a professional email about: " + topic + ". Include subject line, body, and sign-off.";
            case "product":return "Write a compelling product description for: " + topic + ". Focus on benefits and features.";
            default:       return "Write content about: " + topic;
        }
    }

    private String buildRefinePrompt(String content, String action) {
        switch (action.toLowerCase()) {
            case "improve": return "Improve this content to be more engaging, clear, and professional. Keep the same format:\n\n" + content;
            case "shorten": return "Shorten this content to half its length while keeping all key points:\n\n" + content;
            case "expand":  return "Expand this content with more detail, examples, and depth:\n\n" + content;
            case "formal":  return "Rewrite this in a formal, professional tone:\n\n" + content;
            case "casual":  return "Rewrite this in a casual, conversational tone:\n\n" + content;
            default:        return "Improve this content:\n\n" + content;
        }
    }
}