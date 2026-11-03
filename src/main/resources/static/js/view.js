import {
    ALL_FEEDBACK_CLASSES,
    feedbackClass,
    formatCountToConfirm,
    formatCountToLearn,
    formatRealTranslation,
} from './state.js';

export const createView = (doc = document) => {
    const byId = (id) => doc.getElementById(id);
    const el = {
        error: byId('error-span'),
        countToLearn: byId('remaining-words-count-to-learn-span'),
        countToConfirm: byId('remaining-words-count-to-confirm-span'),
        word: byId('word-to-learn-span'),
        input: byId('translation-input'),
        submit: byId('submit-button'),
        next: byId('next-button'),
        feedback: byId('result-feedback'),
        realTranslation: byId('real-translation-span'),
        learnedBadge: byId('learned-badge'),
    };

    // A single action button is shown at a time: "Valider" while answering, "Suivant" once checked
    const setStep = (checked) => {
        el.submit.hidden = checked;
        el.next.hidden = !checked;
    };

    const hideFeedback = () => {
        setStep(false);
        el.feedback.style.visibility = 'hidden';
        el.feedback.classList.remove(...ALL_FEEDBACK_CLASSES);
    };

    return {
        showError: (text) => {
            el.error.style.display = 'block';
            el.error.textContent = text;
        },
        clearError: () => {
            el.error.style.display = 'none';
            el.error.textContent = '';
        },

        setStatsLoading: () => {
            el.countToLearn.textContent = '…';
            el.countToConfirm.textContent = '…';
        },
        setStats: ({countToLearn, countToConfirm}) => {
            el.countToLearn.textContent = formatCountToLearn(countToLearn);
            el.countToConfirm.textContent = formatCountToConfirm(countToConfirm);
        },

        getWord: () => el.word.textContent,
        showWord: (word) => {
            el.word.textContent = word;
            hideFeedback();
            el.input.value = '';
            el.input.focus();
        },

        getProposedTranslation: () => el.input.value,
        setInputDisabled: (disabled) => {
            el.input.disabled = disabled;
        },
        isInputFocused: () => doc.activeElement === el.input,

        setSubmitLoading: (loading) => el.submit.classList.toggle('loading', loading),
        setNextLoading: (loading) => {
            el.next.classList.toggle('loading', loading);
            el.next.disabled = loading;
        },
        isNextEnabled: () => !el.next.disabled,

        isFeedbackVisible: () => el.feedback.style.visibility !== 'hidden',
        showFeedback: ({matching, realTranslation, learned}) => {
            setStep(true);
            el.feedback.style.visibility = 'visible';
            el.feedback.classList.remove(...ALL_FEEDBACK_CLASSES);
            const cssClass = feedbackClass(matching);
            if (cssClass) el.feedback.classList.add(cssClass);
            el.realTranslation.textContent = formatRealTranslation(realTranslation);
            el.learnedBadge.style.display = learned ? '' : 'none';
            if (el.feedback.getBoundingClientRect().bottom > window.innerHeight) {
                el.feedback.scrollIntoView({behavior: 'smooth', block: 'nearest'});
            }
            el.next.focus();
        },

        onSubmit: (handler) => {
            el.submit.addEventListener('click', handler);
            el.input.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    handler();
                }
            });
        },
        onNext: (handler) => el.next.addEventListener('click', handler),
        onEnterOutsideInput: (handler) => doc.addEventListener('keydown', (event) => {
            if (event.key !== 'Enter' || el.input === doc.activeElement) return;
            if (handler()) event.preventDefault();
        }),
    };
};
