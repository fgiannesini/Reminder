package com.fgiannesini;

import java.util.random.RandomGenerator;

public record Words(RandomGenerator randomProvider, Word... words) {

    public Word next() {
        return words[randomProvider.nextInt(words.length)];
    }
}
