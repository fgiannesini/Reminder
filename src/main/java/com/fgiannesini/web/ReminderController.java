package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.Matching;
import com.fgiannesini.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/reminder/word")
public class ReminderController {

    private final Dictionary dictionary;

    @Autowired
    public ReminderController(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @GetMapping("/next")
    public WordDto next() {
        var word = dictionary.next(20);
        return new WordDto(word.wordToLearn());
    }

    @PostMapping("/check")
    TranslationResponseDto check(@RequestBody TranslationDto translation) throws IOException {
        Word wordToLearn = dictionary.find(translation.wordToLearn());
        Matching matching = wordToLearn.getMatching(translation.proposedTranslation());

        Word newWord = switch (matching) {
            case MATCHED, CLOSED -> wordToLearn.checked();
            case NOT_MATCHED -> wordToLearn.reset();
        };
        dictionary.update(newWord);
        return new TranslationResponseDto(matching, newWord.translation(), newWord.shouldBeMarkedAsLearnt());
    }
}
