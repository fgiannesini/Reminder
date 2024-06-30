package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.io.IOException;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private List<Word> words;
    private final StorageHandler storageHandler;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler) throws IOException {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
        this.words = this.storageHandler.load();
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }

    public void update(Word newWord) throws IOException {
        this.words = Stream.concat(this.words.stream().filter(word -> !word.isSimilarTo(newWord)), Stream.of(newWord)).toList();
        storageHandler.save(this.words);
    }
}
