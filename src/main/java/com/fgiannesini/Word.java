package com.fgiannesini;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

public record Word(String wordToLearn, String translation, int checkedCount, SmRepetition smRepetition) {

    private static final int repetitionLimitToLearn = 3;
    private static final Pattern NON_LETTER = Pattern.compile("[^\\p{L}]");
    private static final Pattern ACCENT_MARK = Pattern.compile("\\p{M}");

    public Word(String word, String translation) {
        this(word, translation, 0, SmRepetition.DEFAULT);
    }

    private static String cleanPunctuationAndSpaces(String string) {
        return NON_LETTER.matcher(string.toLowerCase()).replaceAll("");
    }

    private static boolean isClosed(String cleanedWord, String cleanedTranslation) {
        String normalizedWord = cleanAccents(cleanedWord);
        String normalizedTranslation = cleanAccents(cleanedTranslation);
        return normalizedWord.equals(normalizedTranslation);
    }

    private static String cleanAccents(String toClean) {
        return ACCENT_MARK.matcher(Normalizer.normalize(toClean, Normalizer.Form.NFKD)).replaceAll("");
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

    public boolean isLearningPhase() {
        return checkedCount < repetitionLimitToLearn;
    }

    public boolean isMastered() {
        return smRepetition.isMastered();
    }

    public boolean isInConfirmationPhase() {
        return smRepetition.isInConfirmationPhase();
    }

    public Word reset() {
        if (isLearningPhase()) {
            return new Word(wordToLearn, translation, 0, smRepetition.reset());
        }
        return this;
    }

    public Word checked(int quality, LocalDateTime now) {
        if (isLearningPhase()) {
            int newCount = checkedCount + 1;
            if (newCount == repetitionLimitToLearn) {
                return new Word(wordToLearn, translation, newCount,
                        smRepetition.apply(quality, now));
            }
            return new Word(wordToLearn, translation, newCount, smRepetition);
        }
        return new Word(wordToLearn, translation, checkedCount, smRepetition.apply(quality, now));
    }

    public Word respond(Matching matching, LocalDateTime now) {
        if (matching == Matching.NOT_MATCHED && isLearningPhase()) return reset();
        return checked(matching.quality(), now);
    }

}
