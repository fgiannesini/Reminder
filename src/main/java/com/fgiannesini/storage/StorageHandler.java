package com.fgiannesini.storage;

import com.fgiannesini.Word;

import java.io.IOException;
import java.util.List;

public interface StorageHandler {
    List<Word> load() throws IOException;

    void save(List<Word> words) throws IOException;

    Word find(String wordToLearn);

    void update(Word word) throws IOException;

    List<Word> getNextWords(int limit);

    void delete(List<Word> word) throws IOException;

    long getRemainingWordsCountToLearn();
}
