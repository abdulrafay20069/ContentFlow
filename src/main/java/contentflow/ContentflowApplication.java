package contentflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ← This powers AutoPost
public class ContentflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentflowApplication.class, args);
    }
}