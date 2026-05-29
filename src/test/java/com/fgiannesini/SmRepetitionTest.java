package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SmRepetitionTest {

    @Test
    void should_not_be_in_confirmation_phase_when_sm_repetitions_zero() {
        Assertions.assertFalse(SmRepetition.DEFAULT.isInConfirmationPhase());
    }

    @Test
    void should_be_in_confirmation_phase_when_sm_repetitions_one() {
        Assertions.assertTrue(new SmRepetition(LocalDateTime.MIN, 1, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_be_in_confirmation_phase_when_sm_repetitions_below_mastery() {
        Assertions.assertTrue(new SmRepetition(LocalDateTime.MIN, 7, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_not_be_in_confirmation_phase_when_mastered() {
        Assertions.assertFalse(new SmRepetition(LocalDateTime.MIN, SmRepetition.MASTERY_REPETITIONS, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_apply_quality_5_on_first_review() {
        var sm = new SmRepetition(null, 0, 2.5f, 1);
        var now = LocalDateTime.now();
        var result = sm.apply(5, now);
        Assertions.assertEquals(new SmRepetition(now.plusDays(1), 1, 2.6f, 1), result);
    }

    @Test
    void should_apply_quality_5_on_second_review() {
        var sm = new SmRepetition(LocalDateTime.MIN, 1, 2.5f, 1);
        var now = LocalDateTime.now();
        var result = sm.apply(5, now);
        Assertions.assertEquals(new SmRepetition(now.plusDays(6), 2, 2.6f, 6), result);
    }

    @Test
    void should_apply_quality_5_on_third_review_using_adaptive_interval() {
        float easeFactor = 2.6f;
        var sm = new SmRepetition(LocalDateTime.MIN, 2, easeFactor, 6);
        var now = LocalDateTime.now();
        var result = sm.apply(5, now);
        int expectedInterval = 16;
        float expectedEaseFactor = easeFactor + 0.1f;
        Assertions.assertEquals(new SmRepetition(now.plusDays(expectedInterval), 3, expectedEaseFactor, expectedInterval), result);
    }

    @Test
    void should_apply_quality_5_on_fourth_review_using_adaptive_interval() {
        float easeFactor = 2.6f + 0.1f;
        var sm = new SmRepetition(LocalDateTime.MIN, 3, easeFactor, 16);
        var now = LocalDateTime.now();
        var result = sm.apply(5, now);
        int expectedInterval = 43;
        float expectedEaseFactor = easeFactor + 0.1f;
        Assertions.assertEquals(new SmRepetition(now.plusDays(expectedInterval), 4, expectedEaseFactor, expectedInterval), result);
    }

    @Test
    void should_apply_failure_penalises_ease_factor_and_resets_repetitions() {
        var sm = new SmRepetition(LocalDateTime.MIN, 2, 2.5f, 1);
        var now = LocalDateTime.now();
        var result = sm.apply(0, now);
        Assertions.assertEquals(new SmRepetition(now.plusDays(1), 0, 2.3f, 1), result);
    }

    @Test
    void should_reset_repetitions_and_next_review_but_preserve_ease_factor() {
        var sm = new SmRepetition(LocalDateTime.now(), 5, 1.8f, 7);
        var result = sm.reset();
        Assertions.assertEquals(new SmRepetition(null, 0, 1.8f, 1), result);
    }
}
