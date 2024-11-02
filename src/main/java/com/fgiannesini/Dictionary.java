package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public final class Dictionary {
    private final RandomGenerator randomProvider;
    private List<Word> words;
    private final StorageHandler storageHandler;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler) {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
        this.words = List.of();
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
        return new Word(word.translation(), word.wordToLearn(), word.checkedCount(), null);
    }

    public void load(List<Word> originalWords) throws IOException {
        var existingWords = storageHandler.load(List.of());
        if (!existingWords.isEmpty()) {
            var synchronisedWords = synchronize(originalWords, existingWords);
            storageHandler.save(synchronisedWords);
            this.words = synchronisedWords;
        } else {
            var wordsWithDuplicates = addDuplicates(originalWords);
            storageHandler.save(wordsWithDuplicates);
            this.words = wordsWithDuplicates;
        }
    }

    private List<Word> synchronize(List<Word> words, List<Word> existingWords) {
        var existingWordsToKeep = existingWords.stream()
                .filter(existingWord -> words.stream().anyMatch(word -> isWordOrDuplicate(word, existingWord)))
                .toList();
        var wordsToAdd = words.stream()
                .filter(word -> existingWords.stream().noneMatch(existingWord -> isWordOrDuplicate(word, existingWord)))
                .toList();
        var wordsToAddWithDuplicates = addDuplicates(wordsToAdd);
        return Stream.concat(existingWordsToKeep.stream(), wordsToAddWithDuplicates.stream()).toList();
    }

    private boolean isWordOrDuplicate(Word word1, Word word2) {
        return word1.isSimilarTo(word2) || word1.isSimilarTo(buildDuplicate(word2));
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
