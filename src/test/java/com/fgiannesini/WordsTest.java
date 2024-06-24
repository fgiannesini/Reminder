package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WordsTest {

    @Test
    void should_get_next_word() {
        Words dictionary = new Words(new NextGenerator(), new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Assertions.assertEquals(dictionary.next(), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
    }

}