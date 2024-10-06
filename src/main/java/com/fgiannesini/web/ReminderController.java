package com.fgiannesini.web;

import com.fgiannesini.Matching;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reminder/word")
public class ReminderController {

    @GetMapping("/next")
    public WordDto next() {
        return new WordDto("Hello, World!");
    }

    @PostMapping("/check/{translation}")
    TranslationCheckDto check(@PathVariable String translation) {
        return new TranslationCheckDto(Matching.MATCHED, translation);
    }
}
