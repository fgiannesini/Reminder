package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "word")
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
        return new WordDao(word.wordToLearn(), word.translation(), word.checkedCount(), word.nextReview(), word.smRepetitions(), word.easeFactor(), word.intervalDays());
    }

    public Word toWord() {
        float ef = easeFactor == 0f ? Word.DEFAULT_EASE_FACTOR : easeFactor;
        int interval = intervalDays == 0 ? 1 : intervalDays;
        return new Word(word, translation, checkedCount, nextReview, smRepetitions, ef, interval);
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
