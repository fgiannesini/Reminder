package com.fgiannesini;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/greeting")
    public String getGreeting() {
        return "Hello, World!";
    }

    // Endpoint POST
    @PostMapping("/submit")
    String submitData(@RequestBody String data) {
        return "Data received: ${data.name}";
    }
}
