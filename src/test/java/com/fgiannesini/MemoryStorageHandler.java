package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.util.Arrays;
import java.util.List;

class MemoryStorageHandler implements StorageHandler {

    private final List<Word> words;

    public MemoryStorageHandler(Word... words) {
        this.words = Arrays.asList(words);
    }

    @Override
    public List<Word> load() {
        return this.words;
    }
}
