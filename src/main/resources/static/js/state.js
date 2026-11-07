const FEEDBACK_CLASSES = {
    MATCHED: 'matched',
    NOT_MATCHED: 'not-matched',
    CLOSED: 'closed',
};

export const ALL_FEEDBACK_CLASSES = Object.values(FEEDBACK_CLASSES);

export const feedbackClass = (matching) => FEEDBACK_CLASSES[matching] ?? null;

export const formatCountToLearn = (count) => `${count} à apprendre`;

export const formatCountToConfirm = (count) => `${count} en maîtrise`;
