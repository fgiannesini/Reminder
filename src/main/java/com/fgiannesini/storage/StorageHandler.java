package com.fgiannesini.storage;

import com.fgiannesini.Word;

import java.time.LocalDateTime;
import java.util.List;

public interface StorageHandler {
    List<WordKey> loadKeys();

    void save(List<Word> words);

    Word find(String wordToLearn);

    void update(Word word);

    List<Word> getNextWords(int limit, LocalDateTime localDateTime);

    void delete(List<WordKey> words);

    long getRemainingWordsCountToLearn();

    long getRemainingWordsCountToConfirm();
}
