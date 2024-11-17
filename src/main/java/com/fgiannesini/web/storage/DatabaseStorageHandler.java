package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class DatabaseStorageHandler implements StorageHandler {

    private final WordRepository wordRepository;

    @Autowired
    public DatabaseStorageHandler(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public List<Word> load() throws IOException {
        return wordRepository.findAll().stream().map(WordDao::toWord).toList();
    }

    @Override
    public void save(List<Word> words) {
        var wordDaos = words.stream().map(WordDao::fromWord).toList();
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
    public List<Word> getNextWords(int limit) {
        return wordRepository.getTopOrderByLearntMoment(limit)
                .stream()
                .map(WordDao::toWord)
                .toList();
    }
}
