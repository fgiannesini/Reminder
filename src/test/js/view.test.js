import {beforeEach, describe, expect, it, vi} from 'vitest';
import {createView} from '../../main/resources/static/js/view.js';
import {loadPage} from './page.js';

const byId = (id) => document.getElementById(id);
const keydown = (key) => new KeyboardEvent('keydown', {key, bubbles: true, cancelable: true});

describe('view', () => {
    let view;

    beforeEach(() => {
        loadPage();
        view = createView(document);
    });

    describe('errors', () => {
        it('shows then clears an error', () => {
            view.showError('oops');
            expect(byId('error-span').style.display).toBe('block');
            expect(byId('error-span').textContent).toBe('oops');

            view.clearError();
            expect(byId('error-span').style.display).toBe('none');
            expect(byId('error-span').textContent).toBe('');
        });
    });

    describe('stats', () => {
        it('shows a placeholder while loading', () => {
            view.setStatsLoading();
            expect(byId('remaining-words-count-to-learn-span').textContent).toBe('…');
            expect(byId('remaining-words-count-to-confirm-span').textContent).toBe('…');
        });

        it('formats the counts', () => {
            view.setStats({countToLearn: 1, countToConfirm: 2});
            expect(byId('remaining-words-count-to-learn-span').textContent).toBe('1 à apprendre');
            expect(byId('remaining-words-count-to-confirm-span').textContent).toBe('2 en maîtrise');
        });
    });

    describe('showWord', () => {
        it('displays the word, resets the input and the feedback, and focuses the input', () => {
            view.showFeedback({matching: 'MATCHED', realTranslation: 'chien', learned: false});
            byId('translation-input').value = 'old';

            view.showWord('dog');

            expect(view.getWord()).toBe('dog');
            expect(view.getProposedTranslation()).toBe('');
            expect(view.isFeedbackVisible()).toBe(false);
            expect(byId('result-feedback').classList.contains('matched')).toBe(false);
            expect(document.activeElement).toBe(byId('translation-input'));
        });
    });

    describe('showFeedback', () => {
        it.each([
            ['MATCHED', 'matched'],
            ['CLOSED', 'closed'],
            ['NOT_MATCHED', 'not-matched'],
        ])('applies the %s style', (matching, cssClass) => {
            view.showFeedback({matching, realTranslation: 'chien', learned: false});

            expect(view.isFeedbackVisible()).toBe(true);
            expect(byId('result-feedback').classList.contains(cssClass)).toBe(true);
        });

        it('replaces the previous state style', () => {
            view.showFeedback({matching: 'MATCHED', realTranslation: 'a', learned: false});
            view.showFeedback({matching: 'NOT_MATCHED', realTranslation: 'b', learned: false});

            expect(byId('result-feedback').classList.contains('matched')).toBe(false);
            expect(byId('result-feedback').classList.contains('not-matched')).toBe(true);
        });

        it('displays the translation and focuses the next button', () => {
            view.showFeedback({matching: 'MATCHED', realTranslation: 'chien', learned: false});

            expect(byId('real-translation-span').textContent).toBe('«chien»');
            expect(document.activeElement).toBe(byId('next-button'));
        });

        it('shows the learned badge only when learned', () => {
            view.showFeedback({matching: 'MATCHED', realTranslation: 'a', learned: true});
            expect(byId('learned-badge').style.display).toBe('');

            view.showFeedback({matching: 'MATCHED', realTranslation: 'a', learned: false});
            expect(byId('learned-badge').style.display).toBe('none');
        });
    });

    describe('loading states', () => {
        it('toggles the submit loading class', () => {
            view.setSubmitLoading(true);
            expect(byId('submit-button').classList.contains('loading')).toBe(true);
            view.setSubmitLoading(false);
            expect(byId('submit-button').classList.contains('loading')).toBe(false);
        });

        it('disables the next button while loading', () => {
            view.setNextLoading(true);
            expect(view.isNextEnabled()).toBe(false);
            expect(byId('next-button').classList.contains('loading')).toBe(true);

            view.setNextLoading(false);
            expect(view.isNextEnabled()).toBe(true);
        });

        it('disables the input', () => {
            view.setInputDisabled(true);
            expect(byId('translation-input').disabled).toBe(true);
        });
    });

    describe('events', () => {
        it('calls onSubmit on button click', () => {
            const handler = vi.fn();
            view.onSubmit(handler);

            byId('submit-button').click();

            expect(handler).toHaveBeenCalledTimes(1);
        });

        it('calls onSubmit on Enter in the input', () => {
            const handler = vi.fn();
            view.onSubmit(handler);

            byId('translation-input').dispatchEvent(keydown('Enter'));

            expect(handler).toHaveBeenCalledTimes(1);
        });

        it('ignores other keys in the input', () => {
            const handler = vi.fn();
            view.onSubmit(handler);

            byId('translation-input').dispatchEvent(keydown('a'));

            expect(handler).not.toHaveBeenCalled();
        });

        it('calls onNext on button click', () => {
            const handler = vi.fn();
            view.onNext(handler);

            byId('next-button').click();

            expect(handler).toHaveBeenCalledTimes(1);
        });

        it('calls the Enter handler outside the input and prevents default when it returns true', () => {
            const handler = vi.fn().mockReturnValue(true);
            view.onEnterOutsideInput(handler);
            byId('next-button').focus();
            const event = keydown('Enter');

            document.dispatchEvent(event);

            expect(handler).toHaveBeenCalledTimes(1);
            expect(event.defaultPrevented).toBe(true);
        });

        it('does not call the Enter handler while the input is focused', () => {
            const handler = vi.fn();
            view.onEnterOutsideInput(handler);
            byId('translation-input').focus();

            byId('translation-input').dispatchEvent(keydown('Enter'));

            expect(handler).not.toHaveBeenCalled();
        });
    });
});
