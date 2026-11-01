const BASE_URL = '/reminder/word';

const parse = async (response) => {
    if (!response.ok) {
        throw new Error(await response.text());
    }
    return response.json();
};

export const createApi = (fetchFn = (...args) => fetch(...args)) => ({
    fetchRemaining: async () => parse(await fetchFn(`${BASE_URL}/remaining`)),

    fetchNextWord: async () => (await parse(await fetchFn(`${BASE_URL}/next`))).value,

    checkTranslation: async (wordToLearn, proposedTranslation) =>
        parse(await fetchFn(`${BASE_URL}/check`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({proposedTranslation, wordToLearn}),
        })),
});
