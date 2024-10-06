package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

import java.time.LocalDateTime;

public class CsvWord {

    @CsvBindByPosition(position = 0)
    private String word;
    @CsvBindByPosition(position = 1)
    private String translation;
    @CsvBindByPosition(position = 2)
    private int checkedCount;
    @CsvDate
    @CsvBindByPosition(position = 3)
    private LocalDateTime learntMoment;

    public CsvWord(String word, String translation, int checkedCount, LocalDateTime learntMoment) {
        this.word = word;
        this.translation = translation;
        this.checkedCount = checkedCount;
        this.learntMoment = learntMoment;
    }

    public CsvWord() {
    }

    public static CsvWord fromWord(Word word) {
        return new CsvWord(word.wordToLearn(), word.translation(), word.checkedCount(), word.learnedMoment());
    }

    public Word toWord() {
        return new Word(word, translation, checkedCount, learntMoment);
    }
}
