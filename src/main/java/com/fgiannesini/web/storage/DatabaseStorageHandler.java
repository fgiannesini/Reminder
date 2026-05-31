package com.fgiannesini.web.storage;

import com.fgiannesini.SmRepetition;
import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.storage.WordKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class DatabaseStorageHandler implements StorageHandler {

    private final WordRepository wordRepository;

    public DatabaseStorageHandler(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public List<WordKey> loadKeys() {
        return wordRepository.findAllProjectedBy().stream()
                .map(p -> new WordKey(p.getWord(), p.getTranslation()))
                .toList();
    }

    @Override
    public void save(List<Word> words) {
        var wordDaos = words.stream()
                .map(WordDao::fromWord)
                .toList();
        wordRepository.saveAll(wordDaos);
    }

    @Override
    public Word find(String wordToLearn) {
        return wordRepository.findById(wordToLearn)
                .map(WordDao::toWord)
                .orElseThrow(() -> new NoSuchElementException("Word not found: " + wordToLearn));
    }

    @Override
    public void update(Word word) {
        wordRepository.save(WordDao.fromWord(word));
    }

    @Override
    public List<Word> getNextWords(int limit, LocalDateTime localDateTime) {
        return wordRepository.getTopOrderByNextReview(limit, localDateTime, SmRepetition.MASTERY_REPETITIONS)
                .stream()
                .map(WordDao::toWord)
                .toList();
    }

    @Override
    public void delete(List<WordKey> wordsToDelete) {
        wordRepository.deleteAllById(wordsToDelete.stream().map(WordKey::wordToLearn).toList());
    }

    @Override
    public long getRemainingWordsCountToLearn() {
        return wordRepository.countByNextReviewIsNull();
    }

    @Override
    public long getRemainingWordsCountToConfirm() {
        return wordRepository.countBySmRepetitionsGreaterThanEqualAndSmRepetitionsLessThan(1, SmRepetition.MASTERY_REPETITIONS);
    }
}
