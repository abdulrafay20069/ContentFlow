package contentflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutoPostService {

    @Autowired
    private ScheduledPostRepository scheduledPostRepository;

    @Autowired
    private GroqService groqService;

    // Spring calls this every 60 seconds automatically
    // Think of it like a cron job built into the JVM
    @Scheduled(fixedRate = 60000)
    public void processPendingPosts() {
        // Find all posts that are PENDING and whose scheduled time has passed
        List<ScheduledPost> due = scheduledPostRepository
            .findByStatusAndScheduledForBefore("PENDING", LocalDateTime.now());

        for (ScheduledPost post : due) {
            try {
                String prompt = buildPrompt(post.getTopic(), post.getType());
                String content = groqService.generateContent(prompt);

                post.setGeneratedContent(content);
                post.setStatus("GENERATED");
                post.setGeneratedAt(LocalDateTime.now());

                System.out.println("[AutoPost] ✓ Generated: " + post.getTopic());

            } catch (Exception e) {
                post.setStatus("FAILED");
                System.err.println("[AutoPost] ✗ Failed: " + post.getTopic() + " — " + e.getMessage());
            }

            scheduledPostRepository.save(post);
        }
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
}