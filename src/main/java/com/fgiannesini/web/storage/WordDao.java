package com.fgiannesini.web.storage;

import com.fgiannesini.SmRepetition;
import com.fgiannesini.Word;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "word", indexes = @Index(name = "idx_word_sm_review", columnList = "smRepetitions, nextReview"))
public final class WordDao {
    @Id
    private String word;
    private String translation;
    private int checkedCount;
    private LocalDateTime nextReview;
    private int smRepetitions;
    private float easeFactor;
    private int intervalDays;

    public WordDao(
            String word,
            String translation,
            int checkedCount,
            LocalDateTime nextReview,
            int smRepetitions,
            float easeFactor,
            int intervalDays
    ) {
        this.word = word;
        this.translation = translation;
        this.checkedCount = checkedCount;
        this.nextReview = nextReview;
        this.smRepetitions = smRepetitions;
        this.easeFactor = easeFactor;
        this.intervalDays = intervalDays;
    }

    public WordDao() {
    }

    public static WordDao fromWord(Word word) {
        var sm = word.smRepetition();
        return new WordDao(word.wordToLearn(), word.translation(), word.checkedCount(),
                sm.nextReview(), sm.repetitions(), sm.easeFactor(), sm.intervalDays());
    }

    public Word toWord() {
        return new Word(word, translation, checkedCount,
                new SmRepetition(nextReview, smRepetitions, easeFactor, intervalDays));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WordDao wordDao = (WordDao) o;
        return Objects.equals(word, wordDao.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
