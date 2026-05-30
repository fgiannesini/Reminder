package com.fgiannesini;

import java.time.LocalDateTime;

public record SmRepetition(LocalDateTime nextReview, int repetitions, float easeFactor, int intervalDays) {

    public static final int MASTERY_REPETITIONS = 8;
    public static final SmRepetition DEFAULT = new SmRepetition(null, 0, 2.5f, 1);

    private static final int FIRST_INTERVAL_DAYS = 1;
    private static final int SECOND_INTERVAL_DAYS = 6;
    private static final float MIN_EASE_FACTOR = 1.3f;
    private static final float EF_SUCCESS_DELTA = 0.1f;
    private static final float EF_FAILURE_PENALTY = 0.2f;

    public boolean isMastered() {
        return repetitions >= MASTERY_REPETITIONS;
    }

    public boolean isInConfirmationPhase() {
        return repetitions >= 1 && !isMastered();
    }

    public SmRepetition apply(int quality, LocalDateTime now) {
        if (quality >= 3) {
            int newRep = repetitions + 1;
            int newInterval = switch (repetitions) {
                case 0 -> FIRST_INTERVAL_DAYS;
                case 1 -> SECOND_INTERVAL_DAYS;
                default -> Math.round(intervalDays * easeFactor);
            };
            float newEF = Math.max(MIN_EASE_FACTOR, easeFactor + EF_SUCCESS_DELTA - qualityPenalty(quality));
            return new SmRepetition(now.plusDays(newInterval), newRep, newEF, newInterval);
        } else {
            float newEF = Math.max(MIN_EASE_FACTOR, easeFactor - EF_FAILURE_PENALTY);
            return new SmRepetition(now.plusDays(FIRST_INTERVAL_DAYS), 0, newEF, FIRST_INTERVAL_DAYS);
        }
    }

    public SmRepetition reset() {
        return new SmRepetition(null, 0, easeFactor, FIRST_INTERVAL_DAYS);
    }

    private float qualityPenalty(int quality) {
        int gap = 5 - quality;
        return gap * (0.08f + gap * 0.02f);
    }
}
