package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.Matching;
import com.fgiannesini.storage.FileStorageHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reminder/word")
public class ReminderController {

    private final Dictionary dictionary;

    public ReminderController() throws IOException {
        var originalFileInputStream = ClassLoader.getSystemResourceAsStream("dictionary.csv");
        var storageDir = Path.of(System.getProperty("user.home")).resolve("Reminder");
        var storageHandler = new FileStorageHandler(storageDir, originalFileInputStream);
        dictionary = new Dictionary(
                new SecureRandom(LocalDateTime.now().toString().getBytes()),
                storageHandler
        );
    }

    @GetMapping("/next")
    public WordDto next() {
        var word = dictionary.next(40);
        return new WordDto(word.wordToLearn());
    }

    @PostMapping("/check")
    TranslationResponseDto check(@RequestBody TranslationDto translation) throws IOException {
//        Matching matching = wordToLearn.getMatching(translation.value());
//
//        Word newWord = switch (matching) {
//            case MATCHED, CLOSED -> wordToLearn.checked();
//            case NOT_MATCHED -> wordToLearn.reset();
//        };
//        dictionary.update(newWord);
//        return new TranslationResponseDto(matching, newWord.translation());
        return new TranslationResponseDto(Matching.MATCHED, translation.word());
    }
}
