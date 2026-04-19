package contentflow;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_posts")
public class ScheduledPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String generatedContent;

    @Column(nullable = false)
    private String status; // PENDING → GENERATED or FAILED

    @Column(nullable = false)
    private LocalDateTime scheduledFor;

    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
    }

    public ScheduledPost() {}

    public ScheduledPost(String topic, String type, LocalDateTime scheduledFor) {
        this.topic = topic;
        this.type = type;
        this.scheduledFor = scheduledFor;
    }

    // Getters
    public Long getId()                   { return id; }
    public String getTopic()              { return topic; }
    public String getType()               { return type; }
    public String getGeneratedContent()   { return generatedContent; }
    public String getStatus()             { return status; }
    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    // Setters (only what we need to mutate)
    public void setGeneratedContent(String c) { this.generatedContent = c; }
    public void setStatus(String s)           { this.status = s; }
    public void setGeneratedAt(LocalDateTime t) { this.generatedAt = t; }
}