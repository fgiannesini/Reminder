package com.fgiannesini.original;

import com.fgiannesini.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class OriginalDictionaryTest {

    @Test
    void should_load_from_resource_file() throws IOException {
        var originalDictionary = new OriginalDictionary(getTestOriginalCsvInputStream());
        List<Word> wordList = originalDictionary.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire")
        );

        Assertions.assertEquals(expected, wordList);
    }

    private InputStream getTestOriginalCsvInputStream() {
        return ClassLoader.getSystemResourceAsStream("dictionary-for-test.csv");
    }
}