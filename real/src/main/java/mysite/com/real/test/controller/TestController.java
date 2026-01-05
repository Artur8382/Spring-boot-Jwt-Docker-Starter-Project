package mysite.com.real.test.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String testRateLimit() {
        return "Request Successful!";
    }
}
