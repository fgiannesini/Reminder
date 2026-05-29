package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.time.LocalDateTime;
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
    public List<Word> getNextWords(int limit, LocalDateTime localDateTime) {
        return this.words.stream()
                .filter(w -> !w.isMastered())
                .filter(w -> w.smRepetition().nextReview() == null || !w.smRepetition().nextReview().isAfter(localDateTime))
                .toList();
    }

    @Override
    public void delete(List<Word> word) {
        this.words.removeAll(word);
    }

    @Override
    public long getRemainingWordsCountToLearn() {
        return this.words.stream().filter(Word::isLearningPhase).count();
    }

    @Override
    public long getRemainingWordsCountToConfirm() {
        return this.words.stream().filter(Word::isInConfirmationPhase).count();
    }

    public Word getUpdatedWord() {
        return updatedWord;
    }
}
