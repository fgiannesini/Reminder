package com.fgiannesini;

import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.StorageHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private List<Word> words;
    private final StorageHandler storageHandler;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler, OriginalDictionary originalDictionary) throws IOException {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
        var originalWords = originalDictionary.load();
        this.words = this.storageHandler.load(originalWords);
    }

    public Word next(int limit) {
        var eligibleWords = words.stream().sorted(Comparator.comparing(Word::learnedMoment, Comparator.nullsFirst(Comparator.naturalOrder()))).limit(limit).toList();
        return eligibleWords.get(randomProvider.nextInt(eligibleWords.size()));
    }

    public void update(Word newWord) throws IOException {
        this.words = words.stream()
                .map(word -> word.isSimilarTo(newWord) ? newWord : word)
                .toList();
        storageHandler.save(this.words);
    }

    public Word find(String wordToLearn) {
        return words.stream()
                .filter(word -> word.wordToLearn().equals(wordToLearn))
                .findAny()
                .orElse(null);
    }
}
