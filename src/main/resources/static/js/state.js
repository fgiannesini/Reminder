const FEEDBACK_CLASSES = {
    MATCHED: 'matched',
    NOT_MATCHED: 'not-matched',
    CLOSED: 'closed',
};

export const ALL_FEEDBACK_CLASSES = Object.values(FEEDBACK_CLASSES);

export const feedbackClass = (matching) => FEEDBACK_CLASSES[matching] ?? null;

const plural = (count) => (count > 1 ? 's' : '');

export const formatCountToLearn = (count) => `${count} mot${plural(count)} à apprendre.`;

export const formatCountToConfirm = (count) => `${count} mot${plural(count)} en cours de maîtrise.`;

export const formatRealTranslation = (translation) => `«${translation}»`;
