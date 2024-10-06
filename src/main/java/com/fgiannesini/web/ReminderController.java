package com.fgiannesini.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reminder/word")
public class ReminderController {

    @GetMapping("/next")
    public WordDto next() {
        return new WordDto("Hello, World!");
    }

    @PostMapping("/check")
    String check(@RequestParam String data) {
        return "Data received: ${data.name}";
    }
}
