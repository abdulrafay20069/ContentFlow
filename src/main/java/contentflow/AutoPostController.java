package contentflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/autopost")
@CrossOrigin(origins = "*")
public class AutoPostController {

    @Autowired
    private ScheduledPostRepository scheduledPostRepository;

    // GET /api/autopost/queue
    @GetMapping("/queue")
    public ResponseEntity<List<ScheduledPost>> getQueue() {
        return ResponseEntity.ok(scheduledPostRepository.findAllByOrderByScheduledForAsc());
    }

    // POST /api/autopost/schedule
    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> schedule(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String type = request.get("type");
        String scheduleIn = request.get("scheduleIn"); // minutes from now

        if (topic == null || type == null || scheduleIn == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "topic, type, and scheduleIn are required");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            int minutes = Integer.parseInt(scheduleIn);
            if (minutes < 1) minutes = 1;

            LocalDateTime scheduledFor = LocalDateTime.now().plusMinutes(minutes);
            ScheduledPost post = new ScheduledPost(topic, type, scheduledFor);
            scheduledPostRepository.save(post);

            Map<String, Object> response = new HashMap<>();
            response.put("id", post.getId());
            response.put("topic", topic);
            response.put("type", type);
            response.put("scheduledFor", scheduledFor.toString());
            response.put("status", "PENDING");
            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "scheduleIn must be a valid number");
            return ResponseEntity.badRequest().body(err);
        }
    }

    // DELETE /api/autopost/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        if (!scheduledPostRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        scheduledPostRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Deleted");
        return ResponseEntity.ok(response);
    }
}