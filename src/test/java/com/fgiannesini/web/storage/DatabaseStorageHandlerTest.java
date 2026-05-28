package com.fgiannesini.web.storage;

import com.fgiannesini.SmRepetition;
import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.TestContainerIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

class DatabaseStorageHandlerTest implements TestContainerIntegrationTest {

    @Autowired
    private StorageHandler storageHandler;

    @Test
    @Transactional
    void Should_save_and_load_words() {
        var words = List.of(
                new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("ou seja", "c'est à dire", 5, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1))
        );
        this.storageHandler.save(words);

        var actual = this.storageHandler.load();
        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("ou seja", "c'est à dire", 5, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_find_a_word() {
        var word = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 0, new SmRepetition(null, 0, 2.5f, 1)), word);
    }

    @Test
    @Transactional
    void should_update_a_word() {
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)));
        var actual = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)), actual);
    }

    @Test
    @Transactional
    void should_get_next_words_ordered_by_next_review_null_first() {
        storageHandler.update(new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)));
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 2, 13, 18, 0), 1, 2.5f, 1)));

        var actual = storageHandler.getNextWords(4, LocalDateTime.of(2024, 7, 11, 0, 0));

        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)),
                new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 2, 13, 18, 0), 1, 2.5f, 1))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_exclude_mastered_words() {
        storageHandler.update(new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 8, 2.5f, 1)));
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 2, 13, 18, 0), 8, 2.5f, 1)));

        var actual = storageHandler.getNextWords(4, LocalDateTime.now());

        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_exclude_words_reviewed_within_one_week() {
        storageHandler.update(new Word("ao inves, em vez de", "au lieu de", 2, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)));
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 2, 13, 18, 0), 1, 2.5f, 1)));

        var actual = storageHandler.getNextWords(4, LocalDateTime.of(2024, 7, 9, 0, 0));

        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_delete() {
        storageHandler.delete(List.of(
                new Word("ao inves, em vez de", "au lieu de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("ou seja", "c'est à dire", 0, new SmRepetition(null, 0, 2.5f, 1))
        ));

        var actual = storageHandler.load();
        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 0, new SmRepetition(null, 0, 2.5f, 1)),
                new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_get_count_of_words_to_learn() {
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)));
        var actual = storageHandler.getRemainingWordsCountToLearn();
        Assertions.assertEquals(3, actual);
    }

    @Test
    @Transactional
    void should_get_count_of_words_to_confirm() {
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), 1, 2.5f, 1)));
        storageHandler.update(new Word("ao inves, em vez de", "au lieu de", 3, new SmRepetition(LocalDateTime.of(2024, 7, 3, 13, 18, 0), SmRepetition.MASTERY_REPETITIONS, 2.5f, 1)));
        var actual = storageHandler.getRemainingWordsCountToConfirm();
        Assertions.assertEquals(1, actual);
    }
}
