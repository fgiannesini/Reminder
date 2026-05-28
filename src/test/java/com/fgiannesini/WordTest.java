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
    void should_start_with_zero_checked_count() {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(0, word.checkedCount());
    }

    @Test
    void should_require_three_checks_to_be_learned() {
        Word word = new Word("ou seja", "c'est à dire");
        word = word.checked();
        Assertions.assertFalse(word.shouldBeMarkedAsLearnt());
        word = word.checked();
        Assertions.assertFalse(word.shouldBeMarkedAsLearnt());
        var learnedMoment = LocalDateTime.now();
        word = word.checked(learnedMoment);
        Assertions.assertTrue(word.shouldBeMarkedAsLearnt());
        Assertions.assertNotNull(word.nextReview());
    }

    @Test
    void should_not_be_learned_when_not_checked() {
        Word word = new Word("ou seja", "c'est à dire", 0, null, 0, 2.5f, 1);
        Assertions.assertFalse(word.shouldBeMarkedAsLearnt());
    }

    @Test
    void should_be_in_learning_phase_when_not_checked() {
        Word word = new Word("ou seja", "c'est à dire", 0, null, 0, 2.5f, 1);
        Assertions.assertTrue(word.isLearningPhase());
    }

    @Test
    void should_not_be_in_learning_phase_when_checked_enough() {
        Word word = new Word("ou seja", "c'est à dire", 3, null, 0, 2.5f, 1);
        Assertions.assertFalse(word.isLearningPhase());
    }

    @Test
    void should_not_be_mastered_when_sm_repetitions_low() {
        Word word = new Word("ou seja", "c'est à dire", 3, null, 7, 2.5f, 1);
        Assertions.assertFalse(word.isMastered());
    }

    @Test
    void should_be_mastered_when_sm_repetitions_reach_threshold() {
        Word word = new Word("ou seja", "c'est à dire", 3, null, 8, 2.5f, 1);
        Assertions.assertTrue(word.isMastered());
    }

    @Test
    void should_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null, 0, 2.5f, 1);
        Word word2 = new Word("ou seja", "c'est à dire", 1, null, 0, 2.5f, 1);
        Assertions.assertTrue(word1.isSimilarTo(word2));
    }

    @Test
    void should_not_be_similar() {
        Word word1 = new Word("ou seja", "c'est à dire", 0, null, 0, 2.5f, 1);
        Word word2 = new Word("conferir", "confirmer, vérifier", 0, null, 0, 2.5f, 1);
        Assertions.assertFalse(word1.isSimilarTo(word2));
    }

    @Test
    void should_reset() {
        Word word = new Word("ou seja", "c'est à dire", 5, LocalDateTime.now(), 1, 2.5f, 3);
        Word actual = word.reset();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 0, null, 0, 2.5f, 1), actual);
    }

    @Test
    void should_check_a_not_learned_word() {
        Word word = new Word("ou seja", "c'est à dire", 1, null, 0, 2.5f, 1);
        Word actual = word.checked();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 2, null, 0, 2.5f, 1), actual);
    }

    @Test
    void should_check_a_word_to_learn() {
        Word word = new Word("ou seja", "c'est à dire", 2, null, 0, 2.5f, 1);
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, learnedMoment, 1, 2.5f, 1), actual);
    }

    @Test
    void should_increment_sm_repetitions_on_correct_review() {
        Word word = new Word("ou seja", "c'est à dire", 3, LocalDateTime.MIN, 2, 2.5f, 1);
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, learnedMoment, 3, 2.5f, 1), actual);
    }

    @Test
    void should_update_next_review_on_sm_repetition() {
        var oldNextReview = LocalDateTime.now();
        Word word = new Word("ou seja", "c'est à dire", 3, oldNextReview, 1, 2.5f, 1);
        var newNextReview = LocalDateTime.now();
        Word actual = word.checked(newNextReview);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, newNextReview, 2, 2.5f, 1), actual);
    }

}
