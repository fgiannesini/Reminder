package com.fgiannesini;

import java.text.Normalizer;
import java.util.Arrays;

public record Word(String word, String translation, int checkedCount) {

    public Word(String word, String translation) {
        this(word, translation, 0);
    }

    private static String cleanPunctuationAndSpaces(String string) {
        return string.toLowerCase().replaceAll("[^\\p{L}]", "");
    }

    private static boolean isClosed(String cleanedWord, String cleanedTranslation) {
        String normalizedWord = cleanAccents(cleanedWord);
        String normalizedTranslation = cleanAccents(cleanedTranslation);
        return normalizedWord.equals(normalizedTranslation);
    }

    private static String cleanAccents(String toClean) {
        return Normalizer.normalize(toClean, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }

    public Matching getMatching(String input) {
        String[] splitTranslations = translation.split(",");
        String[] inputs = input.split(",");
        var matchings = Arrays.stream(splitTranslations)
                .flatMap(translationToCheck ->
                        Arrays.stream(inputs)
                                .map(inputToCheck -> getMatching(translationToCheck, inputToCheck))
                ).toList();
        return Matching.from(matchings);
    }

    private Matching getMatching(String wordToCheck, String inputToCheck) {
        String cleanedWord = cleanPunctuationAndSpaces(wordToCheck);
        String cleanedInput = cleanPunctuationAndSpaces(inputToCheck);
        if (cleanedInput.equals(cleanedWord)) return Matching.MATCHED;
        if (isClosed(cleanedInput, cleanedWord)) return Matching.CLOSED;
        return Matching.NOT_MATCHED;
    }

    public boolean isLearned() {
        return checkedCount == 5;
    }

    public Word reset() {
        return new Word(word, translation, 0);
    }

    public Word checked() {
        return new Word(word, translation, checkedCount + 1);
    }

    public boolean isSimilarTo(Word word) {
        return word.translation.equals(this.translation) && word.word.equals(this.word);
    }
}
