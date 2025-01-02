package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.TestContainerIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

class DatabaseStorageHandlerTest extends TestContainerIntegrationTest {

    @Autowired
    private StorageHandler storageHandler;

    @Test
    @Transactional
    void Should_save_and_load_words() throws IOException {
        var words = List.of(
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0)
                ));
        this.storageHandler.save(words);

        var actual = this.storageHandler.load();
        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 3, null),
                new Word("c'est à dire", "ou seja", 3, null),
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_find_a_word() {
        var word = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, null), word);
    }

    @Test
    @Transactional
    void should_update_a_word() throws IOException {
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 3, 13, 18, 0)));
        var actual = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 3, 13, 18, 0)), actual);
    }

    @Test
    @Transactional
    void should_get_next_words_ordered_by_learnt_moment_null_first() throws IOException {
        storageHandler.update(new Word("ao inves, em vez de", "au lieu de", 2, LocalDateTime.of(2024, 7, 3, 13, 18, 0)));
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 2, 13, 18, 0)));

        var actual = storageHandler.getNextWords(4);

        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 3, null),
                new Word("c'est à dire", "ou seja", 3, null),
                new Word("ao inves, em vez de", "au lieu de", 2, LocalDateTime.of(2024, 7, 3, 13, 18, 0)),
                new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 2, 13, 18, 0))
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_delete() throws IOException {
        storageHandler.delete(List.of(
                new Word("ao inves, em vez de", "au lieu de", 3, null),
                new Word("ou seja", "c'est à dire", 3, null)
        ));

        var actual = storageHandler.load();
        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 3, null),
                new Word("c'est à dire", "ou seja", 3, null)
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void should_get_count_of_words_to_learn() throws IOException {
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 3, 13, 18, 0)));
        var actual = storageHandler.getRemainingWordsCountToLearn();
        Assertions.assertEquals(3, actual);
    }
}