import {createApi} from './api.js';
import {createView} from './view.js';

const api = createApi();
const view = createView();

const loadRemaining = async () => {
    view.setStatsLoading();
    try {
        view.setStats(await api.fetchRemaining());
    } catch (error) {
        view.showError(error.message);
    }
};

const loadNextWord = async () => {
    view.clearError();
    view.setNextLoading(true);
    try {
        view.showWord(await api.fetchNextWord());
    } catch (error) {
        view.showError(error.message);
    } finally {
        view.setNextLoading(false);
    }
};

const submitTranslation = async () => {
    view.clearError();
    view.setSubmitLoading(true);
    const proposedTranslation = view.getProposedTranslation();
    view.setInputDisabled(true);
    try {
        view.showFeedback(await api.checkTranslation(view.getWord(), proposedTranslation));
    } catch (error) {
        view.showError(error.message);
    } finally {
        view.setSubmitLoading(false);
        view.setInputDisabled(false);
    }
};

view.onSubmit(submitTranslation);
view.onNext(loadNextWord);
view.onEnterOutsideInput(() => {
    if (!view.isNextEnabled() || !view.isFeedbackVisible()) return false;
    loadNextWord();
    return true;
});

await Promise.all([loadRemaining(), loadNextWord()]);
