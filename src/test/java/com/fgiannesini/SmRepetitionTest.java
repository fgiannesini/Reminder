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
        Assertions.assertTrue(new SmRepetition(null, 1, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_be_in_confirmation_phase_when_sm_repetitions_below_mastery() {
        Assertions.assertTrue(new SmRepetition(null, 7, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_not_be_in_confirmation_phase_when_mastered() {
        Assertions.assertFalse(new SmRepetition(null, SmRepetition.MASTERY_REPETITIONS, 2.5f, 1).isInConfirmationPhase());
    }

    @Test
    void should_increment_repetitions_and_set_next_review() {
        var sm = new SmRepetition(null, 2, 2.5f, 3);
        var moment = LocalDateTime.now();
        var result = sm.increment(moment);
        Assertions.assertEquals(new SmRepetition(moment, 3, 2.5f, 3), result);
    }

    @Test
    void should_reset_repetitions_and_next_review_but_preserve_ease_factor() {
        var sm = new SmRepetition(LocalDateTime.now(), 5, 1.8f, 7);
        var result = sm.reset();
        Assertions.assertEquals(new SmRepetition(null, 0, 1.8f, 1), result);
    }
}
