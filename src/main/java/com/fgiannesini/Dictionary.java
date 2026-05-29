package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public final class Dictionary {
    private static final int SELECTION_WINDOW = 5;

    private final RandomGenerator randomProvider;
    private final StorageHandler storageHandler;
    private final RecentWordsWindow recentWordsWindow;

    public Dictionary(RandomGenerator randomProvider, StorageHandler storageHandler, RecentWordsWindow recentWordsWindow) {
        this.randomProvider = randomProvider;
        this.storageHandler = storageHandler;
        this.recentWordsWindow = recentWordsWindow;
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
        return new Word(word.translation(), word.wordToLearn(), word.checkedCount(), SmRepetition.DEFAULT);
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

    public Word next() {
        var candidates = storageHandler.getNextWords(SELECTION_WINDOW, LocalDateTime.now());
        if (candidates.isEmpty()) {
            throw new NoSuchElementException("No eligible words available");
        }
        var filtered = candidates.stream()
                .filter(w -> !recentWordsWindow.containsTranslation(w.translation()))
                .toList();
        if (!filtered.isEmpty()) candidates = filtered;
        var word = candidates.get(randomProvider.nextInt(candidates.size()));
        recentWordsWindow.add(word.wordToLearn());
        return word;
    }

    public void update(Word newWord) {
        storageHandler.update(newWord);
    }

    public Word find(String wordToLearn) {
        return storageHandler.find(wordToLearn);
    }

    public RemainingStats remainingStats() {
        return new RemainingStats(
                storageHandler.getRemainingWordsCountToLearn(),
                storageHandler.getRemainingWordsCountToConfirm()
        );
    }
}
