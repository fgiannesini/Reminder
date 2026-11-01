import {describe, expect, it, vi} from 'vitest';
import {createApi} from '../../main/resources/static/js/api.js';

const ok = (body) => ({ok: true, json: async () => body});
const ko = (text) => ({ok: false, text: async () => text});

describe('api', () => {
    it('fetches the remaining stats', async () => {
        const fetchFn = vi.fn().mockResolvedValue(ok({countToLearn: 3, countToConfirm: 4}));

        expect(await createApi(fetchFn).fetchRemaining()).toEqual({countToLearn: 3, countToConfirm: 4});
        expect(fetchFn).toHaveBeenCalledWith('/reminder/word/remaining');
    });

    it('fetches the next word value', async () => {
        const fetchFn = vi.fn().mockResolvedValue(ok({value: 'dog'}));

        expect(await createApi(fetchFn).fetchNextWord()).toBe('dog');
        expect(fetchFn).toHaveBeenCalledWith('/reminder/word/next');
    });

    it('posts the translation attempt', async () => {
        const result = {matching: 'MATCHED', realTranslation: 'chien', learned: false};
        const fetchFn = vi.fn().mockResolvedValue(ok(result));

        expect(await createApi(fetchFn).checkTranslation('dog', 'chien')).toEqual(result);
        expect(fetchFn).toHaveBeenCalledWith('/reminder/word/check', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({proposedTranslation: 'chien', wordToLearn: 'dog'}),
        });
    });

    it('throws the server message on a non-OK response', async () => {
        const api = createApi(vi.fn().mockResolvedValue(ko('boom')));

        await expect(api.fetchNextWord()).rejects.toThrow('boom');
        await expect(api.fetchRemaining()).rejects.toThrow('boom');
        await expect(api.checkTranslation('a', 'b')).rejects.toThrow('boom');
    });
});
