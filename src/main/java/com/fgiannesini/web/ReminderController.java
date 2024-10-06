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

    @PostMapping("/check")
    TranslationResponseDto check(@RequestBody TranslationDto translation) {
        return new TranslationResponseDto(Matching.MATCHED, translation.value());
    }
}
