import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {loadPage} from './page.js';

const byId = (id) => document.getElementById(id);
const json = (body) => ({ok: true, json: async () => body});
const failure = (text) => ({ok: false, text: async () => text});
const flush = () => new Promise((resolve) => setTimeout(resolve, 0));
const pressEnter = () => document.dispatchEvent(new KeyboardEvent('keydown', {key: 'Enter', bubbles: true, cancelable: true}));

const routes = {
    '/reminder/word/remaining': () => json({countToLearn: 2, countToConfirm: 1}),
    '/reminder/word/next': () => json({value: 'dog'}),
    '/reminder/word/check': () => json({matching: 'MATCHED', realTranslation: 'chien', learned: true}),
};

const stubFetch = (overrides = {}) => {
    const handlers = {...routes, ...overrides};
    const fetchMock = vi.fn(async (url) => handlers[url]());
    vi.stubGlobal('fetch', fetchMock);
    return fetchMock;
};

const startApp = async () => {
    vi.resetModules();
    await import('../../main/resources/static/js/main.js');
    await flush();
};

describe('main (whole page)', () => {
    beforeEach(() => {
        loadPage();
        vi.stubGlobal('matchMedia', () => ({matches: false, addEventListener: () => {}}));
    });

    afterEach(() => {
        vi.unstubAllGlobals();
    });

    it('loads the stats and the first word on start', async () => {
        stubFetch();

        await startApp();

        expect(byId('word-to-learn-span').textContent).toBe('dog');
        expect(byId('remaining-words-count-to-learn-span').textContent).toBe('2 mots à apprendre.');
        expect(byId('remaining-words-count-to-confirm-span').textContent).toBe('1 mot en cours de maîtrise.');
        expect(document.activeElement).toBe(byId('translation-input'));
    });

    it('checks a translation and shows the feedback', async () => {
        const fetchMock = stubFetch();
        await startApp();
        byId('translation-input').value = 'chien';

        byId('submit-button').click();
        await flush();

        expect(fetchMock).toHaveBeenCalledWith('/reminder/word/check', expect.objectContaining({
            body: JSON.stringify({proposedTranslation: 'chien', wordToLearn: 'dog'}),
        }));
        expect(byId('result-feedback').classList.contains('matched')).toBe(true);
        expect(byId('real-translation-span').textContent).toBe('«chien»');
        expect(byId('learned-badge').style.display).toBe('');
        expect(byId('translation-input').disabled).toBe(false);
        expect(byId('submit-button').classList.contains('loading')).toBe(false);
    });

    it('moves to the next word with Enter once the feedback is visible', async () => {
        const fetchMock = stubFetch();
        await startApp();
        byId('submit-button').click();
        await flush();
        fetchMock.mockClear();

        pressEnter();
        await flush();

        expect(fetchMock).toHaveBeenCalledWith('/reminder/word/next');
        expect(byId('result-feedback').style.visibility).toBe('hidden');
    });

    it('does not move on with Enter before any feedback', async () => {
        const fetchMock = stubFetch();
        await startApp();
        fetchMock.mockClear();
        byId('next-button').focus();

        pressEnter();
        await flush();

        expect(fetchMock).not.toHaveBeenCalled();
    });

    it('shows the server error and re-enables the controls when the check fails', async () => {
        stubFetch({'/reminder/word/check': () => failure('check failed')});
        await startApp();

        byId('submit-button').click();
        await flush();

        expect(byId('error-span').textContent).toBe('check failed');
        expect(byId('error-span').style.display).toBe('block');
        expect(byId('translation-input').disabled).toBe(false);
        expect(byId('submit-button').classList.contains('loading')).toBe(false);
    });

    it('shows the error when the next word cannot be loaded', async () => {
        stubFetch({'/reminder/word/next': () => failure('no word')});

        await startApp();

        expect(byId('error-span').textContent).toBe('no word');
        expect(byId('next-button').disabled).toBe(false);
    });

    it('shows a network failure as an error', async () => {
        stubFetch({
            '/reminder/word/remaining': () => {
                throw new Error('offline');
            },
        });

        await startApp();

        expect(byId('error-span').textContent).toBe('offline');
    });
});
