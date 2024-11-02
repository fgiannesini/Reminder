package com.fgiannesini.storage;

import com.fgiannesini.Word;

import java.io.IOException;
import java.util.List;

public interface StorageHandler {
    List<Word> load(List<Word> originalWords) throws IOException;

    void save(List<Word> words) throws IOException;
}
