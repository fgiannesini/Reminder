package com.fgiannesini;

import java.util.Arrays;
import java.util.Iterator;
import java.util.random.RandomGenerator;

public record Words(RandomGenerator randomProvider, Word... words) {

    public Iterator<Word> iterator() {
        return Arrays.stream(words).iterator();
    }

    public Word next() {
        return words[randomProvider.nextInt(words.length)];
    }
}
