package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class WordsTest {

    @Test
    void should_get_next_word() {
        Words dictionary = new Words(new NextGenerator(), new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Assertions.assertEquals(dictionary.next(), new Word("desligar", "éteindre"));
        Assertions.assertEquals(dictionary.next(), new Word("acender", "allumer"));
    }

    @Test
    void should_load_from_file() throws URISyntaxException, IOException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary-for-test.csv").toURI());

        Words actual = Words.from(new NextGenerator(), path);

        Words expected = new Words(
                new NextGenerator(),
                List.of(
                        new Word("ao inves, em vez de", "au lieu de"),
                        new Word("ou seja", "c'est à dire")
                )
        );
        Assertions.assertEquals(expected, actual);
    }
}