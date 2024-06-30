package com.fgiannesini;

import java.util.List;
import java.util.random.RandomGenerator;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private final List<Word> words;

    public Dictionary(RandomGenerator randomProvider, List<Word> words) {
        this.randomProvider = randomProvider;
        this.words = words;
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }

}
