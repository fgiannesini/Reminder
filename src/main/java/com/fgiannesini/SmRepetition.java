package com.fgiannesini;

import java.time.LocalDateTime;

public record SmRepetition(LocalDateTime nextReview, int repetitions, float easeFactor, int intervalDays) {

    public static final float DEFAULT_EASE_FACTOR = 2.5f;
    public static final int MASTERY_REPETITIONS = 8;
    public static final SmRepetition DEFAULT = new SmRepetition(null, 0, DEFAULT_EASE_FACTOR, 1);

    public boolean isMastered() {
        return repetitions >= MASTERY_REPETITIONS;
    }

    public boolean isInConfirmationPhase() {
        return repetitions >= 1 && !isMastered();
    }

    public SmRepetition increment(LocalDateTime reviewMoment) {
        return new SmRepetition(reviewMoment, repetitions + 1, easeFactor, intervalDays);
    }

    public SmRepetition reset() {
        return new SmRepetition(null, 0, easeFactor, 1);
    }
}
