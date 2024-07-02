package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvBindByPosition;

public class CsvWord {

    @CsvBindByPosition(position = 0)
    private String word;
    @CsvBindByPosition(position = 1)
    private String translation;
    @CsvBindByPosition(position = 2)
    private int checkedCount;

    public CsvWord(String word, String translation, int checkedCount) {
        this.word = word;
        this.translation = translation;
        this.checkedCount = checkedCount;
    }

    public CsvWord() {
    }

    public static CsvWord fromWord(Word word) {
        return new CsvWord(word.word(), word.translation(), word.checkedCount());
    }

    public Word toWord() {
        return new Word(word, translation, checkedCount);
    }
}
