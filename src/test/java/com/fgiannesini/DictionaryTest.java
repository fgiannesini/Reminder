package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

class DictionaryTest {

    @Test
    void should_create_and_store_all_words_if_not_existing() {
        var storageHandler = new MemoryStorageHandler();
        var dictionary = new Dictionary(new NextGenerator(), storageHandler);
        dictionary.load(List.of(new Word("ao inves, em vez de", "au lieu de"), new Word("ou seja", "c'est à dire")));

        var expected = List.of(new Word("ao inves, em vez de", "au lieu de"), new Word("au lieu de", "ao inves, em vez de"), new Word("ou seja", "c'est à dire"), new Word("c'est à dire", "ou seja"));

        Assertions.assertEquals(expected, storageHandler.load());
    }

    @Test
    void should_synchronize_words() {
        var storageHandler = new MemoryStorageHandler(new Word("ao inves, em vez de", "au lieu de", 1, new SmRepetition(null, 0, 2.5f, 1)), new Word("au lieu de", "ao inves, em vez de", 2, new SmRepetition(null, 0, 2.5f, 1)), new Word("acender", "allumer", 0, new SmRepetition(null, 1, 2.5f, 1)), new Word("allumer", "acender", 0, new SmRepetition(null, 1, 2.5f, 1)));

        var dictionary = new Dictionary(new NextGenerator(), storageHandler);

        dictionary.load(List.of(new Word("ao inves, em vez de", "au lieu de"), new Word("ou seja", "c'est à dire")));

        var expected = List.of(new Word("ao inves, em vez de", "au lieu de", 1, new SmRepetition(null, 0, 2.5f, 1)), new Word("au lieu de", "ao inves, em vez de", 2, new SmRepetition(null, 0, 2.5f, 1)), new Word("ou seja", "c'est à dire", 0, new SmRepetition(null, 0, 2.5f, 1)), new Word("c'est à dire", "ou seja", 0, new SmRepetition(null, 0, 2.5f, 1)));
        Assertions.assertEquals(expected, storageHandler.load());
    }

    @Test
    void should_get_next_word() {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler());
        dictionary.load(List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer")));

        Assertions.assertEquals(new Word("desligar", "éteindre"), dictionary.next());
        Assertions.assertEquals(new Word("éteindre", "desligar"), dictionary.next());
        Assertions.assertEquals(new Word("acender", "allumer"), dictionary.next());
        Assertions.assertEquals(new Word("allumer", "acender"), dictionary.next());
    }

    @Test
    void should_update() {
        var storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"));
        var dictionary = new Dictionary(new NextGenerator(), storageHandler);

        var learnedMoment = LocalDateTime.now();
        dictionary.update(new Word("desligar", "éteindre", 3, new SmRepetition(learnedMoment, 1, 2.5f, 1)));

        Assertions.assertEquals(new Word("desligar", "éteindre", 3, new SmRepetition(learnedMoment, 1, 2.5f, 1)), storageHandler.getUpdatedWord());
    }

    @Test
    void should_get_first_words() {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer")));
        Assertions.assertEquals(new Word("desligar", "éteindre"), dictionary.next());
        Assertions.assertEquals(new Word("acender", "allumer"), dictionary.next());
    }

    @Test
    void should_throw_exception_when_no_eligible_words() {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler());
        Assertions.assertThrows(NoSuchElementException.class, dictionary::next);
    }

    @Test
    void should_get_remaining_stats() {
        var dictionary = new Dictionary(new NextGenerator(), new MemoryStorageHandler(
                new Word("desligar", "éteindre", 3, new SmRepetition(LocalDateTime.now(), 1, 2.5f, 1)),
                new Word("acender", "allumer", 2, new SmRepetition(LocalDateTime.now(), 0, 2.5f, 1))
        ));
        Assertions.assertEquals(new RemainingStats(1, 1), dictionary.remainingStats());
    }

}
