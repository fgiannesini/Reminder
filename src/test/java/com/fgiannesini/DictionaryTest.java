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

}