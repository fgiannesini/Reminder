package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.util.Arrays;
import java.util.List;

class MemoryStorageHandler implements StorageHandler {

    private final List<Word> words;
    private int saveCalls = 0;

    public MemoryStorageHandler(Word... words) {
        this.words = Arrays.asList(words);
    }

    @Override
    public List<Word> load() {
        return this.words;
    }

    @Override
    public void save(List<Word> words) {
        saveCalls++;
    }

    public int saveCallsCount() {
        return saveCalls;
    }
}
