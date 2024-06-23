package com.fgiannesini;

public record Word(String portugues, String french) {
    public boolean isFrench(String translation) {
        return translation.equals(french);
    }
}
