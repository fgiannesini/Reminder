package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class DictionaryTest {

    @Test
    void should_get_next_word() {
        Dictionary dictionary = new Dictionary(new NextGenerator(), new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Assertions.assertEquals(dictionary.next(), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
    }

    @Test
    void should_load_from_file() throws URISyntaxException, IOException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary-for-test.csv").toURI());

        Dictionary actual = Dictionary.from(new NextGenerator(), path);

        Dictionary expected = new Dictionary(
                new NextGenerator(),
                List.of(
                        new Word("ao inves, em vez de", "au lieu de"),
                        new Word("ou seja", "c'est à dire")
                )
        );
        Assertions.assertEquals(expected, actual);
    }
}