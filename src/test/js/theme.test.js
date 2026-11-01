import {beforeEach, describe, expect, it} from 'vitest';
import {applySystemTheme} from '../../main/resources/static/js/theme.js';

const fakeWindow = (dark) => {
    const listeners = [];
    const query = {
        matches: dark,
        addEventListener: (_, listener) => listeners.push(listener),
    };
    return {
        matchMedia: () => query,
        switchTo: (value) => {
            query.matches = value;
            listeners.forEach((listener) => listener());
        },
    };
};

describe('applySystemTheme', () => {
    beforeEach(() => {
        document.body.className = '';
    });

    it('applies the dark class when the system is dark', () => {
        applySystemTheme(document, fakeWindow(true));

        expect(document.body.classList.contains('dark')).toBe(true);
        expect(document.body.classList.contains('light')).toBe(false);
    });

    it('applies the light class when the system is light', () => {
        applySystemTheme(document, fakeWindow(false));

        expect(document.body.classList.contains('light')).toBe(true);
        expect(document.body.classList.contains('dark')).toBe(false);
    });

    it('follows later system changes', () => {
        const win = fakeWindow(false);
        applySystemTheme(document, win);

        win.switchTo(true);

        expect(document.body.classList.contains('dark')).toBe(true);
        expect(document.body.classList.contains('light')).toBe(false);
    });
});
