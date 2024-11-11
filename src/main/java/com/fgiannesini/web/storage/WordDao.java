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
    private LocalDateTime learntMoment;

    public WordDao(
            String word,
            String translation,
            int checkedCount,
            LocalDateTime learntMoment
    ) {
        this.word = word;
        this.translation = translation;
        this.checkedCount = checkedCount;
        this.learntMoment = learntMoment;
    }

    public WordDao() {
    }

    public static WordDao fromWord(Word word) {
        return new WordDao(word.wordToLearn(), word.translation(), word.checkedCount(), word.learnedMoment());
    }

    public Word toWord() {
        return new Word(word, translation, checkedCount, learntMoment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordDao wordDao = (WordDao) o;
        return checkedCount == wordDao.checkedCount &&
                Objects.equals(word, wordDao.word) &&
                Objects.equals(translation, wordDao.translation) &&
                Objects.equals(learntMoment, wordDao.learntMoment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, translation, checkedCount, learntMoment);
    }
}
