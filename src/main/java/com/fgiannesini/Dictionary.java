package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.io.IOException;
import java.util.List;
import java.util.random.RandomGenerator;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private final List<Word> words;
    private final StorageHandler storageHandler;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler) throws IOException {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
        this.words = this.storageHandler.load();
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }

    public void update(Word word) throws IOException {
        words.remove(0);
        storageHandler.save(this.words);
    }
}
