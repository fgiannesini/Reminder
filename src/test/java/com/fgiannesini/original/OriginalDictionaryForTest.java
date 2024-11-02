package com.fgiannesini.original;

import com.fgiannesini.Word;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class OriginalDictionaryForTest extends OriginalDictionary {

    private final List<Word> words;

    public OriginalDictionaryForTest(Word... words) {
        super(InputStream.nullInputStream());
        this.words = Arrays.asList(words);
    }

    @Override
    public List<Word> load() {
        return words;
    }
}
