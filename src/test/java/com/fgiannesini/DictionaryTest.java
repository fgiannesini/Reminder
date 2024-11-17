package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

class DictionaryTest {

    @Test
    void should_create_and_store_all_words_if_not_existing() throws IOException {
        var storageHandler = new MemoryStorageHandler();
        var dictionary = new Dictionary(
                new NextGenerator(),
                storageHandler
        );
        dictionary.load(List.of(new Word("ao inves, em vez de", "au lieu de"), new Word("ou seja", "c'est à dire")));

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("au lieu de", "ao inves, em vez de"),
                new Word("ou seja", "c'est à dire"),
                new Word("c'est à dire", "ou seja"));

        Assertions.assertEquals(expected, storageHandler.load());
    }

    @Test
    void should_synchronize_words() throws IOException {
        var storageHandler = new MemoryStorageHandler(
                new Word("ao inves, em vez de", "au lieu de", 1, null),
                new Word("au lieu de", "ao inves, em vez de", 2, null),
                new Word("acender", "allumer", 0, null),
                new Word("allumer", "acender", 0, null)
        );

        var dictionary = new Dictionary(
                new NextGenerator(),
                storageHandler
        );

        dictionary.load(List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire"))
        );

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de", 1, null),
                new Word("au lieu de", "ao inves, em vez de", 2, null),
                new Word("ou seja", "c'est à dire", 3, null),
                new Word("c'est à dire", "ou seja", 3, null));
        Assertions.assertEquals(expected, storageHandler.load());
    }

    @Test
    void should_get_next_word() throws IOException {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler());
        dictionary.load(List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer")));

        Assertions.assertEquals(new Word("desligar", "éteindre"), dictionary.next(20));
        Assertions.assertEquals(new Word("éteindre", "desligar"), dictionary.next(20));
        Assertions.assertEquals(new Word("acender", "allumer"), dictionary.next(20));
        Assertions.assertEquals(new Word("allumer", "acender"), dictionary.next(20));
    }

    @Test
    void should_update() throws IOException {
        var storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"));
        var dictionary = new Dictionary(new NextGenerator(), storageHandler);

        var learnedMoment = LocalDateTime.now();
        dictionary.update(new Word("desligar", "éteindre", 3, learnedMoment));

        Assertions.assertEquals(new Word("desligar", "éteindre", 3, learnedMoment), storageHandler.getUpdatedWord());
    }

    @Test
    void should_get_first_words() {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer")));
        Assertions.assertEquals(new Word("desligar", "éteindre"), dictionary.next(1));
        Assertions.assertEquals(new Word("acender", "allumer"), dictionary.next(1));
    }
}