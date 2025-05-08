package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private final StorageHandler storageHandler;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler) {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
    }

    private static List<Word> addDuplicates(List<Word> words) {
        return words.stream()
                .flatMap(word -> Stream.of(
                        word,
                        buildDuplicate(word)
                ))
                .toList();
    }

    private static Word buildDuplicate(Word word) {
        return new Word(word.translation(), word.wordToLearn(), word.checkedCount(), null, 0);
    }

    public void load(List<Word> originalWords) {
        var existingWords = storageHandler.load();
        var wordsToAdd = originalWords.stream()
                .filter(word -> existingWords.stream().noneMatch(existingWord -> isWordOrDuplicate(word, existingWord)))
                .toList();
        var wordsToAddWithDuplicates = addDuplicates(wordsToAdd);
        storageHandler.save(wordsToAddWithDuplicates);
        var wordsToRemove = existingWords.stream()
                .filter(existingWord -> originalWords.stream().noneMatch(word -> isWordOrDuplicate(word, existingWord)))
                .toList();
        storageHandler.delete(wordsToRemove);
    }

    private boolean isWordOrDuplicate(Word word1, Word word2) {
        return word1.isSimilarTo(word2) || word1.isSimilarTo(buildDuplicate(word2));
    }

    public Word next(int limit) {
        var eligibleWords = storageHandler.getNextWords(limit);
        return eligibleWords.get(randomProvider.nextInt(eligibleWords.size()));
    }

    public void update(Word newWord) {
        storageHandler.update(newWord);
    }

    public Word find(String wordToLearn) {
        return storageHandler.find(wordToLearn);
    }

    public long remainingWordsCountToLearn() {
        return storageHandler.getRemainingWordsCountToLearn();
    }

    public RemainingStats remainingStats() {
        return new RemainingStats(
                storageHandler.getRemainingWordsCountToLearn(),
                storageHandler.getRemainingWordsCountToConfirm()
        );
    }
}
