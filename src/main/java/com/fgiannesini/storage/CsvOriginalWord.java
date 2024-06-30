package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvBindByPosition;

public class CsvOriginalWord {

    @CsvBindByPosition(position = 0)
    private String word;
    @CsvBindByPosition(position = 1)
    private String translation;

    public CsvOriginalWord() {
    }

    public Word toWord() {
        return new Word(word, translation);
    }
}
