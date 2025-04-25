package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

class WordTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dire",
            "c'est à DIRE",
            "cestàdire",
            "c est à dire",
            "c est0à_dire",
            " c  est  à dire ",
    })
    void should_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(Matching.MATCHED, word.getMatching(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dir",
            "",
            " ",
    })
    void should_not_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(Matching.NOT_MATCHED, word.getMatching(input));
    }

    @Test
    void should_be_closed_to_matching_french_if_input_has_accents() {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(Matching.CLOSED, word.getMatching("c est a dire"));
    }

    @Test
    void should_handle_two_translations() {
        Word word = new Word("conferir", "confirmer, vérifier");
        Assertions.assertEquals(Matching.MATCHED, word.getMatching("confirmer"));
        Assertions.assertEquals(Matching.MATCHED, word.getMatching("vérifier"));
        Assertions.assertEquals(Matching.MATCHED, word.getMatching("confirmer, vérifier"));
        Assertions.assertEquals(Matching.MATCHED, word.getMatching("vérifier, confirmer"));
    }

    @Test
    void should_be_learned_when_checked_lots_of_time() {
        Word word = new Word("ou seja", "c'est à dire", 3, null, 0);
        Assertions.assertTrue(word.shouldBeMarkedAsLearnt());
    }

    @Test
    void should_not_be_learned_when_not_checked() {
        Word word = new Word("ou seja", "c'est à dire", 0, null, 0);
        Assertions.assertFalse(word.shouldBeMarkedAsLearnt());
    }

    @Test
    void should_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null, 0);
        Word word2 = new Word("ou seja", "c'est à dire", 1, null, 0);
        Assertions.assertTrue(word1.isSimilarTo(word2));
    }

    @Test
    void should_not_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null, 0);
        Word word2 = new Word("conferir", "confirmer, vérifier", 0, null, 0);
        Assertions.assertFalse(word1.isSimilarTo(word2));
    }

    @Test
    void should_reset() {
        Word word = new Word("ou seja", "c'est à dire", 5, LocalDateTime.now(), 1);
        Word actual = word.reset();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 0, null, 0), actual);
    }

    @Test
    void should_check_a_not_learned_word() {
        Word word = new Word("ou seja", "c'est à dire", 1, null, 0);
        Word actual = word.checked();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 2, null, 0), actual);
    }

    @Test
    void should_check_a_word_to_learn() {
        Word word = new Word("ou seja", "c'est à dire", 2, null, 0);
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, learnedMoment, 1), actual);
    }

    @Test
    void should_check_a_learnt_word() {
        Word word = new Word("ou seja", "c'est à dire", 3, LocalDateTime.MIN, 2);
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, learnedMoment, 2), actual);
    }

    @Test
    void should_check_a_word_learnt() {
        var oldLearntMoment = LocalDateTime.now();
        Word word = new Word("ou seja", "c'est à dire", 3, oldLearntMoment, 1);
        var newLearntMoment = LocalDateTime.now();
        Word actual = word.checked(newLearntMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, newLearntMoment, 2), actual);
    }

}