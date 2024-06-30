package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.io.IOException;
import java.util.List;
import java.util.random.RandomGenerator;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private final List<Word> words;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler) throws IOException {
        this.randomProvider = randomProvider;
        this.words = storageHandler.load();
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }

}
