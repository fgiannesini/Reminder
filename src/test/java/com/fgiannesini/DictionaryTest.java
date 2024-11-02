package com.fgiannesini;

import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.original.OriginalDictionaryForTest;
import com.fgiannesini.storage.StorageHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

class DictionaryTest {

    @Test
    void should_get_next_word() throws IOException {
        StorageHandler storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        OriginalDictionary originalDictionary = new OriginalDictionaryForTest(new Word("desligar", "éteindre"), new Word("acender", "allumer"));

        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler, originalDictionary);

        Assertions.assertEquals(dictionary.next(20), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(20), new Word("acender", "allumer"));
    }

    @Test
    void should_update_and_return_a_learnt_word_at_the_end() throws IOException {
        MemoryStorageHandler storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        OriginalDictionary originalDictionary = new OriginalDictionaryForTest(new Word("desligar", "éteindre"), new Word("acender", "allumer"));

        var learnedMoment = LocalDateTime.now();

        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler, originalDictionary);
        dictionary.update(new Word("desligar", "éteindre", 5, learnedMoment));

        Assertions.assertEquals(dictionary.next(20), new Word("acender", "allumer"));
        Assertions.assertEquals(dictionary.next(20), new Word("desligar", "éteindre", 5, learnedMoment));
        Assertions.assertEquals(1, storageHandler.saveCallsCount());
    }

    @Test
    void should_get_first_words() throws IOException {
        StorageHandler storageHandler = new MemoryStorageHandler(
                new Word("desligar", "éteindre"),
                new Word("acender", "allumer")
        );
        OriginalDictionary originalDictionary = new OriginalDictionaryForTest(
                new Word("desligar", "éteindre"),
                new Word("acender", "allumer")
        );
        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler, originalDictionary);
        Assertions.assertEquals(dictionary.next(1), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(1), new Word("desligar", "éteindre"));
    }

    @Test
    void should_find_a_word() throws IOException {
        StorageHandler storageHandler = new MemoryStorageHandler(
                new Word("desligar", "éteindre"),
                new Word("acender", "allumer")
        );
        OriginalDictionary originalDictionary = new OriginalDictionaryForTest(
                new Word("desligar", "éteindre"),
                new Word("acender", "allumer")
        );
        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler, originalDictionary);
        Assertions.assertEquals(dictionary.find("desligar"), new Word("desligar", "éteindre"));
    }
}