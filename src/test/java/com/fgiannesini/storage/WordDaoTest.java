package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.fgiannesini.web.storage.WordDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WordDaoTest {

    @Test
    void should_convert_to_Word() {
        WordDao wordDao = new WordDao("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7));
        Word expected = new Word("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7));
        Assertions.assertEquals(wordDao.toWord(), expected);
    }

    @Test
    void should_build_from_Word() {
        Word word = new Word("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7));
        WordDao expected = new WordDao("acender", "allumer", 1, LocalDateTime.of(2024, 11, 2, 16, 20, 7));
        Assertions.assertEquals(WordDao.fromWord(word), expected);
    }
}
