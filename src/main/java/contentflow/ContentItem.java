package contentflow;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// @Entity tells Spring: "This class maps to a database table"
@Entity
@Table(name = "content_items")
public class ContentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false) // TEXT = long strings
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist // Runs automatically before every save
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ContentItem() {}

    public ContentItem(String topic, String type, String content) {
        this.topic = topic;
        this.type = type;
        this.content = content;
    }

    // Getters
    public Long getId()               { return id; }
    public String getTopic()          { return topic; }
    public String getType()           { return type; }
    public String getContent()        { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}