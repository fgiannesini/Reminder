package com.fgiannesini;

public record Word(String portugues, String french) {
    public boolean isFrench(String translation) {
        String cleanTransaction = translation.toLowerCase().replaceAll("[^a-z]", "");
        String cleanFrench = french.toLowerCase().replaceAll("[^a-z]", "");
        return cleanTransaction.equals(cleanFrench);
    }
}
