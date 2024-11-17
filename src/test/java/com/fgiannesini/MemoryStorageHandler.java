package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryStorageHandler implements StorageHandler {

    private final List<Word> words;
    private Word updatedWord;

    public MemoryStorageHandler(Word... words) {
        this.words = Arrays.stream(words).collect(Collectors.toList());
    }

    @Override
    public List<Word> load() {
        return this.words;
    }

    @Override
    public void save(List<Word> words) {
        this.words.addAll(words);
    }

    @Override
    public Word find(String wordToLearn) {
        return null;
    }

    @Override
    public void update(Word word) {
        updatedWord = word;
    }

    @Override
    public List<Word> getNextWords(int limit) {
        return this.words;
    }

    @Override
    public void delete(List<Word> word) {
        this.words.removeAll(word);
    }

    public Word getUpdatedWord() {
        return updatedWord;
    }
}
