package com.fgiannesini;

import com.opencsv.bean.CsvBindByPosition;

public class CsvWord {

    @CsvBindByPosition(position = 0)
    private String word;
    @CsvBindByPosition(position = 1)
    private String translation;

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Word toWord() {
        return new Word(word, translation);
    }
}
