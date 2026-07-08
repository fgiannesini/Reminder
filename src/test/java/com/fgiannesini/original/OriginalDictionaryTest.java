package com.fgiannesini.original;

import com.fgiannesini.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

class OriginalDictionaryTest {

    @Test
    void should_load_from_resource_file() throws IOException {
        var dict = new OriginalDictionary(ClassLoader.getSystemResourceAsStream("dictionary-for-test.csv"));
        List<Word> wordList = dict.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire")
        );

        Assertions.assertEquals(expected, wordList);
    }

    @Test
    void should_throw_an_exception_if_format_is_incorrect() {
        InputStream is = new ByteArrayInputStream("porém, pourtant".getBytes(StandardCharsets.UTF_8));
        var dict = new OriginalDictionary(is);
        var exception = Assertions.assertThrows(IllegalWordFormatException.class, dict::load);
        Assertions.assertEquals("word: porém, pourtant | translation:null", exception.getMessage());
    }

    @Nested
    class FindDuplicates {

        private final OriginalDictionary originalDictionary = new OriginalDictionary(null);

        @Test
        void should_find_no_duplicates_when_words_are_unique() {
            var words = List.of(
                    new Word("barulho", "bruit"),
                    new Word("tempo", "temps")
            );
            Assertions.assertTrue(originalDictionary.findDuplicates(words).isEmpty());
        }

        @Test
        void should_detect_same_wordToLearn() {
            var wordA = new Word("superar", "vaincre");
            var wordB = new Word("superar", "surmonter");
            var duplicates = originalDictionary.findDuplicates(List.of(wordA, wordB));
            Assertions.assertEquals(List.of(wordA, wordB), duplicates.get("superar"));
        }

        @Test
        void should_detect_same_translation() {
            var wordA = new Word("barulho", "bruit");
            var wordB = new Word("ruído", "bruit");
            var duplicates = originalDictionary.findDuplicates(List.of(wordA, wordB));
            Assertions.assertEquals(List.of(wordA, wordB), duplicates.get("bruit"));
        }

        @Test
        void should_detect_cross_direction_conflict() {
            var wordA = new Word("surmonter", "overcome");
            var wordB = new Word("superar", "surmonter");
            var duplicates = originalDictionary.findDuplicates(List.of(wordA, wordB));
            Assertions.assertEquals(List.of(wordA, wordB), duplicates.get("surmonter"));
        }

        @Test
        void should_not_detect_as_duplicate_when_only_translation_component_overlaps() {
            var wordA = new Word("acender", "allumer, (un feu)");
            var wordB = new Word("éclairer", "allumer");
            Assertions.assertTrue(originalDictionary.findDuplicates(List.of(wordA, wordB)).isEmpty());
        }

        @Test
        void should_not_detect_as_duplicate_when_word_equals_translation() {
            var word = new Word("internet", "internet");
            Assertions.assertTrue(originalDictionary.findDuplicates(List.of(word)).isEmpty());
        }
    }
}
