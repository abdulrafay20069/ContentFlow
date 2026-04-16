package contentflow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello! Your Spring Boot server is working!";
    }

    @GetMapping("/status")
    public String status() {
        return "ContentFlow API is running. Week 2 has begun!";
    }
}