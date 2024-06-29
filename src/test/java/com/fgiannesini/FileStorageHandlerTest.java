package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class FileStorageHandlerTest {

    @Test
    void should_load_from_file(@TempDir Path tempDir) throws URISyntaxException, IOException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary-for-test.csv").toURI());

        var storageHandler = new FileStorageHandler(tempDir);
        List<Word> wordList = storageHandler.load(path);

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire")
        );

        Assertions.assertEquals(expected, wordList);
        File[] files = tempDir.toFile().listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);
        File file = files[0];
        List<String> elements = Files.readAllLines(file.toPath());
        String actual = String.join("\n", elements);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de
                ou seja;c'est à dire""");
    }

}