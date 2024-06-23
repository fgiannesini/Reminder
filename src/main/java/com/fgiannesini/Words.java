package com.fgiannesini;

import java.util.Arrays;
import java.util.Iterator;

public record Words(Word... words) {
    public Iterator<Word> iterator() {
        return Arrays.stream(words).iterator();
    }
}
