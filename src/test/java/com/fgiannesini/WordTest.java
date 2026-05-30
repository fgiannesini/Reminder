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
        word = word.checked(5, LocalDateTime.now());
        word = word.checked(5, LocalDateTime.now());
        var learnedMoment = LocalDateTime.now();
        word = word.checked(5, learnedMoment);
        Assertions.assertEquals(learnedMoment.plusDays(1), word.smRepetition().nextReview());
    }

    @Test
    void should_be_in_learning_phase_when_not_checked() {
        Word word = new Word("ou seja", "c'est à dire", 0, new SmRepetition(null, 0, 2.5f, 1));
        Assertions.assertTrue(word.isLearningPhase());
    }

    @Test
    void should_not_be_in_learning_phase_when_checked_enough() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(null, 0, 2.5f, 1));
        Assertions.assertFalse(word.isLearningPhase());
    }

    @Test
    void should_not_be_mastered_when_sm_repetitions_low() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 7, 2.5f, 1));
        Assertions.assertFalse(word.isMastered());
    }

    @Test
    void should_be_mastered_when_sm_repetitions_reach_threshold() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 8, 2.5f, 1));
        Assertions.assertTrue(word.isMastered());
    }

    @Test
    void should_reset_in_learning_phase() {
        Word word = new Word("ou seja", "c'est à dire", 1, new SmRepetition(null, 0, 2.5f, 1));
        Word actual = word.reset();
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 0, new SmRepetition(null, 0, 2.5f, 1)), actual);
    }

    @Test
    void should_not_reset_in_review_phase() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.now(), 1, 2.5f, 3));
        Word actual = word.reset();
        Assertions.assertSame(word, actual);
    }

    @Test
    void should_check_a_not_learned_word() {
        Word word = new Word("ou seja", "c'est à dire", 1, new SmRepetition(null, 0, 2.5f, 1));
        Word actual = word.checked(5, LocalDateTime.now());
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 2, new SmRepetition(null, 0, 2.5f, 1)), actual);
    }

    @Test
    void should_graduate_word_on_third_matched_check() {
        Word word = new Word("ou seja", "c'est à dire", 2, new SmRepetition(null, 0, 2.5f, 1));
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(5, learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(learnedMoment.plusDays(1), 1, 2.6f, 1)), actual);
    }

    @Test
    void should_graduate_word_on_third_closed_check_with_lower_ef() {
        Word word = new Word("ou seja", "c'est à dire", 2, new SmRepetition(null, 0, 2.5f, 1));
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(3, learnedMoment);
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(learnedMoment.plusDays(1), 1, 2.36f, 1)), actual);
    }

    @Test
    void should_apply_sm2_interval_and_ease_factor_on_correct_review() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 2, 2.5f, 1));
        var learnedMoment = LocalDateTime.now();
        Word actual = word.checked(5, learnedMoment);
        int expectedInterval = 3;
        float expectedEaseFactor = 2.6f;
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(learnedMoment.plusDays(expectedInterval), 3, expectedEaseFactor, expectedInterval)), actual);
    }

    @Test
    void should_apply_six_day_interval_on_second_review() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 1, 2.5f, 1));
        var now = LocalDateTime.now();
        Word actual = word.checked(5, now);
        int expectedInterval = 6;
        float expectedEaseFactor = 2.6f;
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(now.plusDays(expectedInterval), 2, expectedEaseFactor, expectedInterval)), actual);
    }

    @Test
    void should_lower_ease_factor_on_closed_match_in_review() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 2, 2.5f, 1));
        var now = LocalDateTime.now();
        Word actual = word.checked(3, now);
        int expectedInterval = 3;
        float expectedEaseFactor = 2.36f;
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(now.plusDays(expectedInterval), 3, expectedEaseFactor, expectedInterval)), actual);
    }

    @Test
    void should_penalise_ease_factor_and_reset_repetitions_on_failure_in_review() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 2, 2.5f, 1));
        var now = LocalDateTime.now();
        Word actual = word.checked(0, now);
        float expectedEaseFactor = 2.3f;
        int expectedInterval = 1;
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(now.plusDays(expectedInterval), 0, expectedEaseFactor, expectedInterval)), actual);
    }

    @Test
    void should_respond_matched_increments_count_in_learning_phase() {
        Word word = new Word("ou seja", "c'est à dire", 1, new SmRepetition(null, 0, 2.5f, 1));
        Word actual = word.respond(Matching.MATCHED, LocalDateTime.now());
        Assertions.assertEquals(2, actual.checkedCount());
    }

    @Test
    void should_respond_closed_increments_count_in_learning_phase() {
        Word word = new Word("ou seja", "c'est à dire", 1, new SmRepetition(null, 0, 2.5f, 1));
        Word actual = word.respond(Matching.CLOSED, LocalDateTime.now());
        Assertions.assertEquals(2, actual.checkedCount());
    }

    @Test
    void should_respond_not_matched_resets_count_in_learning_phase() {
        Word word = new Word("ou seja", "c'est à dire", 2, new SmRepetition(null, 0, 2.5f, 1));
        Word actual = word.respond(Matching.NOT_MATCHED, LocalDateTime.now());
        Assertions.assertEquals(0, actual.checkedCount());
    }

    @Test
    void should_respond_not_matched_applies_sm2_failure_in_review_phase() {
        Word word = new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.MIN, 1, 2.5f, 1));
        var now = LocalDateTime.now();
        Word actual = word.respond(Matching.NOT_MATCHED, now);
        Assertions.assertEquals(0, actual.smRepetition().repetitions());
        Assertions.assertEquals(now.plusDays(1), actual.smRepetition().nextReview());
    }

}
