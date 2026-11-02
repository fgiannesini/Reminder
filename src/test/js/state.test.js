import {describe, expect, it} from 'vitest';
import {
    ALL_FEEDBACK_CLASSES,
    feedbackClass,
    formatCountToConfirm,
    formatCountToLearn,
    formatRealTranslation,
} from '../../main/resources/static/js/state.js';

describe('feedbackClass', () => {
    it.each([
        ['MATCHED', 'matched'],
        ['CLOSED', 'closed'],
        ['NOT_MATCHED', 'not-matched'],
    ])('maps %s to %s', (matching, expected) => {
        expect(feedbackClass(matching)).toBe(expected);
    });

    it('returns null for an unknown value', () => {
        expect(feedbackClass('WHATEVER')).toBeNull();
    });

    it('lists every class', () => {
        expect(ALL_FEEDBACK_CLASSES).toEqual(['matched', 'not-matched', 'closed']);
    });
});

describe('formatting', () => {
    it.each([
        [0, '0 à apprendre'],
        [1, '1 à apprendre'],
        [2, '2 à apprendre'],
    ])('formats %i words to learn', (count, expected) => {
        expect(formatCountToLearn(count)).toBe(expected);
    });

    it.each([
        [1, '1 en maîtrise'],
        [5, '5 en maîtrise'],
    ])('formats %i words to confirm', (count, expected) => {
        expect(formatCountToConfirm(count)).toBe(expected);
    });

    it('wraps the real translation in guillemets', () => {
        expect(formatRealTranslation('chat')).toBe('«chat»');
    });
});
