package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DictionaryTest {

    @Test
    void should_get_next_word() {
        Dictionary dictionary = new Dictionary(new NextGenerator(), List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer")));
        Assertions.assertEquals(dictionary.next(), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
    }

}