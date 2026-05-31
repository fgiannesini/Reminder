package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
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
                .flatMap(word -> word.wordToLearn().equals(word.translation())
                        ? Stream.of(word)
                        : Stream.of(word, buildDuplicate(word)))
                .toList();
    }

    private static Word buildDuplicate(Word word) {
        return new Word(word.translation(), word.wordToLearn(), word.checkedCount(), SmRepetition.DEFAULT);
    }

    private static String key(String wordToLearn, String translation) {
        return wordToLearn + "|" + translation;
    }

    public void load(List<Word> originalWords) {
        var existingKeys = storageHandler.loadKeys();

        var existingKeySet = existingKeys.stream()
                .map(wk -> key(wk.wordToLearn(), wk.translation()))
                .collect(Collectors.toSet());
        var wordsToAdd = originalWords.stream()
                .filter(w -> !existingKeySet.contains(key(w.wordToLearn(), w.translation()))
                        && !existingKeySet.contains(key(w.translation(), w.wordToLearn())))
                .toList();
        storageHandler.save(addDuplicates(wordsToAdd));

        var originalKeySet = originalWords.stream()
                .flatMap(w -> Stream.of(key(w.wordToLearn(), w.translation()), key(w.translation(), w.wordToLearn())))
                .collect(Collectors.toSet());
        var wordsToRemove = existingKeys.stream()
                .filter(wk -> !originalKeySet.contains(key(wk.wordToLearn(), wk.translation())))
                .toList();
        storageHandler.delete(wordsToRemove);
    }

    public Word next() {
        var candidates = storageHandler.getNextWords(SELECTION_WINDOW, LocalDateTime.now());
        if (candidates.isEmpty()) {
            throw new NoSuchElementException("No eligible words available");
        }
        var filtered = candidates.stream()
                .filter(w -> !recentWordsWindow.contains(w.translation()))
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
