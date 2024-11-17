package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.util.Arrays;
import java.util.List;

public class MemoryStorageHandler implements StorageHandler {

    private List<Word> words;
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
        this.words = words;
        saveCalls++;
    }

    @Override
    public Word find(String wordToLearn) {
        return null;
    }

    @Override
    public void update(Word word) {

    }

    @Override
    public List<Word> getNextWords(int limit) {
        return this.words;
    }

    public List<Word> getAllWords() {
        return this.words;
    }
    public int saveCallsCount() {
        return saveCalls;
    }
}
