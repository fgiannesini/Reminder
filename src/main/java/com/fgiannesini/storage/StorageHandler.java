package com.fgiannesini.storage;

import com.fgiannesini.Word;

import java.time.LocalDateTime;
import java.util.List;

public interface StorageHandler {
    List<Word> load();

    void save(List<Word> words);

    Word find(String wordToLearn);

    void update(Word word);

    List<Word> getNextWords(int limit, LocalDateTime localDateTime);

    void delete(List<Word> word);

    long getRemainingWordsCountToLearn();

    long getRemainingWordsCountToConfirm();
}
