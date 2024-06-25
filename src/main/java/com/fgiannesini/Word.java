package com.fgiannesini;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

public record Word(String portugues, String french) {
    public boolean isFrench(String translation) {
        String cleanTransaction = translation.toLowerCase().replaceAll("[^a-z]", "");
        String cleanFrench = french.toLowerCase().replaceAll("[^a-z]", "");
        return cleanTransaction.equals(cleanFrench);
    }

    private static String cleanPunctuationAndSpaces(String string) {
        return string.toLowerCase().replaceAll("[^\\p{L}]", "");
    }

    private static boolean isClosed(String cleanFrench, String cleanTransaction) {
        String normalized = cleanAccents(cleanFrench);
        String normalizedTranslation = cleanAccents(cleanTransaction);
        return normalized.equals(normalizedTranslation);
    }

    private static String cleanAccents(String cleanFrench) {
        return Normalizer.normalize(cleanFrench, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }

    public Matching isFrenchMatching(String translation) {
        List<Matching> list = Arrays.stream(french.split(",")).map(word -> getMatching(translation, word)).toList();
        if (list.stream().anyMatch(match -> match == Matching.MATCHED)) {
            return Matching.MATCHED;
        } else if (list.stream().anyMatch(match -> match == Matching.CLOSED)) {
            return Matching.CLOSED;
        } else return Matching.NOT_MATCHED;
    }

    private Matching getMatching(String translation, String word) {
        String cleanTransaction = cleanPunctuationAndSpaces(translation);
        String cleanFrench = cleanPunctuationAndSpaces(word);
        if (cleanFrench.equals(cleanTransaction)) {
            return Matching.MATCHED;
        } else if (isClosed(cleanFrench, cleanTransaction)) {
            return Matching.CLOSED;
        }
        return Matching.NOT_MATCHED;
    }

    public enum Matching {
        MATCHED,
        CLOSED,
        NOT_MATCHED
    }
}
