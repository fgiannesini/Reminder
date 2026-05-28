package com.fgiannesini.web.storage;

import com.fgiannesini.SmRepetition;
import com.fgiannesini.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WordDaoTest {

    @Test
    void should_convert_to_Word() {
        WordDao wordDao = new WordDao("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7), 1, 2.5f, 3);
        Word expected = new Word("acender", "allumer", 1, new SmRepetition(LocalDateTime.of(2024, 11, 2, 16, 20, 7), 1, 2.5f, 3));
        Assertions.assertEquals(wordDao.toWord(), expected);
    }

    @Test
    void should_build_from_Word() {
        Word word = new Word("acender", "allumer", 1, new SmRepetition(LocalDateTime.of(2024, 11, 2, 16, 20, 7), 1, 2.5f, 3));
        WordDao expected = new WordDao("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7), 1, 2.5f, 3);
        Assertions.assertEquals(WordDao.fromWord(word), expected);
    }

    @Test
    void should_apply_default_ease_factor_when_zero() {
        WordDao wordDao = new WordDao("acender", "allumer", 0, null, 0, 0f, 0);
        Word actual = wordDao.toWord();
        Assertions.assertEquals(2.5f, actual.smRepetition().easeFactor());
        Assertions.assertEquals(1, actual.smRepetition().intervalDays());
    }
}
