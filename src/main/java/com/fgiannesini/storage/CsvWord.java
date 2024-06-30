package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvBindByPosition;

public class CsvWord {

    @CsvBindByPosition(position = 0)
    private String word;
    @CsvBindByPosition(position = 1)
    private String translation;

    public CsvWord(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public CsvWord() {
    }

    public Word toWord() {
        return new Word(word, translation);
    }

    public static CsvWord fromWord(Word word) {
        return new CsvWord(word.word(), word.translation());
    }
}
