package com.fgiannesini;

import com.fgiannesini.storage.StorageHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DictionaryTest {

    @Test
    void should_get_next_word() throws IOException {
        StorageHandler storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler);
        Assertions.assertEquals(dictionary.next(), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
    }

    @Test
    void should_update_and_not_return_a_word_when_it_is_learned() throws IOException {
        MemoryStorageHandler storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Dictionary dictionary = new Dictionary(new NextGenerator(), storageHandler);
        dictionary.update(new Word("desligar", "éteindre", 5));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
        Assertions.assertEquals(1, storageHandler.saveCallsCount());
    }
}