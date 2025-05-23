package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;

import java.time.LocalDate;
import java.util.List;

public class DatabaseStorageHandler implements StorageHandler {

    private final WordRepository wordRepository;

    public DatabaseStorageHandler(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public List<Word> load() {
        return wordRepository.findAll().stream().map(WordDao::toWord).toList();
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
        return wordRepository.getReferenceById(wordToLearn).toWord();
    }

    @Override
    public void update(Word word) {
        wordRepository.save(WordDao.fromWord(word));
    }

    @Override
    public List<Word> getNextWords(int limit, LocalDate now) {
        return wordRepository.getTopOrderByLearntMoment(limit, now.minusWeeks(1))
                .stream()
                .map(WordDao::toWord)
                .toList();
    }

    @Override
    public void delete(List<Word> wordsToDelete) {
        wordRepository.deleteAll(wordsToDelete.stream().map(WordDao::fromWord).toList());
    }

    @Override
    public long getRemainingWordsCountToLearn() {
        return wordRepository.countByLearntMomentIsNull();
    }

    @Override
    public long getRemainingWordsCountToConfirm() {
        return wordRepository.countByLearntCountLessThan(2);
    }
}
