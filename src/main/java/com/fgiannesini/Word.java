package com.fgiannesini;

import java.text.Normalizer;

public record Word(String portugues, String french) {
    public boolean isFrench(String translation) {
        String cleanTransaction = translation.toLowerCase().replaceAll("[^a-z]", "");
        String cleanFrench = french.toLowerCase().replaceAll("[^a-z]", "");
        return cleanTransaction.equals(cleanFrench);
    }

    public Matching isFrenchMatching(String translation) {
        String cleanTransaction = translation.toLowerCase().replaceAll("[^a-z]", "");
        String cleanFrench = french.toLowerCase().replaceAll("[^a-z]", "");
        if (cleanFrench.equals(cleanTransaction)) {
            return Matching.MATCHED;
        } else if (Normalizer.normalize(cleanFrench, Normalizer.Form.NFKD).equals(Normalizer.normalize(cleanTransaction, Normalizer.Form.NFKD))) {
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
