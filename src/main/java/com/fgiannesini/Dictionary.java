package com.fgiannesini;

import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

public record Dictionary(RandomGenerator randomProvider, List<Word> words) {

    public Dictionary(RandomGenerator randomProvider, Word... words) {
        this(randomProvider, Arrays.stream(words).toList());
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }
}
