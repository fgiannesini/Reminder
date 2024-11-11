package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.TestContainerIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

class DatabaseStorageHandlerTest implements TestContainerIntegrationTest {

    @Autowired
    private StorageHandler storageHandler;

    @Test
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
    void should_find_a_word() {
        var word = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, null), word);
    }
}