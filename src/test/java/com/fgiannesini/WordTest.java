package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

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
        Assertions.assertEquals(word.getMatching(input), Matching.MATCHED);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dir",
            "",
            " ",
    })
    void should_not_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(word.getMatching(input), Matching.NOT_MATCHED);
    }

    @Test
    void should_be_closed_to_matching_french_if_input_has_accents() {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(word.getMatching("c est a dire"), Matching.CLOSED);
    }

    @Test
    void should_handle_two_translations() {
        Word word = new Word("conferir", "confirmer, vérifier");
        Assertions.assertEquals(word.getMatching("confirmer"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("vérifier"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("confirmer, vérifier"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("vérifier, confirmer"), Matching.MATCHED);
    }

    @Test
    void should_be_learned_when_checked_lots_of_time() {
        Word word = new Word("ou seja", "c'est à dire", 5, null);
        Assertions.assertTrue(word.isLearned());
    }

    @Test
    void should_not_be_learned_when_not_checked() {
        Word word = new Word("ou seja", "c'est à dire", 0, null);
        Assertions.assertFalse(word.isLearned());
    }

    @Test
    void should_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null);
        Word word2 = new Word("ou seja", "c'est à dire", 1, null);
        Assertions.assertTrue(word1.isSimilarTo(word2));
    }

    @Test
    void should_not_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null);
        Word word2 = new Word("conferir", "confirmer, vérifier", 0, null);
        Assertions.assertFalse(word1.isSimilarTo(word2));
    }

    @Test
    void should_reset() {
        Word word = new Word("ou seja", "c'est à dire", 5, new Date());
        Word actual = word.reset();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 0, null), actual);
    }

    @Test
    void should_check_a_not_learned_word() {
        Word word = new Word("ou seja", "c'est à dire", 1, null);
        Word actual = word.checked();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 2, null), actual);
    }

    @Test
    void should_check_a_word_to_learn() {
        Word word = new Word("ou seja", "c'est à dire", 4, null);
        Date learnedMoment = new Date();
        Word actual = word.checked(learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 5, learnedMoment), actual);
    }

    @Test
    void should_check_a_word_learnt() {
        Date oldLearntMoment = new Date();
        Word word = new Word("ou seja", "c'est à dire", 5, oldLearntMoment);
        Date newLearntMoment = new Date();
        Word actual = word.checked(newLearntMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 5, newLearntMoment), actual);
    }

}