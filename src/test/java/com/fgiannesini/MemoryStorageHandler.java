package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.storage.WordKey;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemoryStorageHandler implements StorageHandler {

    private final List<Word> words;
    private Word updatedWord;

    public MemoryStorageHandler(Word... words) {
        this.words = Arrays.stream(words).collect(Collectors.toList());
    }

    public List<Word> getWords() {
        return this.words;
    }

    @Override
    public List<WordKey> loadKeys() {
        return this.words.stream()
                .map(w -> new WordKey(w.wordToLearn(), w.translation()))
                .toList();
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
    public void delete(List<WordKey> keys) {
        Set<String> toRemove = keys.stream().map(WordKey::wordToLearn).collect(Collectors.toSet());
        this.words.removeIf(w -> toRemove.contains(w.wordToLearn()));
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
